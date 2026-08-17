package com.asitkg.bmitracker.domain.validation

/**
 * Outcome of validating one field.
 *
 * Carrying the parsed value in [Valid] means a caller cannot forget to convert
 * the raw string afterwards — the parse and the check happen together.
 */
sealed interface ValidationResult<out T> {

    data class Valid<T>(val value: T) : ValidationResult<T>

    data class Invalid(val message: String) : ValidationResult<Nothing>

    val errorMessage: String?
        get() = (this as? Invalid)?.message

    val valueOrNull: T?
        get() = (this as? Valid)?.value
}
