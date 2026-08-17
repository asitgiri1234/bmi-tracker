package com.asitkg.bmitracker.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asitkg.bmitracker.data.preferences.UserPreferencesRepository
import com.asitkg.bmitracker.domain.model.Gender
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val weightRepository: WeightRepository,
    private val userPreferences: UserPreferencesRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserDetailsUiState())
    val uiState: StateFlow<UserDetailsUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) = update { it.copy(name = value) }

    fun onGenderChange(value: Gender) = update { it.copy(gender = value) }

    fun onDateOfBirthChange(value: LocalDate?) = update { it.copy(dateOfBirth = value) }

    fun onWeightChange(value: String) = update { it.copy(weight = value) }

    fun onHeightCmChange(value: String) = update { it.copy(heightCm = value) }

    fun onHeightFeetChange(value: String) = update { it.copy(heightFeet = value) }

    fun onHeightInchesChange(value: String) = update { it.copy(heightInches = value) }

    /**
     * Switching units converts the value already entered rather than clearing
     * it, so toggling KG/LBS mid-form does not lose the user's input.
     */
    fun onWeightUnitChange(unit: WeightUnit) = update { state ->
        if (unit == state.weightUnit) return@update state
        val converted = state.weight.trim().replace(',', '.').toDoubleOrNull()?.let { entered ->
            val kg = UnitConverter.displayToKg(entered, state.weightUnit)
            formatNumber(UnitConverter.kgToDisplay(kg, unit))
        }
        state.copy(weightUnit = unit, weight = converted ?: state.weight)
    }

    fun onHeightUnitChange(unit: HeightUnit) = update { state ->
        if (unit == state.heightUnit) return@update state
        when (unit) {
            HeightUnit.FEET_INCHES -> {
                val cm = state.heightCm.trim().replace(',', '.').toDoubleOrNull()
                if (cm == null) {
                    state.copy(heightUnit = unit)
                } else {
                    val split = UnitConverter.cmToFeetAndInches(cm)
                    state.copy(
                        heightUnit = unit,
                        heightFeet = split.feet.toString(),
                        heightInches = formatNumber(split.inches),
                    )
                }
            }

            HeightUnit.CM -> {
                val feet = state.heightFeet.trim().toIntOrNull()
                val inches = state.heightInches.trim().replace(',', '.').toDoubleOrNull() ?: 0.0
                if (feet == null) {
                    state.copy(heightUnit = unit)
                } else {
                    state.copy(
                        heightUnit = unit,
                        heightCm = formatNumber(UnitConverter.feetAndInchesToCm(feet, inches)),
                    )
                }
            }
        }
    }

    fun onSubmit() {
        val state = _uiState.value
        if (!state.isValid) {
            _uiState.update { it.copy(showErrors = true) }
            return
        }
        save(state)
    }

    private fun save(state: UserDetailsUiState) {
        val weightKg = ProfileValidator
            .validateWeight(state.weight, state.weightUnit).valueOrNull ?: return
        val heightCm = ProfileValidator
            .validateHeight(state.heightUnit, state.heightCm, state.heightFeet, state.heightInches)
            .valueOrNull ?: return
        val name = ProfileValidator.validateName(state.name).valueOrNull ?: return

        val ownerUid = authRepository.currentUid
        if (ownerUid == null) {
            // Reachable only if the session expired while the form was open.
            _uiState.update { it.copy(saveError = "Your session has expired. Please sign in again.") }
            return
        }

        _uiState.update { it.copy(isSaving = true, saveError = null) }

        viewModelScope.launch {
            runCatching {
                val profileId = profileRepository.createProfile(
                    Profile(
                        ownerUid = ownerUid,
                        name = name,
                        gender = state.gender,
                        dateOfBirth = state.dateOfBirth,
                        heightCm = heightCm,
                        weightUnit = state.weightUnit,
                        heightUnit = state.heightUnit,
                    ),
                )
                // The starting weight becomes the first point on the history
                // graph, so a new profile is never charted as empty.
                weightRepository.recordWeight(profileId, weightKg)
                userPreferences.setActiveProfileId(profileId)
                profileId
            }.onSuccess { profileId ->
                _uiState.update { it.copy(isSaving = false, savedProfileId = profileId) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveError = error.message ?: "Could not save your details",
                    )
                }
            }
        }
    }

    /** Re-runs every field validation so [UserDetailsUiState.isValid] stays current. */
    private fun update(transform: (UserDetailsUiState) -> UserDetailsUiState) {
        _uiState.update { current -> validate(transform(current)) }
    }

    private fun validate(state: UserDetailsUiState): UserDetailsUiState = state.copy(
        nameError = ProfileValidator.validateName(state.name).errorMessage,
        weightError = ProfileValidator.validateWeight(state.weight, state.weightUnit).errorMessage,
        heightError = ProfileValidator.validateHeight(
            unit = state.heightUnit,
            rawCm = state.heightCm,
            rawFeet = state.heightFeet,
            rawInches = state.heightInches,
        ).errorMessage,
        dateOfBirthError = ProfileValidator.validateDateOfBirth(state.dateOfBirth).errorMessage,
    )

    private fun formatNumber(value: Double): String =
        if (value % 1.0 == 0.0) value.toInt().toString() else String.format("%.1f", value)
}
