package tech.gloucestercounty.frontend_sd26

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        Scaffold { innerPaddings ->
            Column(
                modifier = Modifier.padding(innerPaddings)
            ) {
                Button(
                    onClick = {}
                ) {
                    Text("test button")
                }
            }
        }
    }
}