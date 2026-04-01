package tech.gloucestercounty.frontend_sd26.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel

//TODO: fix ui, not too sure if joe wants it to still look like chat messages
@Composable
fun PostScanPage(
    path: String,
    viewModel: PostScanViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(path) {
        viewModel.startScan(path)
    }

    Scaffold { innerPaddings ->
        Column(
            modifier = Modifier.padding(innerPaddings).padding(8.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AsyncImage(
                model = "file://$path",
                contentDescription = "Preview of taken image",
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )

            if (state.isScanning) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                Text("Scanning image...")
            }

            state.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = { viewModel.startScan(path) }) {
                    Text("Retry Scan")
                }
            }

            if (state.matches.isNotEmpty()) {
                val currentMatch = viewModel.currentMatch
                
                Text(
                    text = "Found: ${currentMatch?.label ?: "Unknown"}",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )

                state.cropBytes?.let { bytes ->
                    Text("Crop loaded (${bytes.size} bytes)")
                }

                Row(modifier = Modifier.padding(top = 16.dp)) {
                    Button(
                        onClick = { viewModel.previousMatch() },
                        enabled = state.currentMatchIndex > 0
                    ) {
                        Text("Previous")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { viewModel.nextMatch() },
                        enabled = state.currentMatchIndex < state.matches.size - 1
                    ) {
                        Text("Next")
                    }
                }

                Button(
                    onClick = { viewModel.sendWrongFeedback() },
                    modifier = Modifier.padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Mark as Wrong")
                }
            }
        }
    }
}
