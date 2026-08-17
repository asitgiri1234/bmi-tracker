package com.asitkg.bmitracker.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asitkg.bmitracker.domain.model.Gender
import com.asitkg.bmitracker.domain.model.HeightUnit
import com.asitkg.bmitracker.domain.model.WeightUnit
import com.asitkg.bmitracker.ui.components.UnitToggle
import com.asitkg.bmitracker.ui.components.ValidatedTextField
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailsScreen(
    onSaved: (Long) -> Unit,
    viewModel: UserDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.savedProfileId) {
        state.savedProfileId?.let(onSaved)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Your details") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "We use these to calculate your BMI. You can change them at any time.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            ValidatedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = "Name",
                error = state.visibleNameError,
            )

            FieldSection(title = "Gender") {
                UnitToggle(
                    options = Gender.entries,
                    selected = state.gender,
                    onSelect = viewModel::onGenderChange,
                    label = { it.name.lowercase().replaceFirstChar(Char::uppercase) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            FieldSection(title = "Date of birth") {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        state.dateOfBirth
                            ?.format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
                            ?: "Select date (optional)",
                    )
                }
                state.visibleDateOfBirthError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            FieldSection(title = "Height") {
                UnitToggle(
                    options = HeightUnit.entries,
                    selected = state.heightUnit,
                    onSelect = viewModel::onHeightUnitChange,
                    label = { it.label },
                )
                Spacer(Modifier.height(4.dp))
                when (state.heightUnit) {
                    HeightUnit.CM -> ValidatedTextField(
                        value = state.heightCm,
                        onValueChange = viewModel::onHeightCmChange,
                        label = "Height",
                        suffix = "cm",
                        error = state.visibleHeightError,
                        keyboardType = KeyboardType.Decimal,
                    )

                    HeightUnit.FEET_INCHES -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ValidatedTextField(
                                value = state.heightFeet,
                                onValueChange = viewModel::onHeightFeetChange,
                                label = "Feet",
                                suffix = "ft",
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f),
                            )
                            ValidatedTextField(
                                value = state.heightInches,
                                onValueChange = viewModel::onHeightInchesChange,
                                label = "Inches",
                                suffix = "in",
                                keyboardType = KeyboardType.Decimal,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        state.visibleHeightError?.let { error ->
                            Text(
                                text = error,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }

            FieldSection(title = "Weight") {
                UnitToggle(
                    options = WeightUnit.entries,
                    selected = state.weightUnit,
                    onSelect = viewModel::onWeightUnitChange,
                    label = { it.label },
                )
                Spacer(Modifier.height(4.dp))
                ValidatedTextField(
                    value = state.weight,
                    onValueChange = viewModel::onWeightChange,
                    label = "Weight",
                    suffix = state.weightUnit.label,
                    error = state.visibleWeightError,
                    keyboardType = KeyboardType.Decimal,
                )
            }

            state.saveError?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Button(
                onClick = viewModel::onSubmit,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text("Continue")
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.dateOfBirth
                ?.atStartOfDay(ZoneId.systemDefault())
                ?.toInstant()
                ?.toEpochMilli(),
            // A future date of birth is never valid, so future days are made
            // unselectable rather than only rejected after the fact.
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    !Instant.ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDate()
                        .isAfter(LocalDate.now())

                override fun isSelectableYear(year: Int): Boolean =
                    year <= LocalDate.now().year
            },
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        // The picker reports UTC midnight; converting through the
                        // UTC zone keeps the calendar date the user tapped.
                        pickerState.selectedDateMillis?.let { millis ->
                            viewModel.onDateOfBirthChange(
                                Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate(),
                            )
                        }
                        showDatePicker = false
                    },
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun FieldSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
        )
        content()
    }
}
