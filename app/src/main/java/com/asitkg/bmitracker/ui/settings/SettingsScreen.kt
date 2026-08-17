package com.asitkg.bmitracker.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asitkg.bmitracker.domain.model.HeightUnit
import com.asitkg.bmitracker.domain.model.WeightUnit
import com.asitkg.bmitracker.ui.components.UnitToggle
import com.asitkg.bmitracker.ui.components.ValidatedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSignedOut: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.signedOut) {
        if (state.signedOut) onSignedOut()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )

                state.noProfile -> Text(
                    text = "No profile selected.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                )

                else -> SettingsContent(state = state, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    viewModel: SettingsViewModel,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = state.profileName,
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Changes here update your BMI and weight history straight away.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Section(title = "Height") {
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

        Section(title = "Weight") {
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
                label = "Current weight",
                suffix = state.weightUnit.label,
                error = state.visibleWeightError,
                keyboardType = KeyboardType.Decimal,
            )
            Text(
                text = "Saving a new weight adds a point to your history chart.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        state.saveError?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
            )
        }

        if (state.savedAt != null) {
            Text(
                text = "Saved",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Button(
            onClick = viewModel::onSave,
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
                Text("Save changes")
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = viewModel::onSignOut,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Sign out")
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        content()
    }
}
