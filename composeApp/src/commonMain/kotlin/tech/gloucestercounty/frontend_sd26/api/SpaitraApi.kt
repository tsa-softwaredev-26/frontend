package tech.gloucestercounty.frontend_sd26.api

import de.jensklingenberg.ktorfit.http.*
import io.ktor.client.request.forms.MultiPartFormDataContent

interface SpaitraApi {

    @POST("scan")
    @Multipart
    suspend fun scan(@Body body: MultiPartFormDataContent): ScanResponse

    @GET("crop")
    suspend fun getCrop(
        @Query("scan_id") scanId: String,
        @Query("index") index: Int
    ): ByteArray

    @POST("feedback")
    suspend fun postFeedback(@Body request: FeedbackRequest): FeedbackResponse

    @POST("sightings")
    suspend fun postSightings(@Body request: SightingsRequest): SightingsResponse

    @GET("user-settings")
    suspend fun getUserSettings(): UserSettings

    @PATCH("user-settings")
    suspend fun patchUserSettings(@Body patch: UserSettingsPatch): UserSettings

    @GET("settings")
    suspend fun getMlSettings(): MlSettings

    @PATCH("settings")
    suspend fun patchMlSettings(@Body patch: MlSettingsPatch): MlSettings
}