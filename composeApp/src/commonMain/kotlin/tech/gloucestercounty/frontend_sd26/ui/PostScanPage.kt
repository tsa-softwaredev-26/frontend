package tech.gloucestercounty.frontend_sd26.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import tech.gloucestercounty.frontend_sd26.AudioRecorder


@Composable
fun PostScanPage(path: String) {

    Scaffold(
        floatingActionButton = { AudioRecorder.FAB() }
    ) { innerPaddings ->
        Column(
            modifier = Modifier.padding(innerPaddings).padding(8.dp)

        ) {

            AsyncImage(
                "file://$path",
                "Preview of taken image"

            )
            //TODO: ktorfit is needed here

        }
    }
}