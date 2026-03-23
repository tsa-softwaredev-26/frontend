package tech.gloucestercounty.frontend_sd26.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpCenter
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.gloucestercounty.frontend_sd26.ScanPage
import tech.gloucestercounty.frontend_sd26.TestingPage
import tech.gloucestercounty.frontend_sd26.nav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun HomeScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    //TODO: add logo
                    Text(
                        "SPAITRA",
                        fontWeight = FontWeight(700)
                    )
                }
            )
        }
    ) { innerPaddings ->
        Column(
            modifier = Modifier.padding(innerPaddings).padding(8.dp)
        ) {
            Button(
                onClick = {
                    nav.navigate(ScanPage)
                },
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Rounded.CameraAlt,
                        "Scan Icon",
                        modifier = Modifier.width(96.dp).height(96.dp)
                    )
                    Text(
                        "Scan",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    nav.navigate(TestingPage)
                },
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.HelpCenter,
                        "Ask Icon",
                        modifier = Modifier.width(96.dp).height(96.dp)
                    )
                    Text(
                        "Ask",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }
    }
}