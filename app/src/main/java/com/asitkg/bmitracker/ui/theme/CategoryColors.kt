package com.asitkg.bmitracker.ui.theme

import androidx.compose.ui.graphics.Color
import com.asitkg.bmitracker.domain.model.BmiCategory

/**
 * Colour per BMI band.
 *
 * Defined here rather than on the enum so the domain layer stays free of
 * Compose types and remains unit-testable on the JVM.
 */
val BmiCategory.color: Color
    get() = when (this) {
        BmiCategory.UNDERWEIGHT -> CategoryUnderweight
        BmiCategory.NORMAL -> CategoryNormal
        BmiCategory.OVERWEIGHT -> CategoryOverweight
        BmiCategory.OBESE -> CategoryObese
    }
