package com.asitkg.bmitracker.ui.dashboard

import com.asitkg.bmitracker.domain.model.BmiCategory
import com.asitkg.bmitracker.domain.model.WeightUnit

sealed interface DashboardUiState {

    data object Loading : DashboardUiState

    /** No profile selected yet — the user is sent to onboarding. */
    data object NoProfile : DashboardUiState

    data class Ready(
        val profileName: String,
        val ageYears: Int?,
        val heightDisplay: String,
        val weightUnit: WeightUnit,
        /** Null until a weight has been recorded for this profile. */
        val weightDisplay: String?,
        val bmi: Double?,
        val category: BmiCategory?,
        val healthyRangeDisplay: String?,
        /** Signed difference from the nearest healthy bound, already formatted. */
        val advice: String?,
    ) : DashboardUiState
}
