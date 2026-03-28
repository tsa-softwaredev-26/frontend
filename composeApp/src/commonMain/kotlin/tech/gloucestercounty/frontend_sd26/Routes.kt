package tech.gloucestercounty.frontend_sd26

import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

lateinit var nav: NavHostController

@Serializable
object Home

@Serializable
object ScanPage

@Serializable
data class PostScan(val path: String)

@Serializable
object SettingsPage