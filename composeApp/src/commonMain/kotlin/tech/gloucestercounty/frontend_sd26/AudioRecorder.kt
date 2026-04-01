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

object AudioRecorder {
    private val _isRecording = MutableStateFlow(false)

    @Composable
    fun FAB() {
        val isRecording by _isRecording.collectAsState()
        if (isRecording) {
            FloatingActionButton(
                onClick = {
                    Record.stopRecording()
                    _isRecording.value = false
                }
            ) {
                Icon(Icons.Rounded.Album, "chat", tint = Color(0xFFFF0000))
            }
        } else {
            val factory = rememberPermissionsControllerFactory()
            val controller: PermissionsController = remember(factory) { factory.createPermissionsController() }
            BindEffect(controller)
            rememberCoroutineScope().launch {
                controller.providePermission(Permission.RECORD_AUDIO)
            }
            FloatingActionButton(
                onClick = {
                    Record.startRecording()
                    _isRecording.value = true
                }
            ) {
                Icon(Icons.AutoMirrored.Rounded.Chat, "chat")
            }
        }
    }
}