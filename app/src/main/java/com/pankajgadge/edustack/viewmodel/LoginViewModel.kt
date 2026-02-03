package com.pankajgadge.edustack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pankajgadge.api.repository.AuthRepository
import com.pankajgadge.common.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(authRepository.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            when (val result = authRepository.signIn(email, password)) {
                is AuthResult.Success -> {
                    _loginState.value = LoginState.Success(result.displayName)
                    _isLoggedIn.value = true
                }
                is AuthResult.Error -> {
                    _loginState.value = LoginState.Error(result.message)
                }
            }
        }
    }

    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            when (val result = authRepository.signUp(email, password, name)) {
                is AuthResult.Success -> {
                    _loginState.value = LoginState.Success(result.displayName)
                    _isLoggedIn.value = true
                }
                is AuthResult.Error -> {
                    _loginState.value = LoginState.Error(result.message)
                }
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _isLoggedIn.value = false
        _loginState.value = LoginState.Idle
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val userName: String) : LoginState()
    data class Error(val message: String) : LoginState()
}