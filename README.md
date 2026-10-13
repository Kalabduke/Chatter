# Chatter

An Android chat and calling app: sign up with an email address, message people, and start one-to-one voice or video calls with them.

## Features

- **Accounts** — email/password sign up and sign in with session persistence (Firebase Auth).
- **Messaging** — conversation list and message threads, powered by ZEGOCLOUD's In-app Chat UIKit (ZIMKit).
- **Start a chat by email** — type someone's address and it opens a peer conversation with them.
- **Voice and video calls** — call invitations and the in-call UI from ZEGOCLOUD's prebuilt call UIKit.
- **Profile and sign out** — shows the signed-in address and clears both the Firebase and the ZEGO session.

## Tech stack

| Concern | Choice |
| --- | --- |
| Language / UI | Kotlin 2.0, Jetpack Compose, Material 3 |
| Build | AGP 8.7.3, minSdk 26, targetSdk 34, compileSdk 35 |
| DI | Hilt |
| Navigation | Navigation Compose |
| Auth & backend | Firebase — Auth, Realtime Database, Firestore, Storage, Analytics |
| Chat & calls | ZEGOCLOUD — In-app Chat UIKit + prebuilt call UIKit |
| Images | Coil |
| Permissions | PermissionX |
| Logging | Timber, XLog |

## Project structure

```
app/src/main/java/com/example/chatter/
├── Const.kt                       ZEGO appID/appSign (from BuildConfig) + the email→userID helper
├── MainActivity.kt                ZEGO call-service setup, permission handling, NavHost
├── MainApp.kt                     Hilt application class
├── feature/
│   ├── auth/signin/               SignInScreen + SignInViewModel
│   ├── auth/signup/               SignUpScreen + SignUpViewModel
│   ├── conversationscreen/        ConversationScreen — ZIMKit conversation list, profile, logout
│   └── home/                      HomeScreen — start a voice or video call
└── ui/                            theme + Compose animation helpers
```

## Getting started

You need **JDK 17** and the **Android SDK 35**. Point Gradle at your SDK with `sdk.dir` in `local.properties` or the `ANDROID_HOME` variable.

**1. Firebase.** Create a Firebase project, register an Android app with the package name `com.example.chatter`, and download its `google-services.json` into `app/`. That file is gitignored — each developer supplies their own, and it is not in this repository.

**2. ZEGOCLOUD.** Create a project in the ZEGOCLOUD console, subscribe it to **In-app Chat**, and copy the AppID and AppSign. Put them in `local.properties` in the project root (also gitignored):

```properties
zegoAppId=YOUR_APP_ID
zegoAppSign=YOUR_APP_SIGN
```

The build reads them and injects `BuildConfig.ZEGO_APP_ID` / `BuildConfig.ZEGO_APP_SIGN`, which `Const.kt` exposes as `appID` and `appSign`.

**3. Build.**

```bash
./gradlew assembleDebug      # debug APK
./gradlew installDebug       # build and install on a connected device
```

## Security notes

- **No credentials in the source.** The ZEGO AppID and AppSign are injected from `local.properties` at build time; `local.properties` and `app/google-services.json` are both gitignored. Never hardcode either value again.
- **Rotate a leaked AppSign.** The AppSign is the secret ZEGOCLOUD signs authentication tokens with. Anyone who has it can mint a valid token for any user ID — impersonating your users and spending your minutes. If it ever reached a commit, a chat, or a shipped APK, rotate it in the console. Note that any built APK contains whatever the AppSign was at build time, so a leaked build leaks the credential.
- **Generate tokens server-side in production.** Shipping the AppSign in the client is the quick path for prototypes; ZEGOCLOUD's guidance is that production apps fetch user tokens from their own server instead. That is the next step for this app.
- **Lock down the Firebase side.** Restrict the API key to this package name and signing certificate, and make sure your Realtime Database, Firestore and Storage rules are not world-readable or writable.
- **One identity per user.** ZEGO accepts user IDs of at most 32 letters and digits, so an email address cannot be used as one. `zegoUserIdFor()` in `Const.kt` derives a stable ID from the address instead. Every place that identifies a user to ZEGO must use that helper — mixing a Firebase uid with a raw email registers the same person under two IDs and stops chats and calls from reaching them.

## Known gaps

- **Offline call invitations don't arrive yet.** `enableFCMPush()` is called, but `firebase-messaging` is not a dependency, there is no messaging service registered, and `POST_NOTIFICATIONS` is never requested, so push-delivered invites cannot land.
- **Tests are placeholders.** Only the generated `ExampleUnitTest` and `ExampleInstrumentedTest` exist.
- The application ID is still the IDE default `com.example.chatter`.
