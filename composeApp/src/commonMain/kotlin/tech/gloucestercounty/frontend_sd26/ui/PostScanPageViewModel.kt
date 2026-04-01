package tech.gloucestercounty.frontend_sd26.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import tech.gloucestercounty.frontend_sd26.api.FeedbackRequest
import tech.gloucestercounty.frontend_sd26.api.ScanMatch
import tech.gloucestercounty.frontend_sd26.api.SightingEntry
import tech.gloucestercounty.frontend_sd26.api.SightingsRequest
import tech.gloucestercounty.frontend_sd26.api.SpaitraClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import tech.gloucestercounty.frontend_sd26.api.ScanResponse

data class PostScanUiState(
    val isScanning: Boolean = false,
    val isDark: Boolean = false,
    val error: String? = null,

    val scanId: String? = null,
    val matches: List<ScanMatch> = emptyList(),
    val currentMatchIndex: Int = 0,

    val cropBytes: ByteArray? = null,
    val isLoadingCrop: Boolean = false,

    val roomNamePromptVisible: Boolean = false,
    val roomSaved: Boolean = false,

    val excludedIndices: Set<Int> = emptySet(),

    val itemAskResult: String? = null,
)

class PostScanViewModel : ViewModel() {

    private val api = SpaitraClient.spaitraApi

    private val _uiState = MutableStateFlow(PostScanUiState())
    val uiState = _uiState

    fun startScan(imagePath: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, error = null, isDark = false) }
            try {
                val imageFile = Path(imagePath)
                val imageBytes = SystemFileSystem.source(imageFile).use { source ->
                    source.buffered().readByteArray()
                }

                val body = MultiPartFormDataContent(
                    formData {
                        append(
                            "image",
                            imageBytes,
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=\"scan.jpg\"")
                            }
                        )
                    }
                )

                val response = ScanResponse(
                    scanId = "test123",
                    count = 4,
                    matches = listOf(
                        ScanMatch(
                            label = "wallet",
                            similarity = 0.72f,
                            confidence = "high",
                            direction = "to your left",
                            narration = "Wallet, look down, to your left."
                        ),
                        ScanMatch(
                            label = "house keys",
                            similarity = 0.61f,
                            confidence = "high",
                            direction = "ahead",
                            narration = "House keys, ahead."
                        ),
                        ScanMatch(
                            label = "phone charger",
                            similarity = 0.38f,
                            confidence = "medium",
                            direction = "slightly right",
                            narration = "May be phone charger, slightly right, focus to verify."
                        ),
                        ScanMatch(
                            label = "water bottle",
                            similarity = 0.29f,
                            confidence = "medium",
                            direction = "to your right",
                            narration = "May be water bottle, look up, to your right."
                        )
                    )
                )
                    //api.scan(body)

                if (response.isDark == true) {
                    _uiState.update {
                        it.copy(
                            isScanning = false,
                            isDark = true,
                            error = response.message ?: "Image is too dark. Increase lighting."
                        )
                    }
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        isScanning = false,
                        scanId = response.scanId,
                        matches = response.matches,
                        currentMatchIndex = 0
                    )
                }

                if (response.matches.isNotEmpty()) {
                    loadCrop(0)
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isScanning = false, error = "Scan failed: ${e.message}")
                }
            }
        }
    }

    fun loadCrop(index: Int) {
        val scanId = _uiState.value.scanId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCrop = true, cropBytes = null) }
            try {
                val bytes = api.getCrop(scanId, index)
                _uiState.update { it.copy(isLoadingCrop = false, cropBytes = bytes) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingCrop = false, cropBytes = null) }
            }
        }
    }

    fun nextMatch() {
        val state = _uiState.value
        if (state.currentMatchIndex < state.matches.size - 1) {
            val newIndex = state.currentMatchIndex + 1
            _uiState.update { it.copy(currentMatchIndex = newIndex, cropBytes = null) }
            loadCrop(newIndex)
        }
    }

    fun previousMatch() {
        val state = _uiState.value
        if (state.currentMatchIndex > 0) {
            val newIndex = state.currentMatchIndex - 1
            _uiState.update { it.copy(currentMatchIndex = newIndex, cropBytes = null) }
            loadCrop(newIndex)
        }
    }

    val currentMatch: ScanMatch?
        get() = _uiState.value.matches.getOrNull(_uiState.value.currentMatchIndex)

    fun sendWrongFeedback() {
        val state = _uiState.value
        val scanId = state.scanId ?: return
        val currentIndex = state.currentMatchIndex
        val label = currentMatch?.label ?: return

        _uiState.update { it.copy(excludedIndices = it.excludedIndices + currentIndex) }

        viewModelScope.launch {
            try {
                api.postFeedback(FeedbackRequest(scanId, label, "wrong"))
            } catch (e: Exception) {

            }
        }
    }


    fun showRoomNamePrompt() {
        _uiState.update { it.copy(roomNamePromptVisible = true) }
    }

    fun saveRoomName(roomName: String) {
        val state = _uiState.value
        viewModelScope.launch {
            try {
                api.postSightings(
                    SightingsRequest(
                        roomName = roomName,
                        sightings = state.matches.filterIndexed { index, _ ->
                            index !in state.excludedIndices
                        }.map { match ->
                            SightingEntry(
                                label = match.label,
                                direction = match.direction,
                                similarity = match.similarity
                            )
                        }
                    )
                )
                _uiState.update {
                    it.copy(roomNamePromptVisible = false, roomSaved = true)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to save room: ${e.message}")
                }
            }
        }
    }
}