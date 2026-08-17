package com.asitkg.bmitracker.ui.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asitkg.bmitracker.data.preferences.UserPreferencesRepository
import com.asitkg.bmitracker.domain.BmiCalculator
import com.asitkg.bmitracker.domain.model.BmiCategory
import com.asitkg.bmitracker.domain.model.Profile
import com.asitkg.bmitracker.domain.repository.AuthRepository
import com.asitkg.bmitracker.domain.repository.ProfileRepository
import com.asitkg.bmitracker.domain.repository.WeightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** A profile plus the summary shown on its row. */
data class ProfileRow(
    val id: Long,
    val name: String,
    val isActive: Boolean,
    val bmi: Double?,
    val category: BmiCategory?,
)

data class ProfilesUiState(
    val isLoading: Boolean = true,
    val profiles: List<ProfileRow> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfilesViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val weightRepository: WeightRepository,
    private val userPreferences: UserPreferencesRepository,
    authRepository: AuthRepository,
) : ViewModel() {

    private val ownerUid = authRepository.currentUid

    val uiState: StateFlow<ProfilesUiState> =
        if (ownerUid == null) {
            flowOf(ProfilesUiState(isLoading = false))
        } else {
            combine(
                profileRepository.observeProfiles(ownerUid),
                userPreferences.activeProfileId,
            ) { profiles, activeId -> profiles to activeId }
                .map { (profiles, activeId) ->
                    ProfilesUiState(
                        isLoading = false,
                        profiles = profiles.map { profile ->
                            buildRow(profile, isActive = profile.id == activeId)
                        },
                    )
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfilesUiState(),
        )

    private suspend fun buildRow(profile: Profile, isActive: Boolean): ProfileRow {
        val latestKg = weightRepository.getLatest(profile.id)?.weightKg
        val bmi = latestKg?.let { BmiCalculator.calculate(it, profile.heightCm) }
        return ProfileRow(
            id = profile.id,
            name = profile.name,
            isActive = isActive,
            bmi = bmi?.let(BmiCalculator::round),
            category = bmi?.let(BmiCategory::forBmi),
        )
    }

    fun onSelect(profileId: Long) {
        viewModelScope.launch { userPreferences.setActiveProfileId(profileId) }
    }

    /**
     * Deleting cascades to that profile's weight entries.
     *
     * If the deleted profile was active, another is selected so the dashboard
     * is never left pointing at a row that no longer exists.
     */
    fun onDelete(profileId: Long) {
        val uid = ownerUid ?: return
        viewModelScope.launch {
            val profile = profileRepository.getProfile(profileId) ?: return@launch
            val wasActive = userPreferences.activeProfileId.first() == profileId

            profileRepository.deleteProfile(profile)

            if (wasActive) {
                val replacement = profileRepository.firstForOwner(uid)
                if (replacement == null) {
                    userPreferences.clearActiveProfile()
                } else {
                    userPreferences.setActiveProfileId(replacement.id)
                }
            }
        }
    }
}
