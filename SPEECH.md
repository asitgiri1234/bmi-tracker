# Demo video speech

Read this aloud while you tap through the app. Written to be spoken, not read —
short sentences, plain words, no jargon without an explanation.

Roughly **2 minutes 30 seconds** at a calm pace. Don't rush; pauses are fine.

---

## Opening (about 15 seconds)

> Hi, I'm Asit. This is BMI Tracker, an Android app I built for the IV
> Innovations assignment.
>
> It lets you work out your BMI, keep an eye on your weight over time, and
> manage more than one person's data from a single account.
>
> I built it with Kotlin and Jetpack Compose, which is the modern way to build
> Android screens. Let me walk you through it.

---

## 1. Signing in (about 20 seconds)

*Tap "Sign in with Google", complete the sheet.*

> First, the login screen. You can sign in with Google, or with an email address
> and password.
>
> The sign-in is handled by Firebase, which is Google's backend service. That
> means I'm not storing anyone's password myself. Firebase does that properly,
> and it's far safer than rolling my own.
>
> One nice detail: once you're signed in, you stay signed in. Close the app,
> reopen it, and it goes straight to your data. You don't log in again every
> time.

---

## 2. Creating an account and resetting a password (about 25 seconds)

*Sign out. Show the sign-up screen. Type a weak password so the error appears.
Then show "Forgot password?".*

> If you don't have an account, you can create one here.
>
> Watch what happens when I type a weak password. The app tells me straight away
> that it needs at least eight characters with a letter and a number. It checks
> this on the phone before sending anything to the server, so you get an instant
> answer instead of waiting.
>
> And if you forget your password, this screen emails you a reset link.
>
> One thing I did deliberately: if you type an email that has no account, it
> still says the link was sent. That sounds unhelpful, but it's on purpose. If
> the app said "no account found", anyone could use it to discover which email
> addresses are registered. So it stays quiet either way.

---

## 3. Entering your details (about 30 seconds)

*Add a profile. Type letters in the weight box. Then type 15 in the inches box.
Then correct both and pick a date of birth.*

> Now the details form. It asks for weight, height, gender and date of birth.
>
> The important part here is checking what people type. Let me show you.
>
> If I type letters where the weight should go, it stops me. If I say fifteen
> inches, it stops me too, because anything over twelve inches is really another
> foot, so that's almost certainly a mistake.
>
> You can enter weight in kilograms or pounds, and height in centimetres or feet
> and inches. Behind the scenes the app always stores kilograms and centimetres,
> and just converts for display. That matters, because it means switching
> between units can never quietly corrupt your saved numbers.
>
> It also means the limits apply fairly. A weight that's too high in kilograms
> is still too high if you type it in pounds, because the app checks after
> converting, not before.
>
> And for date of birth, future dates simply can't be picked. Rather than
> letting you choose tomorrow and then complaining, the calendar greys them out.

---

## 4. Your BMI (about 25 seconds)

*Land on the dashboard. Pause on the number and the coloured bar.*

> Here's the result. My BMI is 22.6, which is in the normal range.
>
> BMI is just your weight divided by your height squared. But a bare number
> doesn't tell you much on its own, so I added this bar underneath.
>
> The four colours are the standard World Health Organisation categories:
> underweight, normal, overweight and obese. The little marker shows exactly
> where you sit.
>
> That's more useful than the number alone. Someone at 24.9 is technically
> normal, but they're right at the edge, and you can see that at a glance here,
> which you couldn't from the number by itself.
>
> Below it, the app shows the healthy weight range for your height, so you know
> what you're actually aiming for.

---

## 5. Updating your weight (about 25 seconds)

*Open Settings, change the weight, save, go back. Make sure the BMI visibly
changes.*

> You can change your height and weight whenever you like, in Settings.
>
> I'll drop my weight slightly and save it. Going back, the BMI has already
> updated, and the marker on the bar has moved.
>
> Nothing had to be refreshed. The dashboard reads straight from the database
> and updates itself whenever the data changes.
>
> And saving a weight does two jobs at once. It becomes your current weight, and
> it adds a point to your history chart. They're the same piece of information,
> so I store it once. That way the number and the chart can never disagree with
> each other.

---

## 6. Weight history (about 20 seconds)

*Scroll to the chart.*

> This is the weight history for the past seven days.
>
> I drew this chart myself, rather than using a chart library. The app only
> needed one simple graph, and drawing it by hand meant I could match it to the
> app's own colours and keep everything looking like one piece.
>
> A couple of small things I had to think about. If there's only one
> measurement, there's no line to draw, so instead of showing a broken-looking
> chart, it just tells you the number and asks for another. And days you didn't
> weigh yourself are left out completely, rather than being drawn as zero, which
> would look like you'd suddenly vanished.

---

## 7. Multiple people (about 25 seconds)

*Open the profile switcher, tap the second profile, let the dashboard change,
switch back.*

> Last part. More than one person can use the same account, which is handy for a
> family.
>
> Here's the list. Each one shows their own BMI and category, so you can see
> everyone at once.
>
> I'll switch to the second profile, and everything changes: the BMI, the
> category colour, and the history chart. Each person's data is completely
> separate.
>
> You can add, edit and delete profiles here too. If you delete the one you're
> currently using, the app quietly moves you to another one rather than leaving
> you staring at an empty screen. And it won't let you delete your last profile,
> because then there'd be nothing to show at all.

---

## Closing (about 15 seconds)

> That's all seven features from the brief, including the three optional ones.
>
> Underneath, I've kept the calculations and the checking rules separate from
> the screens. That let me write sixty-nine automated tests for them, covering
> the BMI maths, the unit conversions, the validation rules and the chart logic,
> and they all run in seconds without needing a phone.
>
> Setup and build instructions are in the README. Thanks for watching.

---

## Speaking notes

- **Slow down on scene 3 and scene 4.** Validation and BMI display are the two
  most heavily graded parts.
- **Let the screen catch up.** Tap, wait a beat, then speak. Talking over a
  transition makes both harder to follow.
- **If you fluff a line, keep going.** A small stumble sounds human. Restarting
  five times costs you far more.
- **If you're running out of time**, cut the password-reset explanation in scene
  2 and the closing paragraph. Never cut validation, the BMI bar, or profiles.
