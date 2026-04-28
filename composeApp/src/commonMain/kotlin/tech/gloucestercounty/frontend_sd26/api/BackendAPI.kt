package tech.gloucestercounty.frontend_sd26.api

import dev.icerock.moko.socket.Socket
import dev.icerock.moko.socket.SocketEvent
import dev.icerock.moko.socket.SocketOptions
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlin.io.encoding.Base64

// the overall api interaction object
object BaseAPI {
    // basic variables used by all functions
    private const val URL = "https://ii7tcxfnhkcaui3pj6zb25y3ri.srv.us" // will occasionally update, looking into wiredoor instead of srv.us
    private const val API_KEY = "299785c6c2c90213cba57e443ecfee5c1f05e86da0a5579f411e41721fdc9048" // change before committing and after pulling (fake one used in some commits)
    private val client = HttpClient()
    private lateinit var socket: Socket

    // basic reusable get function
    private suspend inline fun <reified T> Get(path: String): T {
        val res = client.get("$URL$path") {
            header("X-API-Key", API_KEY) // add api key header
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
        val res = client.request("$URL$path") {
            method = hMethod // specify http method manually
            header("X-API-Key", API_KEY)
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
            endpoint = "wss://$URL/socket.io/?key=$API_KEY&EIO=4&transport=websocket",
            config = SocketOptions(
                transport = SocketOptions.Transport.WEBSOCKET,
                queryParams = null
            )
        ) {
            // when server asks for tts
            on("tts") {
                // run tts function with passed text
                tts(Json.parseToJsonElement(it).jsonObject["narration"].toString())
                println("tts running")
            }

            // when server asks for camera to open
            on("control") {
                // run the passed openCamera function
                openCamera()
                println("opening camera")
            }

            // when server asks to store scan id for later use
            on("action_result") {
                val data = Json.parseToJsonElement(it).jsonObject // get data
                store(data["type"].toString(), (data["data"] as JsonObject)["scan_id"].toString()) // run corresponding function
                println("storing data")
            }

            // when an error occurs
            on("error") {
                val data = Json.parseToJsonElement(it).jsonObject // get data
                error(data["code"].toString(), data["message"].toString()) // run corresponding function
                println("ERROR ${data["code"].toString()}: ${data["message"].toString()}")
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
        socket.emit("audio", Json.encodeToString(mapOf(
            "audio" to Base64.encode(SystemFileSystem.source(Path(file)).buffered().use { it.readByteArray() })
        )))
        println("sent audio at $file")
    }
    // sends image file
    fun sendImage(file: String) {
        socket.emit("audio", Json.encodeToString(buildJsonObject {
            put("image", Base64.encode(SystemFileSystem.source(Path(file)).buffered().use { it.readByteArray() }))
            put("focal_length_px", 3094)
        }))
        println("sent image at $file")
    }
}
