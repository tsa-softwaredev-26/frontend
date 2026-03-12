package tech.gloucestercounty.frontend_sd26

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

lateinit var nav: NavHostController

@Serializable
object Home

@Serializable
object TestingPage