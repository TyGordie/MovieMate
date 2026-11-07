MovieMate corrected working copy

Contents:
- AndroidStudioProject/MovieMate2: corrected Android Studio project
- BackendAPI/MovieMateAPI: cleaned Node.js REST API source
- SETUP_INSTRUCTIONS.md: exact setup, Firebase, API and test steps
- PROJECT_STATUS.md: what was changed and what was/was not verified

Important:
- Open the Android project folder that contains settings.gradle.kts.
- The Firebase Admin service-account JSON is intentionally not included. Copy your private key locally into BackendAPI/MovieMateAPI as serviceAccountKey.json and set GOOGLE_APPLICATION_CREDENTIALS as described in SETUP_INSTRUCTIONS.md.
- The Android API base URL still uses the previous local address 192.168.10.179. Change it if your computer's Wi-Fi address has changed.
- The Android Gradle build could not be run in the preparation environment because Gradle 9.2.1 could not be downloaded. Build and test it in Android Studio before submitting.
- Read PROJECT_STATUS.md before treating any feature as verified.
