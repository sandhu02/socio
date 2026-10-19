# CLAUDE.md - Socio Project Guide

## Project Overview
**Socio** is a full-stack social video-sharing and real-time chat application consisting of:
1. **Android Client (`c:\MyFiles\Android_Projects\Socio`)**: Native Android app written in Kotlin and Jetpack Compose (Material 3) utilizing Retrofit for REST APIs, Socket.IO for real-time messaging, Media3 ExoPlayer for video streaming, and Firebase Cloud Messaging (FCM) for push notifications.
2. **Backend Server (`c:\MyFiles\Web Projects\socio`)**: Custom Node.js / Express service using MongoDB (Mongoose), Cloudinary for video asset storage, Socket.IO for bidirectional chat communication, and Firebase Admin SDK for multicast push notifications.

---

## Technical Stack & Requirements

### Android Client
- **Language**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose with Material 3 (Compose BOM `2024.09.00`)
- **Architecture**: MVVM with `StateFlow` and unidirectional data flow (UDF)
- **Compile / Target SDK**: Android 35 (VanillaIceCream) / Min SDK: 24 (Android 7.0)
- **Java / JDK**: JDK 17 LTS (configured via `org.gradle.java.home=C:/Program Files/Java/jdk-17`)
- **Android Gradle Plugin (AGP)**: 8.12.2
- **Networking**: Retrofit 2.9.0, OkHttp 4.10.0, GsonConverterFactory
- **Real-Time Messaging**: `io.socket:socket.io-client:2.1.0`
- **Media Playback**: `androidx.media3:media3-exoplayer:1.3.1`, `media3-ui:1.3.1`
- **Push Notifications**: Firebase Messaging (BOM `34.1.0`)

### Backend Server
- **Runtime**: Node.js (Express, `cors`, `cookie-parser`, `dotenv`)
- **Database**: MongoDB (via Mongoose)
- **Authentication**: JWT (`jsonwebtoken`) and `bcrypt`
- **File Uploads**: `multer` (memoryStorage) streamed directly to Cloudinary
- **WebSockets**: Socket.IO (`io(server)`)
- **Push Notifications**: Firebase Admin SDK (`firebase-admin`)

---

## Build, Test & Development Commands

### Android
```powershell
# Set Java 17 environment (if not picked up automatically from gradle.properties)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"

# Compile Kotlin debug sources
.\gradlew compileDebugKotlin

# Assemble Debug APK (outputs to app/build/outputs/apk/debug/app-debug.apk)
.\gradlew assembleDebug

# Run unit tests
.\gradlew testDebugUnitTest

# Clean build artifacts
.\gradlew clean
```

### Backend (located in `c:\MyFiles\Web Projects\socio`)
```bash
# Install dependencies
npm install

# Start development server (defaults to port 3000)
npm start
# or
node app.js
```

---

## Android Project Architecture

```
app/src/main/
├── AndroidManifest.xml
├── java/com/example/cusotmauthtest/
│   ├── MainActivity.kt                # App entrypoint, permission requests, edge-to-edge
│   ├── AuthTestNavigation.kt          # Compose NavHost and AuthTestScreens definitions
│   ├── ViewModelFactory.kt            # Central ViewModelProvider.Factory with Context injection
│   ├── ui/theme/                      # Material 3 Theme (Color, Type, Theme)
│   ├── retrofit/
│   │   ├── model.kt                   # DTOs, API Requests/Responses, ChatMessage, sealed Response
│   │   ├── retrofit.kt                # AuthApiService definition, OkHttp client, dynamic BASE_URL
│   │   └── token.kt                   # SharedPreferences persistence (JWT, username, FCM token)
│   ├── firebase/
│   │   └── PushNotificationService.kt # FirebaseMessagingService (token updates, system notifications)
│   └── screens/
│       ├── SplashScreen.kt / SplashViewModel.kt         # Token validation & auto-routing
│       ├── SignInScreen.kt / SignInViewModel.kt         # Login, URL config, FCM token upload
│       ├── RegisterScreen.kt / RegisterViewModel.kt     # Account creation with validation
│       ├── HomeScreen.kt / HomeScreenViewModel.kt       # Video feed, delete video, logout
│       ├── UploadVideScreen.kt / UploadVideoViewModel.kt# Video picker, multipart streaming upload
│       ├── ViewVideoScreen.kt                           # Media3 ExoPlayer video playback
│       ├── UsersForChatScreen.kt / UsersForChatViewModel.kt # User directory for chat initiation
│       ├── ChatScreen.kt / ChatViewModel.kt             # Real-time Socket.IO chat + bubble UI
│       └── SharedViewModel.kt                           # SharedVideoViewModel (cross-screen data)
└── res/
    ├── values/ (strings.xml, themes.xml, colors.xml)
    ├── xml/ (network_security_config.xml, backup_rules.xml, data_extraction_rules.xml)
    └── mipmap-*/ (Launcher icons)
```

---

## Core Guidelines & Conventions

1. **State Management**:
   - Every screen has a dedicated ViewModel exposing a `StateFlow<UiState>`.
   - UI components collect state via `collectAsState()` and dispatch user actions via ViewModel methods.
   - Operations that alter global state (e.g. logout) clear local session tokens via `clearAuth(context)`.

2. **Network Communications**:
   - The app uses a configurable base URL managed via `getBaseUrl(context)` and `setBaseUrl(context, url)`. Default is the development tunnel or emulator IP (`http://10.0.2.2:3000/`).
   - Cleartext HTTP traffic is explicitly permitted in `network_security_config.xml` to allow seamless local network / emulator development.
   - Requests needing authentication automatically attach `Authorization: Bearer <token>` through the OkHttp interceptor in `retrofit.kt`.

3. **Media Handling**:
   - Video uploads use `ActivityResultContracts.GetContent()` with MIME type `video/*`.
   - Video files are copied from content URIs to temporary cache files, streamed as `MultipartBody.Part`, and cleaned up immediately after request completion.
   - Video playback uses `androidx.media3.exoplayer.ExoPlayer` bounded to the Composable lifecycle using `DisposableEffect`.

4. **Chat & Real-Time Sync**:
   - Socket.IO connection is authenticated during handshake using `{ auth: { token: "<JWT>" } }`.
   - Messages are emitted on the `"chat message"` event with `{ toUserName, message }`.
   - The client tracks sent messages in local state immediately upon emit, and listens to incoming messages from the server on `"chat message"`.
