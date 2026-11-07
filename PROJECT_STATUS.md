# MovieMate code review and verification status

## Changes made in this working copy

- Changed the launcher activity to AppCompatActivity so AppCompat application-language changes can be applied to the activity.
- Connected the persisted dark-mode setting to the app-wide Compose theme.
- Completed interface strings for English, isiXhosa and isiZulu across login, registration, home, search, movie details and settings.
- Added local caching for movie results and user watchlists.
- Added a persistent queue for offline watchlist additions/removals and movie ratings.
- Added WorkManager work constrained to a connected network so pending actions can retry in the background.
- Added Firebase Cloud Messaging receiver, notification channel, notification permission handling and topic subscription (`moviemate_updates`).
- Added email/rating validation and additional diagnostic logging.
- Added unit tests for input validation.
- Added GitHub Actions workflow for unit tests and debug APK assembly.
- Improved API health checking, request logging, input validation and Firebase Admin credential loading through `GOOGLE_APPLICATION_CREDENTIALS`.
- Replaced the default Android launcher graphic with a MovieMate-themed clapperboard icon.

## Checks completed here

- All Android XML files parsed successfully.
- English, isiXhosa and isiZulu resource files contain the same 58 string keys.
- Kotlin `R.string` references were checked against the supplied resources; no missing string IDs were found (Google Services generates `default_web_client_id`).
- Firebase project/package configuration matches `moviemate-489cb` / `com.example.moviemate`.
- Backend JavaScript passed `node --check`.
- Backend `/health` endpoint returned `{"status":"ok","service":"MovieMate API"}` when started locally.

## Not verified here

- Android Gradle build and Android unit tests: not run because Gradle 9.2.1 was not cached and this environment could not download it from `services.gradle.org`.
- Google SSO: requires Firebase OAuth provider configuration, correct signing fingerprints and a device with working Google Play services.
- Push delivery: requires Firebase Cloud Messaging setup, user permission and a test message sent to the `moviemate_updates` topic.
- Offline synchronisation: code is present but must be tested on a device with the API and Firestore available.
- Physical-phone operation, release signing and Play Console upload: not performed.
- Firebase Storage/blob storage and User Defined 4/5: require confirmation against the Part 1 design and lecturer feedback.
