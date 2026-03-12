package com.carlos.ismartshell.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.features.auth.domain.usecases.RegisterUseCase
import com.carlos.ismartshell.features.auth.presentation.screens.AuthUiState
import com.carlos.ismartshell.features.auth.presentation.screens.RegisterFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(RegisterFormState())
    val formState: StateFlow<RegisterFormState> = _formState.asStateFlow()

    fun onEmailChange(email: String) {
        _formState.update { it.copy(email = email) }
    }

    fun onPasswordChange(pass: String) {
        _formState.update { it.copy(pass = pass) }
    }

    fun onUsernameChange(username: String) {
        _formState.update { it.copy(username = username) }
    }

    fun onFullNameChange(fullName: String) {
        _formState.update { it.copy(fullName = fullName) }
    }

    fun onPhoneChange(phone: String) {
        _formState.update { it.copy(phone = phone) }
    }

    fun onRoleChange(role: String) {
        _formState.update { it.copy(role = role) }
    }

    fun register() {
        val f = _formState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = registerUseCase(f.email, f.pass, f.role, f.username, f.fullName, f.phone)
                _uiState.update { it.copy(user = user, isSuccess = true, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error al registrar", isLoading = false) }
            }
        }
    }

    fun resetState() {
        _uiState.update { AuthUiState() }
    }
}
