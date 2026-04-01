package tech.gloucestercounty.frontend_sd26.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

// HTTP models
enum class PerformanceMode {
    @SerialName("fast") FAST,
    @SerialName("balanced") BALANCED,
    @SerialName("accurate") ACCURATE
}

@Serializable
data class UserSettings(
    @SerialName("performance_mode") val performanceMode: PerformanceMode,
    @SerialName("voice_speed") val voiceSpeed: Float,
)

@Serializable
data class UserSettingsPatch(
    @SerialName("performance_mode") val performanceMode: PerformanceMode? = null,
    @SerialName("voice_speed") val voiceSpeed: Float? = null,
)

@Serializable
data class Item(
    val label: String,
)

@Serializable
data class RenameRequest(
    @SerialName("new_label") val newLabel: String,
    val force: Boolean? = null
)
//Debug models
@Serializable
data class DebugState(
    @SerialName("pipeline_health") val pipelineHealth: String? = null,
    @SerialName("model_load_status") val modelLoadStatus: String? = null
)

@Serializable
data class WipeRequest(
    val confirm: Boolean,
    val target: String
)

//WebSocket models
@Serializable
data class TtsEvent(
    val narration: String,
    @SerialName("next_state") val nextState: String? = null
)

enum class stateMachine {
    @SerialName("idle") IDLE,
    @SerialName("onboarding_teach") ONBOARDING_TEACH,
    @SerialName("onboarding_await_scan") ONBOARDING_AWAIT_SCAN,
    @SerialName("awaiting_image") AWAITING_IMAGE,
    @SerialName("awaiting_location") AWAITING_LOCATION,
    @SerialName("awaiting_confirmation") AWAITING_CONFIRMATION,
    @SerialName("focused_on_item") FOCUSED_ON_ITEM
}
@Serializable
data class SessionStateEvent(
    @SerialName("current_mode") val currentMode: stateMachine,
    val context: SessionContext
)

@Serializable
data class SessionContext(
    @SerialName("scan_id") val scanId: String? = null,
    val label: String? = null,
    @SerialName("item_index") val itemIndex: Int? = null,
    @SerialName("onboarding_phase") val onboardingPhase: String? = null
)

@Serializable
data class ListeningEvent(
    val state: String,
    val prompt: String? = null
)

enum class ControlAction {
    @SerialName("request_image") REQUEST_IMAGE
}

@Serializable
data class ControlEvent(
    val action: ControlAction,
    val context: String? = null
)

enum class ActionResultType {
    @SerialName("scan") SCAN,
    @SerialName("find") FIND,
    @SerialName("remember") REMEMBER,
    @SerialName("set_location") SET_LOCATION,
    @SerialName("item_focus") ITEM_FOCUS,
    @SerialName("open_settings") OPEN_SETTINGS,
    @SerialName("navigate_back") NAVIGATE_BACK
}

@Serializable
data class ActionResultEvent(
    val type: ActionResultType,
    val data: JsonObject
)

@Serializable
data class TranscriptionEvent(
    val text: String
)

@Serializable
data class ErrorEvent(
    val code: String,
    val message: String
)

// Takes care of WebSocket events
sealed class SpaitraEvent {
    data class Tts(val event: TtsEvent) : SpaitraEvent()
    data class SessionState(val event: SessionStateEvent) : SpaitraEvent()
    data class Listening(val event: ListeningEvent) : SpaitraEvent()
    object ListeningStopped : SpaitraEvent()
    data class Control(val event: ControlEvent) : SpaitraEvent()
    data class ActionResult(val event: ActionResultEvent) : SpaitraEvent()
    data class Transcription(val event: TranscriptionEvent) : SpaitraEvent()
    data class Error(val event: ErrorEvent) : SpaitraEvent()
}