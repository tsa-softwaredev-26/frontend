//package tech.gloucestercounty.frontend_sd26.api
//
//import io.ktor.client.*
//import io.ktor.client.plugins.websocket.*
//import io.ktor.websocket.*
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.SharedFlow
//import kotlinx.coroutines.flow.asSharedFlow
//import kotlinx.serialization.json.*
//
//// --- Sealed class for all incoming events ---
//
//
//
//// --- WebSocket class ---
//
//class SpaitraWebSocket(
//    private val httpClient: HttpClient,
//    private val apiKey: String = SpaitraClient.API_KEY,
//    private val baseUrl: String = "nre5bjw44wddpu2zjg4fe4iehq.srv.us"
//) {
//
//    private val _events = MutableSharedFlow<SpaitraEvent>(extraBufferCapacity = 64)
//    val events: SharedFlow<SpaitraEvent> = _events.asSharedFlow()
//
//    private var session: DefaultWebSocketSession? = null
//    private val json = Json { ignoreUnknownKeys = true }
//
//    suspend fun connect() {
//        httpClient.wss(
//            host = baseUrl,
//            path = "/socket.io/?key=$apiKey&EIO=4&transport=websocket"
//        ) {
//            session = this
//            for (frame in incoming) {
//                if (frame is Frame.Text) {
//                    handleRawFrame(frame.readText())
//                }
//            }
//        }
//    }
//
//    private suspend fun handleRawFrame(raw: String) {
//        val trimmed = raw.trimStart { it.isDigit() }
//        if (trimmed.isBlank()) return
//
//        runCatching {
//            val array = json.parseToJsonElement(trimmed).jsonArray
//            val eventName = array[0].jsonPrimitive.content
//            val payload = array.getOrNull(1) ?: return
//
//            val event: SpaitraEvent? = when (eventName) {
//                "tts" -> SpaitraEvent.Tts(
//                    json.decodeFromJsonElement(payload)
//                )
//                "session_state" -> SpaitraEvent.SessionState(
//                    json.decodeFromJsonElement(payload)
//                )
//                "listening" -> SpaitraEvent.Listening(
//                    json.decodeFromJsonElement(payload)
//                )
//                "listening_stopped" -> SpaitraEvent.ListeningStopped
//                "control" -> SpaitraEvent.Control(
//                    json.decodeFromJsonElement(payload)
//                )
//                "action_result" -> SpaitraEvent.ActionResult(
//                    json.decodeFromJsonElement(payload)
//                )
//                "transcription" -> SpaitraEvent.Transcription(
//                    json.decodeFromJsonElement(payload)
//                )
//                "error" -> SpaitraEvent.Error(
//                    json.decodeFromJsonElement(payload)
//                )
//                else -> null
//            }
//
//            event?.let { _events.emit(it) }
//        }.onFailure { e ->
//            println("WebSocket frame error: $e")
//        }
//    }
//
//    // Sending events
//    suspend fun sendChatStart() = sendEvent("chat_start", "{}")
//
//    suspend fun sendChatStop() = sendEvent("chat_stop", "{}")
//
//    suspend fun sendAudio(
//        audio: String,
//        image: String? = null,
//        focalLengthPx: Double? = null
//    ) {
//        val payload = buildJsonObject {
//            put("audio", audio)
//            image?.let { put("image", it) }
//            focalLengthPx?.let { put("focal_length_px", it) }
//        }
//        sendEvent("audio", payload.toString())
//    }
//
//    suspend fun sendNavigate(direction: String) {
//        require(direction == "next" || direction == "prev")
//        val payload = buildJsonObject { put("direction", direction) }
//        sendEvent("navigate", payload.toString())
//    }
//
//    private suspend fun sendEvent(name: String, payloadJson: String) {
//        session?.send(Frame.Text("""42["$name",$payloadJson]"""))
//    }
//
//    fun close() {
//        httpClient.close()
//        session = null
//    }
//}