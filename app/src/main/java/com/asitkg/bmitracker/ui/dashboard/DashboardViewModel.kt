package com.asitkg.bmitracker.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asitkg.bmitracker.data.preferences.UserPreferencesRepository
import com.asitkg.bmitracker.domain.BmiCalculator
import com.asitkg.bmitracker.domain.model.BmiCategory
import com.asitkg.bmitracker.domain.model.HeightUnit
import com.asitkg.bmitracker.domain.model.Profile
import com.asitkg.bmitracker.domain.model.UnitConverter
import com.asitkg.bmitracker.domain.model.WeightEntry
import com.asitkg.bmitracker.domain.repository.ProfileRepository
import com.asitkg.bmitracker.domain.repository.WeightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    userPreferences: UserPreferencesRepository,
    profileRepository: ProfileRepository,
    weightRepository: WeightRepository,
) : ViewModel() {

    /**
     * Derived entirely from the database, so recording a new weight in settings
     * updates the BMI here without any explicit refresh.
     */
    val uiState: StateFlow<DashboardUiState> = userPreferences.activeProfileId
        .flatMapLatest { profileId ->
            if (profileId == null) {
                flowOf(DashboardUiState.NoProfile)
            } else {
                combine(
                    profileRepository.observeProfile(profileId),
                    weightRepository.observeLatest(profileId),
                ) { profile, latestWeight ->
                    if (profile == null) {
                        DashboardUiState.NoProfile
                    } else {
                        buildReadyState(profile, latestWeight)
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState.Loading,
        )

    private fun buildReadyState(profile: Profile, latest: WeightEntry?): DashboardUiState.Ready {
        val bmi = latest?.let { BmiCalculator.calculate(it.weightKg, profile.heightCm) }
        val healthyRange = BmiCalculator.healthyWeightRangeKg(profile.heightCm)

        return DashboardUiState.Ready(
            profileName = profile.name,
            ageYears = profile.ageYears(),
            heightDisplay = formatHeight(profile),
            weightUnit = profile.weightUnit,
            weightDisplay = latest?.let {
                "${format(UnitConverter.kgToDisplay(it.weightKg, profile.weightUnit))} " +
                    profile.weightUnit.label
            },
            bmi = bmi?.let(BmiCalculator::round),
            category = bmi?.let(BmiCategory::forBmi),
            healthyRangeDisplay = healthyRange?.let { range ->
                val low = UnitConverter.kgToDisplay(range.start, profile.weightUnit)
                val high = UnitConverter.kgToDisplay(range.endInclusive, profile.weightUnit)
                "${format(low)}–${format(high)} ${profile.weightUnit.label}"
            },
            advice = latest?.let { entry ->
                healthyRange?.let { range -> buildAdvice(entry.weightKg, range, profile) }
            },
        )
    }

    /**
     * Turns the verdict into an actionable number — how far from the healthy
     * band the user currently is, in their own unit.
     */
    private fun buildAdvice(
        weightKg: Double,
        healthyRange: ClosedFloatingPointRange<Double>,
        profile: Profile,
    ): String {
        val unit = profile.weightUnit
        return when {
            weightKg < healthyRange.start -> {
                val delta = healthyRange.start - weightKg
                "${format(UnitConverter.kgToDisplay(delta, unit))} ${unit.label} " +
                    "below the healthy range"
            }

            weightKg > healthyRange.endInclusive -> {
                val delta = weightKg - healthyRange.endInclusive
                "${format(UnitConverter.kgToDisplay(delta, unit))} ${unit.label} " +
                    "above the healthy range"
            }

            else -> "Within the healthy range for your height"
        }
    }

    private fun formatHeight(profile: Profile): String = when (profile.heightUnit) {
        HeightUnit.CM -> "${format(profile.heightCm)} cm"
        HeightUnit.FEET_INCHES -> {
            val split = UnitConverter.cmToFeetAndInches(profile.heightCm)
            "${split.feet}' ${split.inches.roundToInt()}\""
        }
    }

    private fun format(value: Double): String =
        if (abs(value % 1.0) < 0.05) {
            value.roundToInt().toString()
        } else {
            String.format("%.1f", value)
        }
}
