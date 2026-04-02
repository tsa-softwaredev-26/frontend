package tech.gloucestercounty.frontend_sd26.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import tech.gloucestercounty.frontend_sd26.AudioRecorder
import tech.gloucestercounty.frontend_sd26.ScanPage
import tech.gloucestercounty.frontend_sd26.SettingsPage
import tech.gloucestercounty.frontend_sd26.api.BaseAPI
import tech.gloucestercounty.frontend_sd26.nav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun HomeScreen() {
    // the home screen is the first screen that opens with the app

    // set up snackbars for errors
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // check health and show snackbar error if bad
    scope.launch {
        if (!BaseAPI.Health()) {
            snackbarHostState.showSnackbar("COULD NOT CONNECT TO API: app will most likely crash soon")
        }
    }

    Scaffold(
        topBar = {
            // top app bar with spaitra name
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // loads a logo image from a (unfortunately) hardcoded url due to ios limitations with drawings
                        AsyncImage(
                            "https://cdn.discordapp.com/attachments/1372222272231833804/1485392585295003668/spaitra.png?ex=69c45625&is=69c304a5&hm=5cf0f1e2a4af9e5aa51d7c312c4c81e95d23cad61a399386d2450b99ee902261&",
                            "Spaitra logo",
                            modifier = Modifier.height(48.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "SPAITRA",
                            fontWeight = FontWeight(700)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }, // add snackbar to app
        floatingActionButton = { AudioRecorder.FAB() } // adds fab for recording audio
    ) { innerPaddings ->
        Column(
            modifier = Modifier.padding(innerPaddings).padding(8.dp)
        ) {
            Button( // top settings button
                onClick = {
                    nav.navigate(SettingsPage) // navigates to settings page when clicked
                },
                modifier = Modifier.fillMaxWidth().weight(1f).background(Brush.linearGradient(
                    // difficult but working way of adding the gradient background on the buttons
                    listOf(Color(0xFF727DFF), Color(0xFF2230A3)), Offset(0f, 0f), Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                ), shape = RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp), // shape has to be repeated so neither the background or button itself overflows
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent) // needed for gradient background
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Rounded.Settings, // compose multiplatform icon for settings
                        "Settings Icon",
                        modifier = Modifier.width(96.dp).height(96.dp)
                    )
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // button to go to scan page, nearly identical code to settings but slightly larger weight to make it larger
            Button(
                onClick = {
                    nav.navigate(ScanPage)
                },
                modifier = Modifier.fillMaxWidth().weight(2f).background(Brush.linearGradient(
                    listOf(Color(0xFF727DFF), Color(0xFF2230A3)), Offset(0f, 0f), Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                ), shape = RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Rounded.DocumentScanner, // compose multiplatform icon for scanning
                        "Scan Icon",
                        modifier = Modifier.width(96.dp).height(96.dp)
                    )
                    Text(
                        "Scan",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }
    }
}