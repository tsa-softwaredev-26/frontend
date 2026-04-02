# The Frontend
The Frontend of Spaitra is a mobile app that can run on Android or iOS. It is written in Kotlin using Jetpack Compose.

### The Important Files
There are **many** files in this app. Not all of them are useful to read, but all are necessary to run. This is the list of the ones that are important:

- /composeApp/
  - src/
    - androidMain/
      - kotlin/
        - tech.gloucestercounty.frontend_sd26/
          - MainActivity.kt
      - AndroidManifest.xml
    - commonMain/
      - kotlin/
        - tech.gloucestercounty.frontend_sd26/
          - api/
            - BackendAPI.kt
            - Testing.http
          - ui/
            - HomeScreen.kt
            - PostScanPage.kt
            - Scan.kt
            - Settings.kt
          - App.kt
          - AudioRecorder.kt
          - Routes.kt
    - iosMain/
      - kotlin/
        - tech.gloucestercounty.frontend_sd26/
          - MainViewController.kt
    - main/
      - res/drawable/
        - spaitra.xml
  - build.gradle.kts
- /gradle/
  - libs.versions.toml
- settings.gradle.kts