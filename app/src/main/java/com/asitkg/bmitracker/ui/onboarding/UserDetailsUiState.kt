package com.asitkg.bmitracker.ui.onboarding

import com.asitkg.bmitracker.domain.model.Gender
import com.asitkg.bmitracker.domain.model.HeightUnit
import com.asitkg.bmitracker.domain.model.WeightUnit
import java.time.LocalDate

/**
 * Form state.
 *
 * Raw text is kept exactly as typed and parsed only at validation time, so the
 * field never fights the user mid-entry (e.g. "1." while typing "1.8").
 *
 * Errors are null until [showErrors] flips on submit — validating on first
 * keystroke would flag every field as invalid before the user has typed.
 */
data class UserDetailsUiState(
    val name: String = "",
    val gender: Gender = Gender.MALE,
    val dateOfBirth: LocalDate? = null,

    val weightUnit: WeightUnit = WeightUnit.KG,
    val weight: String = "",

    val heightUnit: HeightUnit = HeightUnit.CM,
    val heightCm: String = "",
    val heightFeet: String = "",
    val heightInches: String = "",

    val showErrors: Boolean = false,
    val nameError: String? = null,
    val weightError: String? = null,
    val heightError: String? = null,
    val dateOfBirthError: String? = null,

    val isSaving: Boolean = false,
    val saveError: String? = null,
    val savedProfileId: Long? = null,
) {
    val isValid: Boolean
        get() = nameError == null &&
            weightError == null &&
            heightError == null &&
            dateOfBirthError == null

    /** Errors only surface once the user has attempted to submit. */
    val visibleNameError: String? get() = nameError.takeIf { showErrors }
    val visibleWeightError: String? get() = weightError.takeIf { showErrors }
    val visibleHeightError: String? get() = heightError.takeIf { showErrors }
    val visibleDateOfBirthError: String? get() = dateOfBirthError.takeIf { showErrors }
}
