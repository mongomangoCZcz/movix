# Implementation Plan - Movix Android App

Create a native Android application that replicates the functionality of the provided Kodi addon for movie/TV show streaming and downloading via Webshare and TMDb.

## User Review Required

> [!IMPORTANT]
> The app will require a Webshare VIP account for streaming/downloading, as specified in the original logic.
> TMDb API key `8badb6099e4a0e44f009dac72c4df37a` will be used.
> VPS Server `http://176.102.65.44:5000` will be used as a primary source for some content.

## Proposed Changes

### Build Configuration & Dependencies
- Add Retrofit, OkHttp, Moshi (JSON), TikXml (XML), Navigation Compose, Hilt, Media3, Coil, DataStore, and WorkManager to `libs.versions.toml` and `app/build.gradle.kts`.

---

### Data Layer
#### [NEW] [Network API Interfaces](file:///Users/tomaspycha/AndroidStudioProjects/movix/app/src/main/java/com/example/movix/data/remote/)
- `TmdbrApiService`: Retrofit interface for TMDb.
- `WebshareApiService`: Retrofit interface for Webshare (XML based).
- `VpsApiService`: Retrofit interface for the VPS server (JSON based).

#### [NEW] [Repositories](file:///Users/tomaspycha/AndroidStudioProjects/movix/app/src/main/java/com/example/movix/data/repository/)
- `AuthRepository`: Handles Webshare login, salt retrieval, and token storage using DataStore.
- `MediaRepository`: Handles searching TMDb, fetching seasons/episodes, and searching Webshare for streams.
- `DownloadRepository`: Manages download tasks and local file access.

---

### Domain Layer
#### [NEW] [Utilities](file:///Users/tomaspycha/AndroidStudioProjects/movix/app/src/main/java/com/example/movix/domain/utils/)
- `Md5Crypt`: Port of the Python `md5crypt` logic to Kotlin for Webshare password hashing.
- `FilenameAnalyzer`: Port of `analyze_filename` logic to parse quality, codec, and language from filenames.

---

### UI Layer (Jetpack Compose)
#### [NEW] [Screens](file:///Users/tomaspycha/AndroidStudioProjects/movix/app/src/main/java/com/example/movix/ui/screens/)
- `MainScreen`: Home screen with categories (Search, A-Z, Genres, Downloads, Settings).
- `SearchScreen`: Input field and results list.
- `DiscoverScreen`: List movies/TV shows by category/genre.
- `SeasonEpisodeScreen`: Navigation for TV shows.
- `StreamSelectionScreen`: Dialog or screen to select from available Webshare streams.
- `PlayerScreen`: Media3 ExoPlayer wrapper for streaming and local playback.
- `SettingsScreen`: Webshare credentials and storage configuration.

#### [NEW] [Navigation](file:///Users/tomaspycha/AndroidStudioProjects/movix/app/src/main/java/com/example/movix/ui/navigation/)
- Setup `NavHost` and destinations.

---

### Background Services
#### [NEW] [WorkManager](file:///Users/tomaspycha/AndroidStudioProjects/movix/app/src/main/java/com/example/movix/worker/)
- `DownloadWorker`: Handles file downloading in the background with notifications.

## Verification Plan

### Automated Tests
- Unit tests for `Md5Crypt` to ensure correct hashing matches Webshare requirements.
- Unit tests for `FilenameAnalyzer`.
- API integration tests (mocked).

### Manual Verification
- Deploy to an Android device/emulator.
- Test login with Webshare credentials.
- Test TMDb search and navigation.
- Test stream selection and playback using Media3.
- Test background downloading and local playback.
