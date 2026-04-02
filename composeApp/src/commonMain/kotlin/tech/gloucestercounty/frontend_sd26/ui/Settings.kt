package tech.gloucestercounty.frontend_sd26.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import tech.gloucestercounty.frontend_sd26.AudioRecorder
import tech.gloucestercounty.frontend_sd26.api.BaseAPI
import tech.gloucestercounty.frontend_sd26.nav
import kotlin.math.roundToInt

@Composable
fun EasySegmentedButton(name: String, list: List<String>, select: () -> Int, change: (Int) -> Unit) {
    // quicker way of making custom segmented buttons, which are reused a lot in this page
    // the list of options is passed normally, but there is no way to directly pass the variable to get and change on the other side, so lambda functions are used instead
    Column(modifier = Modifier.fillMaxWidth()) {
        list.forEachIndexed { i, label ->
            OutlinedButton(
                onClick = {
                    change(i)
                    MainScope().launch {
                        BaseAPI.setUserSettings(
                            Json.encodeToJsonElement(mapOf(
                                name to list[i]
                            )) as JsonObject
                        )
                    }
                }, // change selected item to the one clicked
                // shape changes depending on index in list
                shape = if (i == 0) RoundedCornerShape(8.dp, 8.dp) else if (i == list.size - 1) RoundedCornerShape(0.dp, 0.dp, 8.dp, 8.dp) else RoundedCornerShape(0.dp),
                modifier = Modifier.fillMaxWidth().height(64.dp),
                // color changes if it is selected or not (selected color is actually pulled from normal/nonoutlined button colors)
                colors = if (select() == i) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
            ) {
                Text(
                    label, // label is specific to each item
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                // add check mark on right side of button
                if (select() == i) {
                    Icon(Icons.Rounded.Check, "Selected")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun Settings() {
    // settings page

    // set all variables through "remember" state variables
    // these kinds of variables will recompose or update all tied objects when changed
    var performanceMode by remember { mutableIntStateOf(1) }
    val performanceModes = listOf("fast", "balanced", "accurate")
    var voiceSpeed by remember { mutableFloatStateOf(1.0f) }
    var buttonLayout by remember { mutableIntStateOf(0) }
    val buttonLayouts = listOf("Default", "Swapped")

    // launch a coroutine to update currently selected settings
    MainScope().launch {
        val currentSettings = BaseAPI.getUserSettings() // get settings from api
        performanceMode = performanceModes.indexOf(currentSettings["performance_mode"].toString())
        voiceSpeed = currentSettings["voice_speed"].toString().toFloat()
        buttonLayout = if (currentSettings["button_layout"].toString() == "default") 0 else 1
    }

    Scaffold(
        topBar = { // add top bar with settings label
            CenterAlignedTopAppBar(
                title = {
                    Text("Settings")
                },
                navigationIcon = { // back button
                    IconButton(
                        onClick = { nav.popBackStack() } // navigate back
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = { AudioRecorder.FAB() } // audio controller fab
    ) { innerPaddings ->
        Column(
            // many modifiers, but most importantly .verticalScroll allows user to scroll through page
            modifier = Modifier.padding(innerPaddings).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // NOTE: most settings here are self-explanatory and repetitive so there will be less comments

            // how accurate or fast the model is
            Text("Performance Mode", style = MaterialTheme.typography.titleMedium)
            EasySegmentedButton("performance_mode", performanceModes, { performanceMode }, { performanceMode = it })
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            // the speed of tts voice
            val displaySpeed = (voiceSpeed * 4).roundToInt() / 4f
            Text(
                "Voice Speed: ${displaySpeed}x",
                style = MaterialTheme.typography.titleMedium
            )
            Slider(
                value = voiceSpeed,
                onValueChange = {
                    voiceSpeed = it
                    MainScope().launch {
                        BaseAPI.setUserSettings(
                            Json.encodeToJsonElement(mapOf(
                                "voice_speed" to it
                            )) as JsonObject
                        )
                    }
                },
                valueRange = 0.25f..4.0f,
                steps = 14,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            // swap main page button layout
            Text("Button Layout", style = MaterialTheme.typography.titleMedium)
            EasySegmentedButton("button_layout", buttonLayouts, { buttonLayout }, { buttonLayout = it })
        }
    }
}
