package com.asitkg.bmitracker.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asitkg.bmitracker.data.preferences.UserPreferencesRepository
import com.asitkg.bmitracker.domain.repository.AuthRepository
import com.asitkg.bmitracker.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Where the app should open, decided once at startup. */
sealed interface StartDestination {

    data object Undecided : StartDestination

    data object Login : StartDestination

    data object Onboarding : StartDestination

    data object Dashboard : StartDestination
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val userPreferences: UserPreferencesRepository,
) : ViewModel() {

    private val _destination = MutableStateFlow<StartDestination>(StartDestination.Undecided)
    val destination: StateFlow<StartDestination> = _destination.asStateFlow()

    init {
        decide()
    }

    /**
     * Firebase restores the previous session from disk, so a returning user
     * goes straight to their data without seeing the login screen — the
     * assignment's auth-persistence bonus.
     */
    private fun decide() {
        viewModelScope.launch {
            val uid = authRepository.currentUid
            if (uid == null) {
                _destination.value = StartDestination.Login
                return@launch
            }

            // A signed-in account with no profile yet still needs onboarding.
            if (profileRepository.countForOwner(uid) == 0) {
                _destination.value = StartDestination.Onboarding
                return@launch
            }

            // Reselect a profile if the stored selection is missing or stale,
            // otherwise the dashboard would open with nothing active.
            val activeId = userPreferences.activeProfileId.first()
            val active = activeId?.let { profileRepository.getProfile(it) }
            if (active == null || active.ownerUid != uid) {
                profileRepository.firstForOwner(uid)?.let { fallback ->
                    userPreferences.setActiveProfileId(fallback.id)
                }
            }

            _destination.value = StartDestination.Dashboard
        }
    }
}
