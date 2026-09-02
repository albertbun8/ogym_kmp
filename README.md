# OGym

OGym is a learning project for practicing the end-to-end development of a workout-tracking mobile application: architecture, Kotlin Multiplatform, Compose Multiplatform, HTTP communication, and a Spring Boot backend.

The priority is to understand and evolve each layer progressively, not to deliver the application as quickly as possible.

## Current status

The MVP has two main destinations:

- **Home**: start an empty workout, browse templates, and view workout history;
- **Profile**: user identity, general statistics, a compact calendar, muscle strength profile, and preferences.

Currently:

- the backend exposes exercises, workout templates, and workout sessions from in-memory repositories;
- the frontend has the corresponding data and domain layers;
- Home loads and displays templates and workout history;
- active-workout draft models define editable sets, explicit weight units, rest-timer state, muscle targets, and local restoration boundaries;
- Profile is still a placeholder;
- starting a workout and viewing workout details are not implemented yet;
- PostgreSQL, Spring Security, and authentication are planned for a later stage.

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
- in-memory repositories for the current prototype
- PostgreSQL and Spring Security planned for a later stage

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
./gradlew :backend:bootRun
```

The server listens on `http://localhost:8080`. It is bound to `0.0.0.0`, so devices on the local network can also access it through the host machine's local IP address.

Stop the server with `Ctrl+C`.

### Available endpoints

```http
GET /exercises
GET /workout-templates
GET /workout-sessions
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
