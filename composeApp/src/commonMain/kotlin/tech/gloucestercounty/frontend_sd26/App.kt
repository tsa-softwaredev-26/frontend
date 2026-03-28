package tech.gloucestercounty.frontend_sd26

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import tech.gloucestercounty.frontend_sd26.ui.HomeScreen
import tech.gloucestercounty.frontend_sd26.ui.PostScanPage
import tech.gloucestercounty.frontend_sd26.ui.Scan
import tech.gloucestercounty.frontend_sd26.ui.Settings

@Composable
@Preview
fun App() {
    nav = rememberNavController()

    MaterialTheme(colorScheme = lightColorScheme()) {
        NavHost(navController = nav, startDestination = Home) {
            composable<Home> {
                HomeScreen()
            }

            composable<ScanPage> {
                Scan()
            }

            composable<PostScan> {
                PostScanPage(it.toRoute<PostScan>().path)
            }

            composable<SettingsPage> {
                Settings()
            }
        }
    }
}