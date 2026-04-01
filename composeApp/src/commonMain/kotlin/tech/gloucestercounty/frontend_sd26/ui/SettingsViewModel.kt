//package tech.gloucestercounty.frontend_sd26.ui
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import tech.gloucestercounty.frontend_sd26.api.MlSettingsPatch
//import tech.gloucestercounty.frontend_sd26.api.SpaitraClient
//import tech.gloucestercounty.frontend_sd26.api.UserSettingsPatch
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//
////TODO: make it so it actually saves locally
//data class SettingsState(
//    val isLoading: Boolean = true,
//    val isSaving: Boolean = false,
//    val error: String? = null,
//    val saveSuccess: Boolean = false,
//
//    val performanceMode: Int = 1,
//    val voiceSpeed: Float = 1.0f,
//    val buttonLayout: Int = 0,
//
//    val learningEnabled: Boolean = true,
//
//    val scanUpdateLocation: Boolean = true
//)
//
//class SettingsViewModel : ViewModel() {
//    private val api = SpaitraClient.spaitraApi
//
//    private val _uiState = MutableStateFlow(SettingsState())
//    val uiState = _uiState
//
//    init {
//        loadSettings()
//    }
//
//    fun onScanUpdateLocationChange(enabled: Boolean) {
//        _uiState.update { it.copy(scanUpdateLocation = enabled) }
//    }
//
//
//    private fun loadSettings() {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true, error = null) }
//            try {
//                val userSettings = api.getUserSettings()
//                val mlSettings = api.getMlSettings()
//
//                _uiState.update { current ->
//                    current.copy(
//                        isLoading = false,
//                        performanceMode = when (userSettings.performanceMode) {
//                            "fast" -> 0
//                            "balanced" -> 1
//                            "accurate" -> 2
//                            else -> 1
//                        },
//                        voiceSpeed = userSettings.voiceSpeed,
//                        buttonLayout = if (userSettings.buttonLayout == "default") 0 else 1,
//                        learningEnabled = mlSettings.enableLearning
//                    )
//                }
//            } catch (e: Exception) {
//                _uiState.update { it.copy(isLoading = false, error = "Failed to load settings.") }
//            }
//        }
//    }
//
//    fun onPerformanceModeChange(index: Int) {
//        _uiState.update { it.copy(performanceMode = index) }
//    }
//
//    fun onVoiceSpeedChange(speed: Float) {
//        _uiState.update { it.copy(voiceSpeed = speed) }
//    }
//
//    fun onLearningEnabledChange(enabled: Boolean) {
//        _uiState.update { it.copy(learningEnabled = enabled) }
//    }
//
//    fun onButtonLayoutChange(index: Int) {
//        _uiState.update { it.copy(buttonLayout = index) }
//    }
//
//    fun saveSettings() {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isSaving = true, error = null, saveSuccess = false) }
//            try {
//                val current = _uiState.value
//
//                api.patchUserSettings(
//                    UserSettingsPatch(
//                        performanceMode = listOf("fast", "balanced", "accurate")[current.performanceMode],
//                        voiceSpeed = current.voiceSpeed,
//                        learningEnabled = current.learningEnabled,
//                        buttonLayout = if (current.buttonLayout == 0) "default" else "swapped"
//                    )
//                )
//
//                api.patchMlSettings(
//                    MlSettingsPatch(
//                        enableLearning = current.learningEnabled
//                    )
//                )
//
//                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
//            } catch (e: Exception) {
//                _uiState.update { it.copy(isSaving = false, error = "Failed to save settings.") }
//            }
//        }
//    }
//}
