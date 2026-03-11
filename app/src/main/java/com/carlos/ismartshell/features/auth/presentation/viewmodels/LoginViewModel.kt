package com.carlos.ismartshell.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.features.auth.domain.usecases.LoginUseCase
import com.carlos.ismartshell.features.auth.presentation.screens.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado del Formulario de Login
data class LoginFormState(
    val email: String = "",
    val pass: String = ""
)

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {

    // Estado UI (Carga, Error, Éxito)
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Estado del Formulario (Inputs)
    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    fun onEmailChange(newValue: String) {
        _formState.update { it.copy(email = newValue) }
    }

    fun onPasswordChange(newValue: String) {
        _formState.update { it.copy(pass = newValue) }
    }

    fun login() {
        viewModelScope.launch {
            val currentState = _formState.value
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Usamos los datos guardados en el ViewModel
                val user = loginUseCase(currentState.email, currentState.pass)
                _uiState.update { it.copy(isLoading = false, user = user, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al iniciar sesión") }
            }
        }
    }

    fun resetState() {
        _uiState.update { AuthUiState() }
        _formState.update { LoginFormState() } // Limpiar campos al salir
    }
}