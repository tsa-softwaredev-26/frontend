package tech.gloucestercounty.frontend_sd26

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import tech.gloucestercounty.frontend_sd26.ui.HomeScreen

@Composable
@Preview
fun App() {
    nav = rememberNavController()

    MaterialTheme(colorScheme = lightColorScheme()) {
        NavHost(navController = nav, startDestination = Home) {
            composable<Home> {
                HomeScreen()
            }

            composable<TestingPage> {
                Scaffold { innerPaddings ->
                    Column(
                        modifier = Modifier.padding(innerPaddings)
                    ) {
                        Text("testing page, hit back button to go back")
                    }
                }
            }
        }
    }
}