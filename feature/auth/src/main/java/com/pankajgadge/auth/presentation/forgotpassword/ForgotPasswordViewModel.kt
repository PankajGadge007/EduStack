package com.pankajgadge.auth.presentation.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pankajgadge.auth.domain.usecase.ForgotPasswordUseCase
import com.pankajgadge.core.common.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = null
        )
    }

    fun sendResetEmail() {
        val email = _uiState.value.email

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            when (val result = forgotPasswordUseCase(email)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        emailSent = true,
                        error = null
                    )
                }

                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        emailSent = false,
                        error = result.message ?: "Failed to send reset email"
                    )
                }

                is AuthResult.Info ->{
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        emailSent = false,
                        error = result.info ?: "forgotPasswordViewModel Info"
                    )
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = ForgotPasswordUiState()
    }
}

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val emailSent: Boolean = false,
    val error: String? = null
)