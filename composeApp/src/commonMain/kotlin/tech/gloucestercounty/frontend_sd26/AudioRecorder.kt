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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

// object with all audio controller/recorder/tts items
object AudioRecorder {
    // keeps track of whether audio is being recorded, as a mutable state flow to recompose and update items when changed
    private val _isRecording = MutableStateFlow(false)

    @Composable
    fun FAB() {
        // creates floating action button for use on any page
        val isRecording by _isRecording.collectAsState()
        // button changes while recording to be a recording symbol
        if (isRecording) {
            FloatingActionButton(
                onClick = { // stops the recording
                    Record.stopRecording()
                    _isRecording.value = false
                }
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