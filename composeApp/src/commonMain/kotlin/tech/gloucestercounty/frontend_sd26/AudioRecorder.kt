package tech.gloucestercounty.frontend_sd26

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.microphone.RECORD_AUDIO
import dev.theolm.record.Record
import dev.theolm.record.config.OutputFormat
import dev.theolm.record.config.RecordConfig
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import nl.marc_apps.tts.TextToSpeechInstance
import tech.gloucestercounty.frontend_sd26.api.BaseAPI

// object with all audio controller/recorder/tts items
object AudioRecorder {
    // keeps track of whether audio is being recorded, as a mutable state flow to recompose and update items when changed
    private val _isRecording = MutableStateFlow(false)

    // stores the most recent scan result type and id received from the server's store websocket event
    val lastScanResult = MutableStateFlow<Pair<String, String>?>(null)

    // holds the tts engine instance; set by app.kt once the composable initializes
    var ttsInstance: TextToSpeechInstance? = null

    // stops the current recording and sends the resulting file to the server via websocket
    fun stopAndSend() {
        val file = Record.stopRecording() ?: return // get recorded file path; bail if null (nothing was recorded)
        _isRecording.value = false
        BaseAPI.sendAudio(file) // send audio bytes to server for processing
    }

    // called by the websocket tts event — speaks the given text aloud using the platform tts engine
    fun speak(text: String) {
        MainScope().launch {
            ttsInstance?.say(text) // speak via platform tts; no-op if tts failed to initialize
        }
    }

    @Composable
    fun FAB() {
        // set recording config
        Record.setConfig(
            RecordConfig(
                outputFormat = OutputFormat.WAV // use wav files instead of the default mp4
            )
        )

        // creates floating action button for use on any page
        val isRecording by _isRecording.collectAsState()
        // button changes while recording to be a recording symbol
        if (isRecording) {
            FloatingActionButton(
                onClick = { stopAndSend() } // stops recording and sends audio to server
            ) {
                Icon(Icons.Rounded.Album, "stop recording", tint = Color(0xFFFF0000)) // there is no recording icon so the record/album logo is used since it is close enough
            }
        } else {
            // get permissions to use microphone
            val factory = rememberPermissionsControllerFactory()
            val controller: PermissionsController = remember(factory) { factory.createPermissionsController() }
            BindEffect(controller)
            rememberCoroutineScope().launch {
                controller.providePermission(Permission.RECORD_AUDIO)
            }
            FloatingActionButton(
                onClick = { // starts the recording
                    Record.startRecording()
                    _isRecording.value = true
                }
            ) {
                Icon(Icons.AutoMirrored.Rounded.Chat, "chat")
            }
        }
    }
}
