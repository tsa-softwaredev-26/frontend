package tech.gloucestercounty.frontend_sd26

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform