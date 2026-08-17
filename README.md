# BMI Tracker

An Android app for tracking BMI across multiple user profiles, with Firebase
authentication and a weekly weight-history chart.

Built for the IV Innovations application assignment.

---

## Status

| # | Feature | State |
|---|---------|-------|
| 0 | Project scaffold, theme, navigation graph | ✅ Done |
| — | Data layer + BMI domain core | ✅ Done |
| 3 | User details form + validation | ✅ Done |
| 1 | Login screen — Google sign-in | ⬜ Planned |
| 2 | Account creation + password reset | ⬜ Planned |
| 4 | BMI calculation + category display | ⬜ Planned |
| 5 | Settings — update height/weight | ⬜ Planned |
| 6 | Weight history graph | ⬜ Planned |
| 7 | Multi-user profiles | ⬜ Planned |

---

## Tech stack

| Concern | Choice | Why |
|---|---|---|
| Language | Kotlin 2.3.20 | Applied by AGP 9's built-in Kotlin support |
| UI | Jetpack Compose + Material 3 | Declarative UI, theme-aware light/dark |
| Architecture | MVVM + repositories | Testable domain logic, UI free of business rules |
| DI | Hilt 2.60.1 | Compile-time validated graph |
| Auth | Firebase Auth 34.17.0 | Email/password, reset, Google |
| Google sign-in | Credential Manager 1.6.0 | Current API; `GoogleSignInClient` is deprecated |
| Local storage | Room 2.8.4 | The assignment's "CoreData or alternative" |
| Preferences | DataStore 1.2.1 | Active profile, unit preference |
| Charts | Vico 3.3.0 | Compose-native, customizable |
| Build | AGP 9.3.1 / Gradle 9.7.0 | |

Versions are centralised in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

---

## Requirements

- **JDK 17+** — Android Studio's bundled JBR works and is what this project was built with
- **Android SDK Platform 37** (`compileSdk`/`targetSdk` 37), build-tools 37.0.0
- **minSdk 24** (Android 7.0)

---

## Firebase setup

`app/google-services.json` is **not committed** — this is a public repository, and
that file identifies a specific Firebase project. It ships in the submission
archive instead.

If you have the submission archive, drop the file at `app/google-services.json`
and skip to *Build and run*.

To use your own Firebase project instead:

1. Create a project at [console.firebase.google.com](https://console.firebase.google.com).
2. **Authentication → Sign-in method → enable Email/Password.**
3. **Enable Google** on the same screen and set a project support email.
4. **Project settings → Your apps → Android.** Package name: `com.asitkg.bmitracker`
5. Add your debug SHA-1 (see below).
6. Download `google-services.json` into `app/`.

> **Order matters.** Enable the Google provider *before* downloading. The file
> only contains a Web Client ID (`client_type: 3`) if Google is already enabled,
> and without it the build succeeds but Google sign-in fails at runtime.

### Getting your debug SHA-1

```bash
keytool -list -v \
  -keystore ~/.android/debug.keystore \
  -alias androiddebugkey -storepass android -keypass android
```

On Windows the keystore lives at `%USERPROFILE%\.android\debug.keystore`.

Google sign-in will not work until this fingerprint is registered in Firebase.
Email/password sign-in works without it.

---

## Build and run

```bash
# Debug APK -> app/build/outputs/apk/debug/
./gradlew :app:assembleDebug

# Install onto a connected device or running emulator
./gradlew :app:installDebug

# Unit tests
./gradlew :app:testDebugUnitTest
```

Or open the project root in Android Studio and press **Run**.

### If Gradle can't find your SDK

Create `local.properties` in the project root:

```properties
sdk.dir=C:/Users/<you>/AppData/Local/Android/Sdk
```

Use **forward slashes**. In `.properties` files a single backslash is an escape
character, so `C:\Users\...` is silently parsed as `C:Users...` and the build
fails with a confusing `Invalid file path`.

---

## Project structure

```
app/src/main/java/com/asitkg/bmitracker/
├── BmiApplication.kt        # Hilt entry point
├── MainActivity.kt          # Single activity, Compose host
├── domain/                  # Pure Kotlin — no Android imports
│   ├── BmiCalculator.kt     # BMI maths + healthy weight range
│   ├── model/               # Profile, WeightEntry, units, categories
│   └── repository/          # Interfaces the UI depends on
├── data/
│   ├── local/               # Room database, entities, DAOs
│   ├── mapper/              # Entity <-> domain translation
│   ├── preferences/         # DataStore (active profile)
│   └── repository/          # Room-backed implementations
├── di/                      # Hilt modules
└── ui/
    ├── navigation/          # Routes + NavHost
    └── theme/               # Material 3 colours, typography
```

### Two decisions worth knowing

**Storage is always canonical.** Weight is persisted in kilograms and height in
centimetres; KG/LBS and CM/FT-IN are display preferences converted at the UI
edge. A unit toggle therefore cannot corrupt stored data — the round-trip is
covered by tests.

**There is no weight column on a profile.** Current weight is the most recent
`WeightEntry`, so updating weight and appending to the history graph are the
same write. The dashboard reading and the chart can never disagree.

---

## Testing

```bash
./gradlew :app:testDebugUnitTest
```

44 tests cover BMI arithmetic, WHO category boundaries, unit conversions, and
form validation — including that category bands are contiguous (no BMI can fall
between two categories), that unit round-trips are lossless, and that the same
plausibility limits apply whether input arrives in metric or imperial.

## Input validation

All validation lives in `domain/validation/ProfileValidator.kt`, separate from
the UI so it is testable without an emulator. Rules:

| Field | Rule |
|---|---|
| Name | Required, trimmed, max 40 characters |
| Weight | Numeric, > 0, 2–650 kg after conversion |
| Height | Numeric, 50–300 cm after conversion |
| Inches | Must be < 12 — the remainder belongs in feet |
| Date of birth | Optional; not in the future, within 120 years |

Two details worth noting: values are checked **after** conversion to canonical
units, so an out-of-range weight cannot slip through by being entered in pounds;
and `,` is accepted as a decimal separator, since some keyboard locales give the
user no period key.

---

## Licence

Written as an assignment submission; not licensed for redistribution.
