package com.asitkg.bmitracker.ui.auth.signup

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

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val showErrors: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isSubmitting: Boolean = false,
    val formError: String? = null,
    val isSignedIn: Boolean = false,
) {
    val isValid: Boolean
        get() = emailError == null && passwordError == null && confirmPasswordError == null

    val visibleEmailError: String? get() = emailError.takeIf { showErrors }
    val visiblePasswordError: String? get() = passwordError.takeIf { showErrors }
    val visibleConfirmPasswordError: String? get() = confirmPasswordError.takeIf { showErrors }
}

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) = update { it.copy(email = value) }

    fun onPasswordChange(value: String) = update { it.copy(password = value) }

    fun onConfirmPasswordChange(value: String) = update { it.copy(confirmPassword = value) }

    fun onSubmit() {
        val state = _uiState.value
        if (!state.isValid) {
            _uiState.update { it.copy(showErrors = true) }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, formError = null) }

        viewModelScope.launch {
            when (val result = authRepository.signUp(state.email.trim(), state.password)) {
                AuthResult.Success ->
                    _uiState.update { it.copy(isSubmitting = false, isSignedIn = true) }

                is AuthResult.Failure ->
                    _uiState.update { it.copy(isSubmitting = false, formError = result.message) }
            }
        }
    }

    private fun update(transform: (SignUpUiState) -> SignUpUiState) {
        _uiState.update { current ->
            val next = transform(current)
            next.copy(
                emailError = CredentialValidator.validateEmail(next.email).errorMessage,
                passwordError = CredentialValidator.validateNewPassword(next.password).errorMessage,
                confirmPasswordError = CredentialValidator
                    .validatePasswordConfirmation(next.password, next.confirmPassword)
                    .errorMessage,
            )
        }
    }
}
