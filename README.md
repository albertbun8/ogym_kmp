# OGym

OGym is a learning project for practicing the end-to-end development of a mobile workout platform: architecture, Kotlin Multiplatform, Compose Multiplatform, HTTP communication, and a Spring Boot backend.

The priority is to understand and evolve each layer progressively, not to deliver the application as quickly as possible.

The current foundation focuses on reliable personal workout tracking. The longer-term product direction is to help members of the same gym voluntarily plan workouts and find compatible training partners without exposing automatic location data. That community direction remains subject to product validation and is not part of the current implementation scope.

## Current status

The current application shell has two main destinations:

- **Home**: start an empty workout or a workout from a template, create a template, and browse recent workout history;
- **Profile**: currently a placeholder for the future user profile and preferences.

Currently:

- workout templates are persisted in PostgreSQL through Spring Data JDBC;
- exercises and workout sessions still use in-memory repositories;
- the shared KMP module contains presentation, domain, data, networking, and SQLDelight persistence code;
- Home loads templates and workout history and can create a workout template;
- a workout can be started empty or from a template;
- the active workout supports adding and reordering exercises, editing sets, completing sets, a rest timer, previous performance, and workout completion;
- the active-workout draft is persisted locally with SQLDelight and restored after an application restart;
- completed workouts are sent to the backend and then displayed in workout history;
- Profile is still a placeholder;
- workout sessions are not yet persisted in PostgreSQL;
- exercise creation, editing, and deletion are not yet exposed by the mobile UI;
- Spring Security, authentication, and user-owned resources are not yet implemented;
- automated tests currently cover selected active-workout domain and repository behavior and PostgreSQL workout-template persistence.

## Current priorities

Before implementing the local gym community capabilities, the project should complete and stabilize the personal workout foundation:

1. persist workout sessions in PostgreSQL;
2. complete workout history and workout details;
3. implement authentication and assign resources to the authenticated user;
4. introduce a minimal profile with privacy and discovery preferences;
5. expand tests around the complete workout lifecycle and backend authorization;
6. validate the local coordination flow with a small group from one gym before finalizing its domain model.

The first candidate community slice is deliberately small:

```text
Select a gym
    ↓
Plan a visit with a time window and visibility
    ↓
Publish a structured partner request
    ↓
Receive and accept or decline a response
    ↓
Expire the visit and request automatically
```

Temporary chat, shared workouts, community programs, feeds, and real-time presence are later capabilities rather than prerequisites for this first experiment.

## Technology stack

### Mobile

- Kotlin Multiplatform
- Compose Multiplatform — shared UI for Android and iOS
- MVVM
- Coroutines, Flow et StateFlow
- Ktor Client
- Kotlin Serialization

### Backend

- Kotlin/JVM 21
- Spring Boot
- API REST
- PostgreSQL, Spring Data JDBC, and Flyway for workout templates
- Spring Security planned for a later stage

## Architecture

The mobile code is organized by feature with `presentation`, `domain`, and `data` layers.

```text
Compose UI
    ↓ actions       ↑ UiState
ViewModel
    ↓
Use case
    ↓
Repository (interface domain)
    ↓
RepositoryImpl (data)
    ↓
Ktor Client
    ↓ HTTP/JSON
Spring Boot Controller
    ↓
Service
    ↓
Repository backend
```

Domain models do not depend on Compose, Ktor, Android, or iOS.

## Project structure

```text
ogym/
├── androidApp/       # Android entry point
├── iosApp/           # iOS entry point and Xcode project
├── shared/
│   └── src/
│       ├── commonMain/   # Shared UI, presentation, domain, data, and networking
│       ├── androidMain/  # Android-specific code when required
│       └── iosMain/      # iOS-specific code when required
├── backend/          # Spring Boot application
├── gradle/           # Version catalog and Gradle Wrapper
```

## Requirements

- JDK 21;
- Docker with Docker Compose for the local PostgreSQL database;
- Android Studio with an Android SDK installed;
- Xcode on macOS to build the iOS application;
- no global Gradle installation is required because the project uses the Gradle Wrapper.

Check the Java version:

```bash
java -version
```

## Run the backend

From the project root:

```bash
docker compose up -d postgres
./gradlew :backend:bootRun
```

The server listens on `http://localhost:8080`. It is bound to `0.0.0.0`, so devices on the local network can also access it through the host machine's local IP address.

Stop the server with `Ctrl+C`.

Stop PostgreSQL while preserving its data with:

```bash
docker compose down
```

The default local database connection can be overridden with `OGYM_DB_URL`,
`OGYM_DB_USER`, and `OGYM_DB_PASSWORD`.

### Available endpoints

```http
GET /exercises
GET /workout-templates
POST /workout-templates
GET /workout-sessions
POST /workout-sessions
```

Example:

```bash
curl http://localhost:8080/workout-templates
```

## Configure the mobile backend address

The Android backend address is passed to `AppDependencies` in `androidApp/src/main/kotlin/com/example/ogym_kmp/MainActivity.kt`.

| Environment | Typical base URL |
|---|---|
| Android Emulator | `http://10.0.2.2:8080` |
| Physical Android device | `http://<HOST_LOCAL_IP>:8080` |
| iOS Simulator on the backend host | `http://127.0.0.1:8080` |
| Physical iPhone | `http://<MAC_LOCAL_IP>:8080` |

For a physical device, the phone and the machine running Spring Boot should normally be on the same network. The firewall must allow traffic on port `8080`.

Cleartext HTTP traffic is currently allowed in the Android manifest for local development only. A production application should use HTTPS.

## Run Android

1. Start the backend.
2. Configure the base URL in `MainActivity.kt`.
3. Open the project in Android Studio.
4. Select an emulator or physical device.
5. Run the Android configuration.

Build the debug APK without running it:

```bash
./gradlew :androidApp:assembleDebug
```

## Run iOS

Building the iOS application requires macOS and Xcode:

1. Start the backend.
2. Check the base URL in `shared/src/iosMain/kotlin/com/example/ogym_kmp/MainViewController.kt`.
3. Open `iosApp/iosApp.xcodeproj` with Xcode.
4. Select a simulator or physical device.
5. Run the application.

The UI remains shared in `commonMain` with Compose Multiplatform. SwiftUI acts as the entry point that hosts the Compose view controller.

## Build and test

Compile the shared Android code and the backend:

```bash
./gradlew :shared:compileAndroidMain :backend:compileKotlin
```

Run backend tests:

```bash
./gradlew :backend:test
```

Run the shared module's Android tests:

```bash
./gradlew :shared:testAndroidHostTest
```

iOS tests require macOS:

```bash
./gradlew :shared:iosSimulatorArm64Test
```

## Contribution principles

- progress through small vertical slices;
- understand one layer before adding another;
- keep business responsibilities out of composables;
- keep the domain independent from technical details;
- prefer immutable state and unidirectional data flow;
- add abstractions only when a concrete need appears;
- accompany important changes with tests and documentation updates.
