package tech.gloucestercounty.frontend_sd26.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.gloucestercounty.frontend_sd26.AudioRecorder
import tech.gloucestercounty.frontend_sd26.nav
import kotlin.math.roundToInt

@Composable
fun EasySegmentedButton(list: List<String>, select: () -> Int, change: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        list.forEachIndexed { i, label ->
            OutlinedButton(
                onClick = { change(i) },
                shape = if (i == 0) RoundedCornerShape(8.dp, 8.dp) else if (i == list.size - 1) RoundedCornerShape(0.dp, 0.dp, 8.dp, 8.dp) else RoundedCornerShape(0.dp),
                modifier = Modifier.fillMaxWidth().height(64.dp),
                colors = if (select() == i) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
            ) {
                Text(
                    label,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
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
    //TODO: ktorfit is needed here to grab current settings and fill them in defaults

    var performanceMode by remember { mutableIntStateOf(1) }
    val performanceModes = listOf("Fast", "Balanced", "Accurate")

    var voiceSpeed by remember { mutableFloatStateOf(1.0f) }

    var scanUpdateLocation by remember { mutableStateOf(true) }
    var learningEnabled by remember { mutableStateOf(true) }

    var buttonLayout by remember { mutableIntStateOf(0) }



    val buttonLayouts = listOf("Default", "Swapped")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Settings")
                },
                navigationIcon = {
                    IconButton(
                        onClick = { nav.popBackStack() }
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                    }





                }
            )
        },
        floatingActionButton = { AudioRecorder.FAB() }
    ) { innerPaddings ->
        Column(
            modifier = Modifier.padding(innerPaddings).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Performance Mode", style = MaterialTheme.typography.titleMedium)
            EasySegmentedButton(performanceModes, { performanceMode }, { performanceMode = it })
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            val displaySpeed = (voiceSpeed * 4).roundToInt() / 4f
            Text(
                "Voice Speed: ${displaySpeed}x",
                style = MaterialTheme.typography.titleMedium
            )
            Slider(
                value = voiceSpeed,
                onValueChange = { voiceSpeed = it },
                valueRange = 0.25f..4.0f,
                steps = 14,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { scanUpdateLocation = !scanUpdateLocation },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Scan Update Location", style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = scanUpdateLocation,
                    onCheckedChange = { scanUpdateLocation = it }
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { learningEnabled = !learningEnabled },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Learning Enabled", style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = learningEnabled,
                    onCheckedChange = { learningEnabled = it }
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text("Button Layout", style = MaterialTheme.typography.titleMedium)
            EasySegmentedButton(buttonLayouts, { buttonLayout }, { buttonLayout = it })
        }
    }
}
