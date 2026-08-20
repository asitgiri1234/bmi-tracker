# Demo video — shot list

Target **2:30**. Seven requirements in roughly 20 seconds each, ordered so each
scene sets up the next. Record portrait on the phone; narrate if you can.

---

## Before you press record

- [ ] **Do Not Disturb on** — a notification banner mid-demo looks careless
- [ ] **Screen recorder set to 1080p** — Settings → Advanced features →
      Screenshots and screen recorder → Screen recorder settings
- [ ] **Sound: "Media sounds and mic"** if narrating
- [ ] **Add 3–4 weight entries** through Settings first, so the chart shows a
      real line rather than two points. Vary them (75 → 74.2 → 74.6 → 73.5) so
      the line has shape.
- [ ] **Add a second profile** with contrasting numbers — e.g. 55 kg at 5'2",
      which lands in a different BMI category so the colour visibly changes
- [ ] **Sign out** and leave the app on the login screen, ready to start
- [ ] Do one throwaway practice run. 2:30 is tighter than it sounds.

---

## Scene 1 — Login (0:00–0:25)

**Show:** the login screen, then tap **Sign in with Google** and complete it.

> "This is BMI Tracker, an Android app built with Kotlin and Jetpack Compose.
> Authentication is Firebase — you can sign in with Google, or with email and
> password."

**Do not skip the Google sheet appearing.** It is direct evidence requirement 1
works, and it is the hardest part to fake.

---

## Scene 2 — Account management (0:25–0:45)

**Show:** sign out → **Sign up** screen. Type a short password like `abc` so the
validation message appears. Then back → **Forgot password?** → enter an email →
send.

> "Registration validates locally before hitting the network — passwords need
> eight characters with a letter and a number. There's also password reset,
> which emails a link."

Sign back in with Google to continue.

---

## Scene 3 — Details form and validation (0:45–1:15)

**Show:** add a profile so the details form appears. Deliberately enter bad
input first:
- letters in the weight field
- `15` in the inches field

Then correct them, pick a date of birth, and continue.

> "The details form takes weight in kilograms or pounds, height in centimetres
> or feet and inches, gender, and date of birth. Validation runs on the
> converted value, so an out-of-range weight can't slip through by being typed
> in pounds. Inches over twelve are rejected, and future dates of birth aren't
> selectable at all."

**This scene wins marks** — input validation is a listed criterion and most
submissions do it thinly.

---

## Scene 4 — BMI and the gauge (1:15–1:40)

**Show:** the dashboard. Pause on the BMI number and gauge.

> "BMI is calculated from the stored values and shown with its WHO category. The
> gauge shows where the reading sits between the bands — so a value near the top
> of normal reads differently from one in the middle. It also shows the healthy
> weight range for your height, and how far you are from it."

Let the gauge sit on screen for a beat. It is hand-drawn and worth looking at.

---

## Scene 5 — Settings updating everything live (1:40–2:00)

**Show:** Settings → change the weight → Save → back to dashboard.

> "Height and weight can be changed at any time. Saving a new weight updates the
> BMI and adds a point to the history chart in one step — the dashboard reads
> from the database, so nothing needs refreshing."

**Make sure the BMI number visibly changes.** That is requirement 5 proven in
one shot.

---

## Scene 6 — Weight history chart (2:00–2:15)

**Show:** scroll to the chart.

> "Weight history over the last seven days, drawn on a Compose canvas rather
> than a charting library — so the styling matches the rest of the app."

If your line has 4–5 points this reads well. With two it looks thin, which is
why the prep step matters.

---

## Scene 7 — Multi-user (2:15–2:35)

**Show:** profile switcher → tap the second profile → dashboard changes → switch
back.

> "Multiple profiles live under one account. Each has its own details, BMI and
> history, and switching is instant."

**Pick contrasting profiles** so the BMI number *and* the category colour both
change. Two similar profiles make this look like nothing happened.

---

## Closing (2:35–2:45)

> "All seven requirements are implemented, with sixty-nine unit tests covering
> the BMI maths, validation and chart logic. Setup and build instructions are in
> the README."

---

## If you run long

Cut in this order — these are the least-graded:

1. Password reset (scene 2, second half)
2. The closing line
3. Switching *back* in scene 7

Never cut: validation (scene 3), the gauge (scene 4), or multi-user (scene 7).
Those map directly to the evaluation criteria.
