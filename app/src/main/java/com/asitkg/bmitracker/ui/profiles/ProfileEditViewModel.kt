package com.asitkg.bmitracker.ui.profiles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asitkg.bmitracker.domain.model.Gender
import com.asitkg.bmitracker.domain.model.Profile
import com.asitkg.bmitracker.domain.repository.ProfileRepository
import com.asitkg.bmitracker.domain.validation.ProfileValidator
import com.asitkg.bmitracker.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ProfileEditUiState(
    val isLoading: Boolean = true,
    val name: String = "",
    val gender: Gender = Gender.MALE,
    val dateOfBirth: LocalDate? = null,
    val showErrors: Boolean = false,
    val nameError: String? = null,
    val dateOfBirthError: String? = null,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val saved: Boolean = false,
    val notFound: Boolean = false,
) {
    val isValid: Boolean get() = nameError == null && dateOfBirthError == null
    val visibleNameError: String? get() = nameError.takeIf { showErrors }
    val visibleDateOfBirthError: String? get() = dateOfBirthError.takeIf { showErrors }
}

/**
 * Edits a profile's identity — name, gender, date of birth.
 *
 * Height and weight are deliberately not here: they belong to the settings
 * screen, where changing weight also records a history point.
 */
@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val profileId: Long = savedStateHandle[Routes.PROFILE_ID_ARG] ?: 0L

    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState: StateFlow<ProfileEditUiState> = _uiState.asStateFlow()

    private var loaded: Profile? = null

    init {
        viewModelScope.launch {
            val profile = profileRepository.getProfile(profileId)
            if (profile == null) {
                _uiState.update { it.copy(isLoading = false, notFound = true) }
                return@launch
            }
            loaded = profile
            _uiState.value = validate(
                ProfileEditUiState(
                    isLoading = false,
                    name = profile.name,
                    gender = profile.gender,
                    dateOfBirth = profile.dateOfBirth,
                ),
            )
        }
    }

    fun onNameChange(value: String) = update { it.copy(name = value) }

    fun onGenderChange(value: Gender) = update { it.copy(gender = value) }

    fun onDateOfBirthChange(value: LocalDate?) = update { it.copy(dateOfBirth = value) }

    fun onSave() {
        val state = _uiState.value
        val profile = loaded ?: return
        if (!state.isValid) {
            _uiState.update { it.copy(showErrors = true) }
            return
        }
        val name = ProfileValidator.validateName(state.name).valueOrNull ?: return

        _uiState.update { it.copy(isSaving = true, saveError = null) }
        viewModelScope.launch {
            runCatching {
                profileRepository.updateProfile(
                    profile.copy(
                        name = name,
                        gender = state.gender,
                        dateOfBirth = state.dateOfBirth,
                    ),
                )
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, saved = true) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveError = error.message ?: "Could not save this profile",
                    )
                }
            }
        }
    }

    private fun update(transform: (ProfileEditUiState) -> ProfileEditUiState) {
        _uiState.update { current -> validate(transform(current)) }
    }

    private fun validate(state: ProfileEditUiState): ProfileEditUiState = state.copy(
        nameError = ProfileValidator.validateName(state.name).errorMessage,
        dateOfBirthError = ProfileValidator.validateDateOfBirth(state.dateOfBirth).errorMessage,
    )
}
