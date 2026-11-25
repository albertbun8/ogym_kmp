# OGym

OGym is a gym workout tracker that will grow into a **"Pinterest for workouts"**: a place to discover, save, follow and share workout templates.

- **Tracker first.** Planning, recording and reviewing workouts must be great on its own.
- **Then discovery.** Users publish templates, browse them by goal, split, equipment or level, save them into collections, and copy them with attribution.
- **Then social.** Follow, feed, comments and photos come once discovery works.

Android comes first, iOS after.

OGym is also a learning project for end-to-end mobile development: Kotlin Multiplatform, Compose Multiplatform, and a Spring Boot backend. The priority is to understand each layer and decision, not to ship as fast as possible.

## Features

The app has two main destinations.

**Home**
- Start an empty workout or a workout from a template.
- Create, edit and delete workout templates.
- Browse recent workout history and the current week.

**Active workout** (a bottom sheet above the app, which can be collapsed without losing the workout)
- Add and reorder exercises, edit and complete sets (weight and reps).
- Rest timer, previous performance for each exercise, targeted muscle groups.
- The draft is saved locally and restored after an app restart.
- Finished workouts are sent to the backend and appear in the history.

**Profile**
- Theme setting: Light, Dark or System, persisted across restarts.
- The full profile page is not built yet.

### Not implemented yet
- Workout sessions and exercises are kept **in memory** on the backend: they are lost when the server restarts. Only templates are stored in PostgreSQL.
- No authentication or user accounts.
- No custom exercises, full history screen or statistics.
- The iOS app has not been compiled or run yet.

## Technology stack

### Mobile (`shared/`, `androidApp/`, `iosApp/`)
- Kotlin Multiplatform, with Compose Multiplatform for a UI shared between Android and iOS
- MVVM with unidirectional data flow: `ViewModel` + `StateFlow` + immutable UI state
- Coroutines and Flow
- Koin for dependency injection
- Navigation Compose (type-safe routes)
- Ktor Client and kotlinx.serialization
- SQLDelight for the local database (active workout draft, cached data)
- DataStore Preferences for settings
- Tests: kotlin-test, kotlinx-coroutines-test, Turbine, hand-written fakes

### Backend (`backend/`)
- Kotlin on JVM 21, Spring Boot, REST
- PostgreSQL 17, Spring Data JDBC, Flyway migrations
- Spring Security is planned with user accounts

## Architecture

The mobile code is organized **by feature**, and each feature is split into layers:

```text
feature/<name>/
├── presentation/   Compose screens, ViewModels, UI state
├── domain/         models, repository interfaces, use cases (no Compose, Ktor or platform code)
├── data/           repository implementations, API and database access
└── di/             Koin module
```

Data flows in one direction:

```text
Compose UI
    ↓ actions       ↑ UiState
ViewModel
    ↓
Use case (when it adds business logic)
    ↓
Repository (domain interface)
    ↓
Repository implementation (data)
    ├── SQLDelight / DataStore (local)
    └── Ktor Client ── HTTP/JSON ──▶ Spring Boot controller → service → repository → PostgreSQL
```

Shared code that is not tied to a feature lives in `core/` (database, DataStore, design system, DI setup, navigation, network, preferences, common helpers).

The backend follows the same feature split: `feature/<name>/{api, application, domain, infrastructure}`.

## Project structure

```text
ogym/
├── androidApp/       # Android entry point (Application, Activity)
├── iosApp/           # iOS entry point (SwiftUI host) and Xcode project
├── shared/
│   └── src/
│       ├── commonMain/      # Shared UI, presentation, domain, data
│       ├── androidMain/     # Android-specific implementations
│       ├── iosMain/         # iOS-specific implementations
│       ├── commonTest/      # Shared unit tests and fakes
│       ├── androidHostTest/ # Tests run on the JVM (e.g. Koin graph check)
│       └── iosTest/
├── backend/          # Spring Boot application
├── gradle/           # Version catalog and Gradle Wrapper
└── compose.yaml      # Local PostgreSQL
```

## Requirements

- JDK 21
- Docker with Docker Compose (local PostgreSQL)
- Android Studio with an Android SDK
- macOS with Xcode to build the iOS app
- No global Gradle installation: the project uses the Gradle Wrapper

## Run the backend

From the project root:

```bash
docker compose up -d postgres
./gradlew :backend:bootRun
```

The server listens on port `8080` and is bound to `0.0.0.0`, so devices on the same local network can reach it through the host's local IP address. Stop it with `Ctrl+C`.

Stop PostgreSQL while keeping its data:

```bash
docker compose down
```

The database connection can be overridden with `OGYM_DB_URL`, `OGYM_DB_USER` and `OGYM_DB_PASSWORD`.

### Endpoints

```http
GET    /exercises
GET    /workout-templates
POST   /workout-templates
PUT    /workout-templates/{id}
DELETE /workout-templates/{id}
GET    /workout-sessions
POST   /workout-sessions
```

Example:

```bash
curl http://localhost:8080/workout-templates
```

## Configure the backend address in the app

The base URL is currently hardcoded and passed to `initKoin`:

| Platform | File |
|---|---|
| Android | `androidApp/src/main/kotlin/com/example/ogym_kmp/OGymApplication.kt` |
| iOS | `shared/src/iosMain/kotlin/com/example/ogym_kmp/core/di/KoinIos.kt` |

| Environment | Typical base URL |
|---|---|
| Android Emulator | `http://10.0.2.2:8080` |
| Physical Android device | `http://<HOST_LOCAL_IP>:8080` |
| iOS Simulator on the backend host | `http://127.0.0.1:8080` |
| Physical iPhone | `http://<HOST_LOCAL_IP>:8080` |

A physical device must be on the same network as the machine running the backend, and the firewall must allow port `8080`. Cleartext HTTP is allowed in the Android manifest for local development only; a production build will use HTTPS.

## Run Android

1. Start the backend.
2. Set the base URL in `OGymApplication.kt`.
3. Open the project in Android Studio, select an emulator or a device, and run `androidApp`.

Build the debug APK without running it:

```bash
./gradlew :androidApp:assembleDebug
```

## Run iOS

Requires macOS and Xcode.

1. Start the backend.
2. Check the base URL in `KoinIos.kt`.
3. Open `iosApp/iosApp.xcodeproj` in Xcode, select a simulator or a device, and run.

The UI is shared from `commonMain` with Compose Multiplatform; SwiftUI only hosts the Compose view controller.

## Build and test

Compile the shared Android code and the backend:

```bash
./gradlew :shared:compileAndroidMain :backend:compileKotlin
```

Run the shared tests on the JVM:

```bash
./gradlew :shared:testAndroidHostTest
```

Run the backend tests:

```bash
./gradlew :backend:test
```

Run the iOS tests (macOS only):

```bash
./gradlew :shared:iosSimulatorArm64Test
```

## Contribution principles

- Progress through small vertical slices: one feature through all layers.
- Keep business logic out of composables, and the domain independent from technical details.
- Prefer immutable state and unidirectional data flow.
- Add abstractions only when a concrete need appears.
- One branch per change, small commits, [Conventional Commits](https://www.conventionalcommits.org/) for commit messages and PR titles.
- Update tests and documentation together with the code they cover.
