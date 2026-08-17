package com.asitkg.bmitracker.ui.auth.login

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

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val showErrors: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isSubmitting: Boolean = false,
    val formError: String? = null,
    val isSignedIn: Boolean = false,
) {
    val isValid: Boolean get() = emailError == null && passwordError == null
    val visibleEmailError: String? get() = emailError.takeIf { showErrors }
    val visiblePasswordError: String? get() = passwordError.takeIf { showErrors }
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) = update { it.copy(email = value) }

    fun onPasswordChange(value: String) = update { it.copy(password = value) }

    fun onSubmit() {
        val state = _uiState.value
        if (!state.isValid) {
            _uiState.update { it.copy(showErrors = true) }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, formError = null) }

        viewModelScope.launch {
            when (val result = authRepository.signIn(state.email.trim(), state.password)) {
                AuthResult.Success ->
                    _uiState.update { it.copy(isSubmitting = false, isSignedIn = true) }

                is AuthResult.Failure ->
                    _uiState.update { it.copy(isSubmitting = false, formError = result.message) }
            }
        }
    }

    fun onGoogleIdToken(idToken: String) {
        _uiState.update { it.copy(isSubmitting = true, formError = null) }
        viewModelScope.launch {
            when (val result = authRepository.signInWithGoogle(idToken)) {
                AuthResult.Success ->
                    _uiState.update { it.copy(isSubmitting = false, isSignedIn = true) }

                is AuthResult.Failure ->
                    _uiState.update { it.copy(isSubmitting = false, formError = result.message) }
            }
        }
    }

    fun onGoogleCancelled() = _uiState.update { it.copy(isSubmitting = false) }

    fun onGoogleFailure(message: String) =
        _uiState.update { it.copy(isSubmitting = false, formError = message) }

    fun onGoogleStarted() = _uiState.update { it.copy(isSubmitting = true, formError = null) }

    private fun update(transform: (LoginUiState) -> LoginUiState) {
        _uiState.update { current ->
            val next = transform(current)
            next.copy(
                emailError = CredentialValidator.validateEmail(next.email).errorMessage,
                passwordError = CredentialValidator.validatePasswordPresent(next.password).errorMessage,
            )
        }
    }
}
