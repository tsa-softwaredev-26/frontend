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
    // main app controller

    // keeps navigation routes and controls
    nav = rememberNavController()

    // materialtheme adds all color schemes to make styling easier
    MaterialTheme(colorScheme = lightColorScheme()) {
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