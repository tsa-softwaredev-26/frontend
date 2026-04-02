package tech.gloucestercounty.frontend_sd26

import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

lateinit var nav: NavHostController

// home page route
@Serializable
object Home

// scan page route
@Serializable
object ScanPage

// post scan page route
// added as data class for path parameter
@Serializable
data class PostScan(val path: String)

// settings page route
@Serializable
object SettingsPage