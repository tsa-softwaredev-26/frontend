package tech.gloucestercounty.frontend_sd26.api

import de.jensklingenberg.ktorfit.http.*
import io.ktor.client.request.forms.MultiPartFormDataContent

interface SpaitraApi {
    @GET("health")
    suspend fun getHealth():Unit

    @GET("user-settings")
    suspend fun getUserSettings(): UserSettings

    @PATCH("user-settings")
    suspend fun patchUserSettings(@Body patch: UserSettingsPatch): UserSettings

    @GET("items")
    suspend fun getItems(): List<Item>

    @DELETE("items/{label}")
    suspend fun deleteItem(@Path("label") label: String)


    @POST("items/{label}/rename")
    suspend fun renameItem(
        @Path("label") label: String,
        @Body body: RenameRequest
    ): Item

    @GET("crop")
    suspend fun getCrop(
        @Query("scan_id") scanId: String,
        @Query("index") index: Int
    ): ByteArray

    @GET("debug/state")
    suspend fun getState(): DebugState

    @GET("debug/test-remember")
    suspend fun postRemember(): Unit

    @GET("debug/test-scan")
    suspend fun getScan(): Unit

    @POST("debug/wipe")
    suspend fun postWipe(@Body body: WipeRequest)
}