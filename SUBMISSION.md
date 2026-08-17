# BMI Tracker — Submission

Android app for tracking BMI across multiple user profiles, with Firebase
authentication and a weekly weight-history chart.

Submitted for the IV Innovations application assignment by **Asit Kumar Giri**.

---

## Package contents

| Item | What it is |
|---|---|
| `app-debug.apk` | Ready to install — nothing to build |
| `bmi-tracker/` | Full source, including `google-services.json` |
| `README.md` | Full documentation: architecture, libraries, design decisions |

---

## Quickest way to run it

Install the APK directly — no toolchain required.

```bash
adb install app-debug.apk
```

Or copy `app-debug.apk` to an Android phone, tap it, and allow installation
from unknown sources.

**Requires Android 7.0 (API 24) or newer.** Verified on a Samsung Galaxy M35
running Android 16.

---

## Building from source

**Requirements**

- JDK 17 or newer (Android Studio's bundled JBR works)
- Android SDK Platform **37** and build-tools **37.0.0**

**Build**

```bash
cd bmi-tracker
./gradlew :app:assembleDebug        # output: app/build/outputs/apk/debug/
./gradlew :app:testDebugUnitTest    # 69 unit tests
```

Or open `bmi-tracker/` in Android Studio and press Run.

If Gradle cannot locate your SDK, create `local.properties` in the project root:

```properties
sdk.dir=C:/Users/<you>/AppData/Local/Android/Sdk
```

Use forward slashes. In `.properties` files a single backslash is an escape
character, so `C:\Users\...` parses as `C:Users...` and the build fails with a
confusing `Invalid file path`.

---

## Please read: Google Sign-In when rebuilding

`google-services.json` is included, so the project builds and runs as-is.

The **supplied APK** is signed with the developer's debug keystore, whose SHA-1
fingerprint is registered in the Firebase project. Google sign-in therefore
works out of the box on the prebuilt APK.

If you **rebuild from source on your own machine**, Gradle signs the APK with
*your* debug keystore, which has a different SHA-1. Firebase will reject the
Google sign-in request:

- ✅ Email/password sign-in, registration, and password reset still work
- ❌ Google sign-in will fail until your fingerprint is registered

To enable it on a rebuild, add your own debug SHA-1 to the Firebase project:

```bash
keytool -list -v -keystore ~/.android/debug.keystore \
  -alias androiddebugkey -storepass android -keypass android
```

This is how Firebase's certificate binding is designed to work, not a defect in
the app. **Installing the supplied APK avoids the issue entirely**, which is why
it is included.

---

## Requirements implemented

All seven, including the three marked as optional.

| # | Requirement | Status |
|---|---|---|
| 1 | Login screen with Google sign-in | ✅ |
| 2 | Account creation + password reset | ✅ |
| 3 | User details form with validation | ✅ |
| 4 | BMI calculation + category display | ✅ |
| 5 | Settings — update height and weight | ✅ |
| 6 | Weight history graph | ✅ |
| 7 | Multi-user profile support | ✅ |

**Bonus items**

- ✅ Authentication state persists across restarts
- ✅ Custom-drawn charts — both the BMI gauge and the weight chart are hand-drawn
  on a Compose `Canvas` rather than pulled from a charting library
- ✅ Error handling — Firebase error codes mapped to readable messages, distinct
  empty states, offline handling

---

## Tech stack

Kotlin, Jetpack Compose, Material 3, MVVM with repositories, Hilt, Room,
DataStore, Firebase Auth with Credential Manager. AGP 9.3.1 / Gradle 9.7.0.

Full rationale for each choice is in `README.md`.

---

## Testing

```bash
./gradlew :app:testDebugUnitTest
```

**69 unit tests**, covering BMI arithmetic, WHO category boundaries, unit
conversions, form validation, and weight-history windowing. The domain layer has
no Android dependencies, so all of it runs on the JVM without an emulator.
