package com.asitkg.bmitracker.domain

import com.asitkg.bmitracker.domain.validation.CredentialValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CredentialValidatorTest {

    // --- email ---

    @Test
    fun `rejects an empty email`() {
        assertNotNull(CredentialValidator.validateEmail("").errorMessage)
    }

    @Test
    fun `rejects an email without an at sign`() {
        assertNotNull(CredentialValidator.validateEmail("asitkg03gmail.com").errorMessage)
    }

    @Test
    fun `rejects an email without a domain dot`() {
        assertNotNull(CredentialValidator.validateEmail("asit@gmail").errorMessage)
    }

    @Test
    fun `rejects an email containing spaces`() {
        assertNotNull(CredentialValidator.validateEmail("asit kg@gmail.com").errorMessage)
    }

    @Test
    fun `accepts a valid email and trims it`() {
        val result = CredentialValidator.validateEmail("  asitkg03@gmail.com  ")
        assertEquals("asitkg03@gmail.com", result.valueOrNull)
    }

    @Test
    fun `accepts an email with a plus tag`() {
        assertNull(CredentialValidator.validateEmail("asit+bmi@gmail.com").errorMessage)
    }

    // --- sign-in password ---

    @Test
    fun `sign-in rejects only an empty password`() {
        assertNotNull(CredentialValidator.validatePasswordPresent("").errorMessage)
        // Short passwords must still be attempted: the rules may have changed
        // since an existing account was created.
        assertNull(CredentialValidator.validatePasswordPresent("abc").errorMessage)
    }

    // --- new password ---

    @Test
    fun `rejects a short new password`() {
        assertNotNull(CredentialValidator.validateNewPassword("ab1").errorMessage)
    }

    @Test
    fun `rejects a new password without a letter`() {
        assertNotNull(CredentialValidator.validateNewPassword("12345678").errorMessage)
    }

    @Test
    fun `rejects a new password without a number`() {
        assertNotNull(CredentialValidator.validateNewPassword("abcdefgh").errorMessage)
    }

    @Test
    fun `accepts a valid new password`() {
        assertNull(CredentialValidator.validateNewPassword("bmitracker1").errorMessage)
    }

    @Test
    fun `enforces the documented minimum length`() {
        val exactly = "a".repeat(CredentialValidator.MIN_PASSWORD_LENGTH - 1) + "1"
        assertNull(CredentialValidator.validateNewPassword(exactly).errorMessage)

        val oneShort = "a".repeat(CredentialValidator.MIN_PASSWORD_LENGTH - 2) + "1"
        assertNotNull(CredentialValidator.validateNewPassword(oneShort).errorMessage)
    }

    // --- confirmation ---

    @Test
    fun `rejects an empty confirmation`() {
        assertNotNull(CredentialValidator.validatePasswordConfirmation("bmitracker1", "").errorMessage)
    }

    @Test
    fun `rejects a mismatched confirmation`() {
        assertNotNull(
            CredentialValidator.validatePasswordConfirmation("bmitracker1", "bmitracker2").errorMessage,
        )
    }

    @Test
    fun `accepts a matching confirmation`() {
        assertNull(
            CredentialValidator.validatePasswordConfirmation("bmitracker1", "bmitracker1").errorMessage,
        )
    }
}
