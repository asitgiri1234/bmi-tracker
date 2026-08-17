package com.asitkg.bmitracker.ui.settings

import com.asitkg.bmitracker.domain.model.HeightUnit
import com.asitkg.bmitracker.domain.model.WeightUnit

data class SettingsUiState(
    val isLoading: Boolean = true,
    val profileName: String = "",

    val weightUnit: WeightUnit = WeightUnit.KG,
    val weight: String = "",

    val heightUnit: HeightUnit = HeightUnit.CM,
    val heightCm: String = "",
    val heightFeet: String = "",
    val heightInches: String = "",

    val showErrors: Boolean = false,
    val weightError: String? = null,
    val heightError: String? = null,

    val isSaving: Boolean = false,
    val saveError: String? = null,
    /** Set briefly after a successful save so the UI can confirm it. */
    val savedAt: Long? = null,

    val signedOut: Boolean = false,
    val noProfile: Boolean = false,
) {
    val isValid: Boolean get() = weightError == null && heightError == null
    val visibleWeightError: String? get() = weightError.takeIf { showErrors }
    val visibleHeightError: String? get() = heightError.takeIf { showErrors }
}
