package com.pankajgadge.edustack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pankajgadge.domain.repository.AuthRepository
import com.pankajgadge.auth.domain.usecase.GoogleSignInUseCase
import com.pankajgadge.common.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,  // ✅ Gets FirebaseAuthRepositoryImpl automatically
    private val googleSignInUseCase: GoogleSignInUseCase  // ✅ Add this
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(authRepository.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    /**
     * Sign in with email and password
     */
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

                is AuthResult.Info ->{
                    _loginState.value = LoginState.Error(result.info)
                }
            }
        }
    }

    /**
     * Sign up with email, password, and name
     */
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
                is AuthResult.Info ->{
                    _loginState.value = LoginState.Error(result.info)
                }
            }
        }
    }

    /**
     * Sign in with Google
     * @param idToken The Google ID token received from Google Sign-In
     */
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            when (val result = googleSignInUseCase(idToken)) {
                is AuthResult.Success -> {
                    _loginState.value = LoginState.Success(result.displayName)
                    _isLoggedIn.value = true
                }
                is AuthResult.Error -> {
                    _loginState.value = LoginState.Error(result.message)
                }

                is AuthResult.Info -> {
                    _loginState.value = LoginState.Error(result.info)
                }
            }
        }
    }

    /**
     * Sign out the current user
     */
    fun signOut() {
        authRepository.signOut()
        _isLoggedIn.value = false
        _loginState.value = LoginState.Idle
    }

    /**
     * Reset the login state to Idle
     * Useful when switching between Sign In and Sign Up
     */
    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

/**
 * Sealed class representing different states of the login flow
 */
sealed class LoginState {
    /**
     * Initial state, no action taken yet
     */
    object Idle : LoginState()

    /**
     * Loading state, authentication is in progress
     */
    object Loading : LoginState()

    /**
     * Success state, user has been authenticated
     * @param userName The display name of the authenticated user
     */
    data class Success(val userName: String) : LoginState()

    /**
     * Error state, authentication failed
     * @param message The error message to display
     */
    data class Error(val message: String) : LoginState()
}