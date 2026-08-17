# BMI Tracker

An Android app for tracking BMI across multiple user profiles, with Firebase
authentication and a weekly weight-history chart.

Built for the IV Innovations application assignment.

> Assembling or reviewing the submission? See **[SUBMISSION.md](SUBMISSION.md)**
> for package contents and how to run the prebuilt APK.

---

## Features

All seven requirements, including the three the brief marks as optional.

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

- Authentication state persists across restarts
- Custom-drawn charts — the BMI gauge and weight chart are hand-drawn on a
  Compose `Canvas`, not taken from a charting library
- Error handling — Firebase codes mapped to readable messages, distinct empty
  states, offline handling

Verified on a physical **Samsung Galaxy M35 (Android 16)** as well as in unit tests.

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
| Charts | Compose `Canvas` (no library) | See [Charts](#charts) |
| Build | AGP 9.3.1 / Gradle 9.7.0 | |

Versions are centralised in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

**Kotlin is pinned to 2.3.20, not the newer 2.4.x**, because KSP — which Room and
Hilt both require — has not yet shipped for 2.4.

---

## Requirements

- **JDK 17+** — Android Studio's bundled JBR works and is what this was built with
- **Android SDK Platform 37** (`compileSdk`/`targetSdk` 37), build-tools 37.0.0
- **minSdk 24** (Android 7.0)

---

## Build and run

```bash
# Debug APK -> app/build/outputs/apk/debug/
./gradlew :app:assembleDebug

# Install onto a connected device or emulator
./gradlew :app:installDebug

# Unit tests
./gradlew :app:testDebugUnitTest
```

Or open the project root in Android Studio and press **Run**.

`app/google-services.json` must be present first — see [Firebase setup](#firebase-setup).

### If Gradle can't find your SDK

Create `local.properties` in the project root:

```properties
sdk.dir=C:/Users/<you>/AppData/Local/Android/Sdk
```

Use **forward slashes**. In `.properties` files a single backslash is an escape
character, so `C:\Users\...` silently parses as `C:Users...` and the build fails
with a confusing `Invalid file path`.

---

## Firebase setup

`app/google-services.json` is **not committed** — this is a public repository and
that file identifies a specific Firebase project. It ships in the submission
archive instead.

If you have the submission archive, copy the file to `app/google-services.json`
and you are done.

To use your own Firebase project:

1. Create a project at [console.firebase.google.com](https://console.firebase.google.com)
2. **Authentication → Sign-in method → enable Email/Password**
3. **Enable Google** on the same screen, and set a project support email
4. **Project settings → Your apps → Android.** Package name: `com.asitkg.bmitracker`
5. Add your debug SHA-1 (below)
6. Download `google-services.json` into `app/`

> **Order matters.** Enable the Google provider *before* downloading. The file
> only contains a Web Client ID (`client_type: 3`) if Google is already enabled,
> and without it the build succeeds but Google sign-in fails at runtime with an
> unhelpful error.

### Getting your debug SHA-1

```bash
keytool -list -v \
  -keystore ~/.android/debug.keystore \
  -alias androiddebugkey -storepass android -keypass android
```

On Windows the keystore is at `%USERPROFILE%\.android\debug.keystore`.

Google sign-in will not work until this fingerprint is registered in Firebase.
Email/password sign-in works without it.

---

## Project structure

```
app/src/main/java/com/asitkg/bmitracker/
├── BmiApplication.kt        # Hilt entry point
├── MainActivity.kt          # Single activity, Compose host
├── domain/                  # Pure Kotlin — no Android imports
│   ├── BmiCalculator.kt     # BMI maths + healthy weight range
│   ├── WeightHistory.kt     # Chart series windowing
│   ├── model/               # Profile, WeightEntry, units, categories
│   ├── repository/          # Interfaces the UI depends on
│   └── validation/          # Form and credential rules
├── data/
│   ├── auth/                # Firebase Auth implementation
│   ├── local/               # Room database, entities, DAOs
│   ├── mapper/              # Entity <-> domain translation
│   ├── preferences/         # DataStore (active profile)
│   └── repository/          # Room-backed implementations
├── di/                      # Hilt modules
└── ui/
    ├── auth/                # Login, sign-up, password reset
    ├── components/          # Shared form controls
    ├── dashboard/           # BMI display, gauge, weight chart
    ├── navigation/          # Routes + NavHost
    ├── onboarding/          # User details form
    ├── profiles/            # Multi-user list, switching, editing
    ├── settings/            # Update height, weight, units
    ├── splash/              # Auth-state routing
    └── theme/               # Material 3 colours, typography
```

The `domain` package has no Android imports, so the BMI maths, validation, and
chart windowing all run as plain JVM unit tests without an emulator.

### Two decisions worth knowing

**Storage is always canonical.** Weight is persisted in kilograms and height in
centimetres; KG/LBS and CM/FT-IN are display preferences converted at the UI
edge. A unit toggle therefore cannot corrupt stored data — the round-trip is
covered by tests.

**There is no weight column on a profile.** Current weight is the most recent
`WeightEntry`, so updating weight and appending to the history graph are the same
write. The dashboard reading and the chart can never disagree.

---

## Authentication

Firebase Auth with three entry points: email/password sign-in, registration, and
password reset. Google sign-in uses **Credential Manager**, the current API — the
older `GoogleSignInClient` is deprecated.

Session state persists across restarts. Firebase stores the session itself, so
the splash screen reads it on a cold start and routes a returning user straight
to their dashboard.

Firebase error codes are mapped to readable messages in `FirebaseAuthRepository`.
One deliberate detail: with email enumeration protection enabled — Firebase's
current default — a wrong password and an unknown account both return
`ERROR_INVALID_CREDENTIAL`, so both show the same message. Distinguishing them
would tell an attacker which addresses have accounts. Password reset reports
success for unregistered addresses for the same reason.

Registration requires 8+ characters including a letter and a number. Sign-in only
requires a non-empty password, because existing accounts may predate that rule
and should not be locked out by it.

---

## Multi-user

One signed-in account owns any number of profiles — family members, for instance.
Profiles are scoped by `ownerUid`, switched from the app bar, and each carries its
own details, BMI, and weight history.

Three edge cases are handled explicitly, since each would otherwise leave the app
pointing at nothing:

- **Deleting the active profile** selects another automatically
- **Deleting the last profile** is prevented — the action is withheld when only
  one remains
- **A stale stored selection** (deleted, or belonging to another account) is
  re-resolved at startup by the splash screen

Deleting a profile cascades to its weight entries at the database level, so no
orphaned measurements survive.

---

## Charts

Both the BMI gauge and the weight-history chart are drawn directly on a Compose
`Canvas` rather than through a charting library. The shapes needed are simple,
owning the drawing keeps both consistent with the app's theme, and it avoids a
dependency used on one screen.

The **BMI gauge** shows the four WHO bands at proportional width with a marker at
the user's value. A scale communicates more than a bare number: it shows how far
a reading sits from neighbouring bands, so 24.9 reads as "near the top of normal"
rather than simply "normal".

The **weight chart** plots the last seven days with a filled area, data points,
dashed gridlines, and weekday labels.

Series construction lives in `domain/WeightHistory.kt`, separate from the
drawing, so the awkward cases are unit-tested rather than eyeballed:

- **Every measurement in the window is plotted.** An earlier version collapsed
  same-day readings to one point, which meant a line could never appear until the
  app had been used across two calendar days — it looked broken in a single
  sitting. Repeated weekday labels are blanked instead, so several readings in
  one day do not print "Mon Mon Mon" along the axis.
- **Days with no measurement** are omitted, not zero-filled; a zero would plot as
  a spike to the chart floor.
- **Entries outside the 7-day window**, including future-dated ones, are excluded.

The chart also handles the two states that break naive implementations: an empty
series, and a single point — which has no line to draw and no range, so the value
is shown as text rather than as a degenerate chart.

---

## Input validation

Validation lives in `domain/validation/`, separate from the UI so it is testable
without an emulator.

| Field | Rule |
|---|---|
| Name | Required, trimmed, max 40 characters |
| Weight | Numeric, > 0, 2–650 kg after conversion |
| Height | Numeric, 50–300 cm after conversion |
| Inches | Must be < 12 — the remainder belongs in feet |
| Date of birth | Optional; not in the future, within 120 years |
| Email | Required, must contain a user, `@`, and a dotted domain |
| Password (new) | 8+ characters, at least one letter and one number |

Three details worth noting:

- Values are checked **after** conversion to canonical units, so an out-of-range
  weight cannot slip through by being entered in pounds
- `,` is accepted as a decimal separator, since some keyboard locales give the
  user no period key
- Errors stay hidden until the first submit, so fields are not flagged red before
  the user has typed in them

---

## Testing

```bash
./gradlew :app:testDebugUnitTest
```

**69 unit tests**, covering:

| Suite | What it protects |
|---|---|
| `BmiCalculatorTest` | BMI maths; implausible input returns null rather than NaN or Infinity |
| `BmiCategoryTest` | WHO bands are contiguous — no BMI can fall between two categories |
| `UnitConverterTest` | Metric ↔ imperial round-trips are lossless |
| `ProfileValidatorTest` | Same limits apply whether input is metric or imperial |
| `CredentialValidatorTest` | Email and password rules |
| `WeightHistoryTest` | Chart windowing, ordering, and same-day entries |

---

## Licence

Written as an assignment submission; not licensed for redistribution.
