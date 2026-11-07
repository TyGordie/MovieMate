# MovieMate Android Application

MovieMate is an Android movie discovery application. Users can create an account, sign in, search a Firestore-backed movie catalogue through a REST API, view movie information, maintain a personal watchlist and submit ratings.

## Features in this version

- Firebase Authentication for email/password registration and login.
- Google single sign-on using Android Credential Manager and Firebase Authentication (requires Google Play services and correct Firebase OAuth configuration).
- Express REST API connected to Cloud Firestore.
- Movie search and movie details.
- Personal watchlist and movie ratings.
- Local movie and watchlist cache for offline reading.
- Offline watchlist and rating actions are placed in a retry queue and retried when Android reports an internet-capable network.
- English, isiXhosa and isiZulu interface resources.
- Settings for language, notifications and dark mode.
- Firebase Cloud Messaging receiver for push notifications.
- Unit tests for input validation and a GitHub Actions build/test workflow.

## Technology

- Kotlin and Jetpack Compose
- AndroidX Navigation Compose and AppCompat locale support
- Firebase Authentication, Cloud Firestore and Firebase Cloud Messaging
- Retrofit and Gson
- Node.js, Express and Firebase Admin SDK
- JUnit 4 and GitHub Actions

## Project structure

- `app/src/main/java`: Android screens, navigation, models, API client, repository, local cache, network sync and FCM receiver.
- `app/src/main/res/values`: English strings.
- `app/src/main/res/values-xh`: isiXhosa strings.
- `app/src/main/res/values-zu`: isiZulu strings.
- `app/src/test`: unit tests.
- `.github/workflows/android.yml`: automated Android build and unit-test workflow.

## Configure and run the Android app

1. Install Android Studio and a JDK 17-compatible Android build environment.
2. Open the `MovieMate2` folder in Android Studio.
3. Confirm `app/google-services.json` belongs to the Firebase project and contains the Android package `com.example.moviemate`.
4. In `network/RetroFitInstance.kt`, change `BASE_URL` to the current computer LAN address when using a phone or BlueStacks on the same Wi-Fi network. The API must be running on that computer. For a hosted deployment, use the hosted HTTPS URL instead.
5. Sync Gradle, then run the `app` configuration on an Android phone.

The project includes a local-network HTTP URL for development. Do not use cleartext HTTP for a public production deployment; deploy the API over HTTPS and update the Android base URL.

## Configure and run the REST API

The backend is in the separate `MovieMateAPI` folder. Follow its README. A Firebase Admin service-account JSON is required locally, but it is deliberately not included in the source bundle. Keep it outside Git and set `GOOGLE_APPLICATION_CREDENTIALS` to its full path before starting the API.

## API endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/health` | API health check |
| GET | `/movies` | List movies |
| GET | `/movies?query=avatar` | Search movies |
| GET | `/movies/{id}` | Get one movie |
| GET | `/api/watchlist?userId=...` | Read a user's watchlist |
| POST | `/api/watchlist?userId=...` | Add a movie to a watchlist |
| DELETE | `/api/watchlist/{movieId}?userId=...` | Remove a movie |
| POST | `/api/ratings` | Save a movie rating |

## Offline behaviour and synchronisation

Movie search results and movie details are cached locally after a successful API response. The last downloaded watchlist is cached per Firebase user. Watchlist additions, removals and ratings are saved locally when the API is unavailable. The app queues those actions and retries them when a network becomes available.

This is a lightweight local persistence implementation for the coursework prototype. It is not a conflict-resolution system: repeated ratings can create multiple records, and server-side account authorisation should be added before public production deployment.

## Notifications

The app contains a Firebase Cloud Messaging service and requests Android notification permission when the user enables notifications in Settings. Configure Firebase Cloud Messaging for the same Firebase project and send a test notification to a registered app installation to verify delivery. The app's receiver displays notification/data messages when notifications are enabled.

## Languages

The application interface includes English, isiXhosa and isiZulu resources. Select the language from Settings. Movie titles and descriptions come from the database and are displayed as stored; they are not automatically translated.

## Tests and CI

Run locally from the project root:

```bash
./gradlew testDebugUnitTest assembleDebug
```

GitHub Actions runs the same unit-test and debug-build tasks for pushes and pull requests to `main` or `master`.

## Publication preparation

The project has an app icon and a release build configuration. Before publishing, create and securely store a release signing key, configure the release signing settings locally or through protected CI secrets, test the signed artifact on a physical Android phone, complete the Play Console store listing and privacy/data-safety declarations, and upload the required screenshots and assets. No signing key or Play Console upload is included in this project archive.

## Release notes

### Final PoE working copy
- Added local offline action queue and retry-on-network sync for watchlist changes and ratings.
- Added Firebase Cloud Messaging receiver and Android notification permission handling.
- Connected the dark-mode setting to the app-wide Compose theme.
- Completed English, isiXhosa and isiZulu resources for authentication and primary app screens.
- Added input validation, diagnostic logging, unit tests and GitHub Actions workflow.
- Improved API request validation, health check and server logging.

## References

- Android Developers. (n.d.). *App-specific language preferences*. https://developer.android.com/guide/topics/resources/app-languages
- Android Developers. (n.d.). *ConnectivityManager*. https://developer.android.com/reference/android/net/ConnectivityManager
- Firebase. (n.d.). *Firebase Authentication*. https://firebase.google.com/docs/auth
- Firebase. (n.d.). *Firebase Cloud Messaging*. https://firebase.google.com/docs/cloud-messaging
- Firebase. (n.d.). *Cloud Firestore*. https://firebase.google.com/docs/firestore
- Retrofit. (n.d.). *Retrofit documentation*. https://square.github.io/retrofit/
- Express. (n.d.). *Express documentation*. https://expressjs.com/

## Demonstration video

Add the unlisted/public demonstration-video URL here after recording the required voice-over walkthrough on a physical Android phone.
