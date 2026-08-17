package com.asitkg.bmitracker.ui.auth.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asitkg.bmitracker.domain.repository.AuthRepository
import com.asitkg.bmitracker.domain.repository.AuthResult
import com.asitkg.bmitracker.domain.validation.CredentialValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForgotPasswordUiState(
    val email: String = "",
    val showErrors: Boolean = false,
    val emailError: String? = null,
    val isSubmitting: Boolean = false,
    val formError: String? = null,
    val emailSent: Boolean = false,
) {
    val visibleEmailError: String? get() = emailError.takeIf { showErrors }
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update {
            it.copy(
                email = value,
                emailError = CredentialValidator.validateEmail(value).errorMessage,
                emailSent = false,
            )
        }
    }

    fun onSubmit() {
        val state = _uiState.value
        if (state.emailError != null) {
            _uiState.update { it.copy(showErrors = true) }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, formError = null) }

        viewModelScope.launch {
            when (val result = authRepository.sendPasswordReset(state.email.trim())) {
                // Success is reported even for an unregistered address: telling
                // the user which emails have accounts would leak that fact to
                // anyone who asks.
                AuthResult.Success ->
                    _uiState.update { it.copy(isSubmitting = false, emailSent = true) }

                is AuthResult.Failure ->
                    _uiState.update { it.copy(isSubmitting = false, formError = result.message) }
            }
        }
    }
}
