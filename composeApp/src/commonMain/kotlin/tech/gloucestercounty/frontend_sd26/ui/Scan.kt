package tech.gloucestercounty.frontend_sd26.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kashif.cameraK.compose.CameraPreviewView
import com.kashif.cameraK.compose.rememberCameraKState
import com.kashif.cameraK.enums.AspectRatio
import com.kashif.cameraK.enums.CameraLens
import com.kashif.cameraK.enums.FlashMode
import com.kashif.cameraK.state.CameraConfiguration
import com.kashif.cameraK.state.CameraKState
import com.kashif.imagesaverplugin.ImageSaverConfig
import com.kashif.imagesaverplugin.rememberImageSaverPlugin
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.camera.CAMERA
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.launch

@Composable
@Preview
fun Scan() {
    Scaffold { innerPaddings ->
        Column(
            modifier = Modifier.padding(innerPaddings).padding(8.dp)
        ) {
            val scope = rememberCoroutineScope()

            val factory = rememberPermissionsControllerFactory()
            val controller: PermissionsController = remember(factory) { factory.createPermissionsController() }
            BindEffect(controller)
            scope.launch {
                controller.providePermission(Permission.CAMERA)
            }

            val imageSaverPlugin = rememberImageSaverPlugin(config = ImageSaverConfig(isAutoSave = true))
            val cameraState by rememberCameraKState(
                config = CameraConfiguration(
                    cameraLens = CameraLens.BACK,
                    flashMode = FlashMode.OFF,
                    aspectRatio = AspectRatio.RATIO_4_3
                ),
                setupPlugins = { stateHolder ->
                    stateHolder.attachPlugin(imageSaverPlugin)
                }
            )

            when (cameraState) {
                is CameraKState.Initializing -> {
                    CircularProgressIndicator()
                }
                is CameraKState.Ready -> {
                    val controller = (cameraState as CameraKState.Ready).controller

                    CameraPreviewView(
                        controller = controller,
                        modifier = Modifier.fillMaxSize().clickable {
                            scope.launch {
                                controller.takePictureToFile()
                            }
                        }
                    )
                }
                is CameraKState.Error -> {
                    Text("Camera Error: ${(cameraState as CameraKState.Error).message}")
                }
            }
        }
    }
}