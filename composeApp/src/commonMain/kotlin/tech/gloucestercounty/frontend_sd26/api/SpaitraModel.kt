package tech.gloucestercounty.frontend_sd26.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScanMatch(
    val label: String,
    val similarity: Float,
    val confidence: String,
    val direction: String,
    val narration: String,
    @SerialName("distance_ft") val distanceFt: Float? = null,
    val box: List<Int>? = null,
    @SerialName("ocr_text") val ocrText: String? = null
)

@Serializable
data class ScanResponse(
    @SerialName("scan_id") val scanId: String? = null,
    val count: Int,
    val matches: List<ScanMatch>,
    @SerialName("is_dark") val isDark: Boolean? = null,
    @SerialName("darkness_level") val darknessLevel: Float? = null,
    val message: String? = null
)

@Serializable
data class FeedbackRequest(
    @SerialName("scan_id") val scanId: String,
    val label: String,
    val feedback: String
)

@Serializable
data class FeedbackResponse(
    val recorded: Boolean,
    val label: String,
    val feedback: String,
    val triplets: Int,
    @SerialName("min_for_training") val minForTraining: Int
)

@Serializable
data class SightingEntry(
    val label: String,
    val direction: String? = null,
    @SerialName("distance_ft") val distanceFt: Float? = null,
    val similarity: Float? = null
)

@Serializable
data class SightingsRequest(
    @SerialName("room_name") val roomName: String,
    val sightings: List<SightingEntry>
)

@Serializable
data class SightingsResponse(
    val saved: Int,
    val labels: List<String>,
    @SerialName("room_name") val roomName: String
)

@Serializable
data class UserSettings(
    @SerialName("performance_mode") val performanceMode: String,
    @SerialName("voice_speed") val voiceSpeed: Float,
    @SerialName("learning_enabled") val learningEnabled: Boolean,
    @SerialName("button_layout") val buttonLayout: String
)

@Serializable
data class UserSettingsPatch(
    @SerialName("performance_mode") val performanceMode: String? = null,
    @SerialName("voice_speed") val voiceSpeed: Float? = null,
    @SerialName("learning_enabled") val learningEnabled: Boolean? = null,
    @SerialName("button_layout") val buttonLayout: String? = null
)

@Serializable
data class FeedbackCounts(
    val positives: Int,
    val negatives: Int,
    val triplets: Int
)

@Serializable
data class MlSettings(
    @SerialName("enable_learning") val enableLearning: Boolean,
    @SerialName("min_feedback_for_training") val minFeedbackForTraining: Int,
    @SerialName("projection_head_weight") val projectionHeadWeight: Float,
    @SerialName("head_trained") val headTrained: Boolean,
    @SerialName("triplet_count") val tripletCount: Int,
    @SerialName("feedback_counts") val feedbackCounts: FeedbackCounts
)
@Serializable
data class MlSettingsPatch(
    @SerialName("enable_learning") val enableLearning: Boolean? = null,
    @SerialName("min_feedback_for_training") val minFeedbackForTraining: Int? = null,
    @SerialName("projection_head_weight") val projectionHeadWeight: Float? = null
)