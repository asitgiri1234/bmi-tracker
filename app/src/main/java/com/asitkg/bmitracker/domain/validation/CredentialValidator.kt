package com.asitkg.bmitracker.domain.validation

/**
 * Validation for the auth forms.
 *
 * Rules are enforced locally before a network call so the user gets an instant,
 * specific message instead of waiting for Firebase to reject the request with a
 * generic one.
 */
object CredentialValidator {

    const val MIN_PASSWORD_LENGTH = 8

    // Deliberately permissive: the only authoritative test of an address is
    // sending mail to it, so this catches typos without rejecting valid but
    // unusual addresses.
    private val EMAIL_PATTERN = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$")

    fun validateEmail(raw: String): ValidationResult<String> {
        val email = raw.trim()
        return when {
            email.isEmpty() -> ValidationResult.Invalid("Email is required")
            !EMAIL_PATTERN.matches(email) -> ValidationResult.Invalid("Enter a valid email address")
            else -> ValidationResult.Valid(email)
        }
    }

    /** Used on sign-in, where any non-empty password is worth attempting. */
    fun validatePasswordPresent(raw: String): ValidationResult<String> =
        if (raw.isEmpty()) ValidationResult.Invalid("Password is required") else ValidationResult.Valid(raw)

    /**
     * Used on registration. Firebase itself only requires six characters; the
     * extra rules here are a deliberate strengthening.
     */
    fun validateNewPassword(raw: String): ValidationResult<String> = when {
        raw.isEmpty() -> ValidationResult.Invalid("Password is required")
        raw.length < MIN_PASSWORD_LENGTH ->
            ValidationResult.Invalid("Use at least $MIN_PASSWORD_LENGTH characters")

        raw.none(Char::isLetter) -> ValidationResult.Invalid("Include at least one letter")
        raw.none(Char::isDigit) -> ValidationResult.Invalid("Include at least one number")
        else -> ValidationResult.Valid(raw)
    }

    fun validatePasswordConfirmation(password: String, confirmation: String): ValidationResult<String> =
        when {
            confirmation.isEmpty() -> ValidationResult.Invalid("Confirm your password")
            confirmation != password -> ValidationResult.Invalid("Passwords do not match")
            else -> ValidationResult.Valid(confirmation)
        }
}
