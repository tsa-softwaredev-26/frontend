package tech.gloucestercounty.frontend_sd26

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import nl.marc_apps.tts.rememberTextToSpeechOrNull
import tech.gloucestercounty.frontend_sd26.api.BaseAPI
import tech.gloucestercounty.frontend_sd26.ui.HomeScreen
import tech.gloucestercounty.frontend_sd26.ui.PostScanPage
import tech.gloucestercounty.frontend_sd26.ui.Scan
import tech.gloucestercounty.frontend_sd26.ui.Settings

@Composable
@Preview
fun App() {
    // main app controller
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // keeps navigation routes and controls
    nav = rememberNavController()

    // create a lifecycle-aware tts instance for the current platform; null if tts is unavailable
    val tts = rememberTextToSpeechOrNull()
    // hand the instance to audio recorder so websocket tts callbacks can use it
    AudioRecorder.ttsInstance = tts

    // initialize the websocket connection once when the app starts
    LaunchedEffect(Unit) {
        BaseAPI.WS(
            tts = { text ->
                AudioRecorder.speak(text) // narrate server-provided text via platform tts
            },
            openCamera = {
                if (nav.currentDestination != ScanPage) nav.navigate(ScanPage) // server requested the camera be opened
            },
            store = { type, id ->
                AudioRecorder.lastScanResult.value = type to id // store scan result type and id for later use
            },
            error = { code, message ->
                scope.launch {
                    snackbarHostState.showSnackbar("Error $code: $message") // surface websocket errors to the user
                }
            }
        )
    }

    // materialtheme adds all color schemes to make styling easier
    MaterialTheme(colorScheme = lightColorScheme()) {
        // top-level scaffold hosts the global snackbar used for websocket error messages
        Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
            // navhost keeps a list of routes to all pages, also defined specifically in Routes.kt
            // route definitions are also in Routes.kt
            NavHost(navController = nav, startDestination = Home) {
                composable<Home> {
                    HomeScreen()
                }

                composable<ScanPage> {
                    Scan()
                }

                composable<PostScan> {
                    // the parameter allows the photo path to be passed from one screen to another
                    PostScanPage(it.toRoute<PostScan>().path)
                }

                composable<SettingsPage> {
                    Settings()
                }
            }
        }
    }
}
