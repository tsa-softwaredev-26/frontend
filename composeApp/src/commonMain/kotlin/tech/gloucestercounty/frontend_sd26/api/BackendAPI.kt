package tech.gloucestercounty.frontend_sd26.api

import dev.icerock.moko.socket.Socket
import dev.icerock.moko.socket.SocketEvent
import dev.icerock.moko.socket.SocketOptions
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlin.io.encoding.Base64

// the overall api interaction object
object BaseAPI {
    // basic variables used by all functions
    private const val DEFAULT_FOCAL_LENGTH_PX = 3094
    private val client = HttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var socket: Socket
    private var lastAudioFile: String? = null
    private var awaitingImageCapture = false

    private fun encodeFile(path: String): String {
        return Base64.encode(SystemFileSystem.source(Path(path)).buffered().use { it.readByteArray() })
    }

    private fun emitAudioEvent(audioFile: String, imageFile: String? = null) {
        val payload = buildJsonObject {
            put("audio", encodeFile(audioFile))
            if (imageFile != null) {
                put("image", encodeFile(imageFile))
                put("focal_length_px", DEFAULT_FOCAL_LENGTH_PX)
            }
        }
        socket.emit("audio", payload)
    }

    // basic reusable get function
    private suspend inline fun <reified T> Get(path: String): T {
        val res = client.get("${ENV.server}$path") {
            header("X-API-Key", ENV.apiKey) // add api key header
        }
        // decide what kind of response to give based on what was passed in function call
        return when (T::class) {
            HttpResponse::class -> res
            String::class -> res.body<String>()
            JsonObject::class, JsonArray::class -> Json.parseToJsonElement(res.body())
            else -> null // if type isnt listed, also required for type safety
        } as T
    }

    // identical to get request except for commented parts
    private suspend inline fun <reified T> Req(path: String, hMethod: HttpMethod, data: JsonObject? = null): T {
        val res = client.request("${ENV.server}$path") {
            method = hMethod // specify http method manually
            header("X-API-Key", ENV.apiKey)
            if (data != null) header(HttpHeaders.ContentType, "application/json") // add content type header
            if (data != null) setBody(data.toString()) // add body data to request
        }
        return when (T::class) {
            HttpResponse::class -> res
            String::class -> res.body<String>()
            JsonObject::class, JsonArray::class -> Json.parseToJsonElement(res.body())
            else -> null
        } as T
    }

    // used to check that server is running
    suspend fun Health(): Boolean {
        return Get<HttpResponse>("/health").status.isSuccess() // return if the status code was a 200
    }

    // used to get current user settings
    suspend fun getUserSettings(): JsonObject {
        return Get<JsonObject>("/user-settings")
    }
    // used to adjust user settings
    suspend fun setUserSettings(value: JsonObject): JsonObject {
        return Req<JsonObject>("/user-settings", HttpMethod.Patch, value)
    }

    // gets list of all scanned items
    suspend fun getItems(): JsonObject {
        return Get<JsonObject>("/items")
    }
    // deletes an item
    suspend fun delItems(label: String) {
        Req<HttpResponse>("/items/$label", HttpMethod.Delete)
    }
    // renames an items label
    suspend fun renItems(old: String, new: String) {
        Req<HttpResponse>("/items/$old/rename", HttpMethod.Post, Json.parseToJsonElement("{\"new_label\": \"$new\"}") as JsonObject?)
    }

    // main loop websocket interaction
    fun WS(
        tts: (String) -> Unit,
        openCamera: () -> Unit,
        store: (String, String) -> Unit,
        error: (String, String) -> Unit
    ) {
        // start the websocket connection
        socket = Socket(
            endpoint = ENV.server,
            config = SocketOptions(
                transport = SocketOptions.Transport.WEBSOCKET,
                queryParams = mapOf("key" to ENV.apiKey)
            )
        ) {
            // when server asks for tts
            on("tts") {
                val narration = json.parseToJsonElement(it).jsonObject["narration"]?.jsonPrimitive?.contentOrNull
                if (narration != null) {
                    tts(narration)
                    println("tts running, text: $narration")
                }
            }

            // when server asks for camera to open
            on("control") {
                val data = json.parseToJsonElement(it).jsonObject
                val action = data["action"]?.jsonPrimitive?.contentOrNull
                if (action == "request_image") {
                    awaitingImageCapture = true
                    MainScope().launch {
                        openCamera()
                    }
                }
                println("opening camera")
            }

            // when server asks to store scan id for later use
            on("action_result") {
                val data = json.parseToJsonElement(it).jsonObject
                val type = data["type"]?.jsonPrimitive?.contentOrNull ?: return@on
                val scanId = data["data"]?.jsonObject?.get("scan_id")?.jsonPrimitive?.contentOrNull
                if (scanId != null) {
                    store(type, scanId)
                }
                println("storing data")
            }

            // when an error occurs
            on("error") {
                val data = json.parseToJsonElement(it).jsonObject
                val code = data["code"]?.jsonPrimitive?.contentOrNull ?: "unknown"
                val message = data["message"]?.jsonPrimitive?.contentOrNull ?: "Unknown error"
                error(code, message)
                println("ERROR $code: $message")
            }

            on("transcription") {
                println("transcription: $it")
            }

            on("session_state") {
                println("session_state: $it")
            }

            on("listening") {
                println("listening: $it")
            }

            on("listening_stopped") {
                println("listening_stopped: $it")
            }

            on(SocketEvent.Message) {
                println(it.toString())
            }
        }
        socket.connect()
    }

    // public functions for interacting with the websocket
    // NOTE: WEBSOCKET MUST ALREADY BE CREATED USING WS()
    // sends most recently recorded audio file
    fun sendAudio(file: String) {
        lastAudioFile = file
        emitAudioEvent(file)
        println("sent audio at $file")
    }

    // queues an image request and, when the backend is waiting for one, completes the
    // original voice turn by resending the last audio with the captured image attached.
    fun sendImage(file: String): Boolean {
        val audioFile = lastAudioFile
        if (awaitingImageCapture && audioFile != null) {
            emitAudioEvent(audioFile, imageFile = file)
            awaitingImageCapture = false
            println("sent image with cached audio at $file")
            return true
        }
        println("captured image at $file but backend is not awaiting one")
        return false
    }
}
