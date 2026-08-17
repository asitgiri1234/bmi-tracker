package com.asitkg.bmitracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asitkg.bmitracker.data.preferences.UserPreferencesRepository
import com.asitkg.bmitracker.domain.model.HeightUnit
import com.asitkg.bmitracker.domain.model.Profile
import com.asitkg.bmitracker.domain.model.UnitConverter
import com.asitkg.bmitracker.domain.model.WeightUnit
import com.asitkg.bmitracker.domain.repository.AuthRepository
import com.asitkg.bmitracker.domain.repository.ProfileRepository
import com.asitkg.bmitracker.domain.repository.WeightRepository
import com.asitkg.bmitracker.domain.validation.ProfileValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val weightRepository: WeightRepository,
    private val userPreferences: UserPreferencesRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var loadedProfile: Profile? = null

    init {
        load()
    }

    /**
     * Fields are pre-filled from the stored profile so the screen shows what is
     * currently in effect rather than empty inputs.
     */
    private fun load() {
        viewModelScope.launch {
            val profileId = userPreferences.activeProfileId.first()
            val profile = profileId?.let { profileRepository.getProfile(it) }
            if (profile == null) {
                _uiState.update { it.copy(isLoading = false, noProfile = true) }
                return@launch
            }
            loadedProfile = profile

            val latestWeightKg = weightRepository.getLatest(profile.id)?.weightKg
            val heightSplit = UnitConverter.cmToFeetAndInches(profile.heightCm)

            _uiState.value = validate(
                SettingsUiState(
                    isLoading = false,
                    profileName = profile.name,
                    weightUnit = profile.weightUnit,
                    weight = latestWeightKg
                        ?.let { format(UnitConverter.kgToDisplay(it, profile.weightUnit)) }
                        .orEmpty(),
                    heightUnit = profile.heightUnit,
                    heightCm = format(profile.heightCm),
                    heightFeet = heightSplit.feet.toString(),
                    heightInches = format(heightSplit.inches),
                ),
            )
        }
    }

    fun onWeightChange(value: String) = update { it.copy(weight = value) }

    fun onHeightCmChange(value: String) = update { it.copy(heightCm = value) }

    fun onHeightFeetChange(value: String) = update { it.copy(heightFeet = value) }

    fun onHeightInchesChange(value: String) = update { it.copy(heightInches = value) }

    /** Converts the entered value so switching units never loses or alters it. */
    fun onWeightUnitChange(unit: WeightUnit) = update { state ->
        if (unit == state.weightUnit) return@update state
        val converted = state.weight.trim().replace(',', '.').toDoubleOrNull()?.let { entered ->
            format(UnitConverter.kgToDisplay(UnitConverter.displayToKg(entered, state.weightUnit), unit))
        }
        state.copy(weightUnit = unit, weight = converted ?: state.weight)
    }

    fun onHeightUnitChange(unit: HeightUnit) = update { state ->
        if (unit == state.heightUnit) return@update state
        when (unit) {
            HeightUnit.FEET_INCHES -> {
                val cm = state.heightCm.trim().replace(',', '.').toDoubleOrNull()
                    ?: return@update state.copy(heightUnit = unit)
                val split = UnitConverter.cmToFeetAndInches(cm)
                state.copy(
                    heightUnit = unit,
                    heightFeet = split.feet.toString(),
                    heightInches = format(split.inches),
                )
            }

            HeightUnit.CM -> {
                val feet = state.heightFeet.trim().toIntOrNull()
                    ?: return@update state.copy(heightUnit = unit)
                val inches = state.heightInches.trim().replace(',', '.').toDoubleOrNull() ?: 0.0
                state.copy(
                    heightUnit = unit,
                    heightCm = format(UnitConverter.feetAndInchesToCm(feet, inches)),
                )
            }
        }
    }

    fun onSave() {
        val state = _uiState.value
        val profile = loadedProfile ?: return
        if (!state.isValid) {
            _uiState.update { it.copy(showErrors = true) }
            return
        }

        val heightCm = ProfileValidator
            .validateHeight(state.heightUnit, state.heightCm, state.heightFeet, state.heightInches)
            .valueOrNull ?: return
        val weightKg = ProfileValidator
            .validateWeight(state.weight, state.weightUnit).valueOrNull ?: return

        _uiState.update { it.copy(isSaving = true, saveError = null) }

        viewModelScope.launch {
            runCatching {
                profileRepository.updateProfile(
                    profile.copy(
                        heightCm = heightCm,
                        weightUnit = state.weightUnit,
                        heightUnit = state.heightUnit,
                    ),
                )

                // Only record a measurement when the weight actually changed:
                // saving unrelated settings should not add a duplicate point to
                // the history chart.
                val previousKg = weightRepository.getLatest(profile.id)?.weightKg
                if (previousKg == null || abs(previousKg - weightKg) > 0.001) {
                    weightRepository.recordWeight(profile.id, weightKg)
                }
            }.onSuccess {
                loadedProfile = profile.copy(
                    heightCm = heightCm,
                    weightUnit = state.weightUnit,
                    heightUnit = state.heightUnit,
                )
                _uiState.update {
                    it.copy(isSaving = false, savedAt = System.currentTimeMillis())
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveError = error.message ?: "Could not save your changes",
                    )
                }
            }
        }
    }

    fun onSignOut() {
        viewModelScope.launch {
            // Clear the active profile too, so the next account does not open
            // on someone else's selection.
            userPreferences.clearActiveProfile()
            authRepository.signOut()
            _uiState.update { it.copy(signedOut = true) }
        }
    }

    private fun update(transform: (SettingsUiState) -> SettingsUiState) {
        _uiState.update { current -> validate(transform(current)).copy(savedAt = null) }
    }

    private fun validate(state: SettingsUiState): SettingsUiState = state.copy(
        weightError = ProfileValidator.validateWeight(state.weight, state.weightUnit).errorMessage,
        heightError = ProfileValidator.validateHeight(
            unit = state.heightUnit,
            rawCm = state.heightCm,
            rawFeet = state.heightFeet,
            rawInches = state.heightInches,
        ).errorMessage,
    )

    private fun format(value: Double): String =
        if (abs(value % 1.0) < 0.05) value.toInt().toString() else String.format("%.1f", value)
}
