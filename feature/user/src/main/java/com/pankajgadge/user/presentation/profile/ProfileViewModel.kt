package com.pankajgadge.user.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pankajgadge.core.common.result.Result
import com.pankajgadge.core.domain.model.UserProfile
import com.pankajgadge.core.domain.repository.AuthSessionRepository
import com.pankajgadge.user.domain.usecase.GetUserProfileUseCase
import com.pankajgadge.user.domain.usecase.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for user profile screen
 * Handles profile loading, editing, and updating
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val authSessionRepository: AuthSessionRepository
) : ViewModel() {


    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    init {
        loadProfile()
    }

    /**
     * Load user profile from repository
     */
    fun loadProfile() {
        val userId = authSessionRepository.getCurrentUserId() ?: return

        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            when (val result = getUserProfileUseCase(userId)) {
                is Result.Success -> {
                    _uiState.value = ProfileUiState.Success(result.data)
                }

                is Result.Error -> {
                    _uiState.value = ProfileUiState.Error(
                        result.exception.message ?: "Failed to load profile"
                    )
                }

                is Result.Loading -> {
                    _uiState.value = ProfileUiState.Loading
                }
            }
        }
    }


    /**
     * Toggle edit mode
     */
    fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value

        // Reset to original profile if canceling edit
        if (!_isEditMode.value) {
            loadProfile()
        }
    }

    /**
     * Update profile field
     */
    fun updateField(updater: (UserProfile) -> UserProfile) {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Success) {
            _uiState.value = ProfileUiState.Success(updater(currentState.profile))
        }
    }

    /**
     * Save profile changes
     */
    fun saveProfile() {
        val currentState = _uiState.value
        if (currentState !is ProfileUiState.Success) return

        viewModelScope.launch {
            _uiState.value = ProfileUiState.Saving(currentState.profile)

            when (val result = updateUserProfileUseCase(currentState.profile)) {
                is Result.Success -> {
                    _uiState.value = ProfileUiState.Success(currentState.profile)
                    _isEditMode.value = false
                }

                is Result.Error -> {
                    _uiState.value = ProfileUiState.Error(
                        result.exception.message ?: "Failed to save profile"
                    )
                }

                is Result.Loading -> {
                    _uiState.value = ProfileUiState.Saving(currentState.profile)
                }
            }
        }
    }

    /**
     * Sign out current user
     */
    fun signOut() {
        authSessionRepository.signOut()
        _uiState.value = ProfileUiState.Loading
    }
}

/**
 * UI state for profile screen
 */
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val profile: UserProfile) : ProfileUiState()
    data class Saving(val profile: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}