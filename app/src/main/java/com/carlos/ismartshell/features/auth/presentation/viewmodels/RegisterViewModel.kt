package com.carlos.ismartshell.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.features.auth.domain.usecases.RegisterUseCase
import com.carlos.ismartshell.features.auth.presentation.screens.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado específico para los campos del formulario
data class RegisterFormState(
    val email: String = "",
    val pass: String = "",
    val username: String = "",
    val fullName: String = "",
    val phone: String = "",
    val role: String = "BUYER"
)

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {

    // Estado de la UI (Carga, Error, Éxito)
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Estado del Formulario (Inputs del usuario)
    private val _formState = MutableStateFlow(RegisterFormState())
    val formState: StateFlow<RegisterFormState> = _formState.asStateFlow()

    // --- Funciones para actualizar los campos ---
    fun onEmailChange(newValue: String) {
        _formState.update { it.copy(email = newValue) }
    }

    fun onPasswordChange(newValue: String) {
        _formState.update { it.copy(pass = newValue) }
    }

    fun onUsernameChange(newValue: String) {
        _formState.update { it.copy(username = newValue) }
    }

    fun onFullNameChange(newValue: String) {
        _formState.update { it.copy(fullName = newValue) }
    }

    fun onPhoneChange(newValue: String) {
        _formState.update { it.copy(phone = newValue) }
    }

    fun onRoleChange(newValue: String) {
        _formState.update { it.copy(role = newValue) }
    }

    // --- Función de Registro ---
    fun register() {
        viewModelScope.launch {
            // Leemos los datos directamente del estado actual del ViewModel
            val currentState = _formState.value

            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val user = registerUseCase(
                    email = currentState.email,
                    pass = currentState.pass,
                    role = currentState.role,
                    username = currentState.username,
                    fullName = currentState.fullName,
                    phone = currentState.phone
                )
                _uiState.update { it.copy(isLoading = false, user = user, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al registrar") }
            }
        }
    }

    fun resetState() {
        _uiState.update { AuthUiState() }
        _formState.update { RegisterFormState() } // Opcional: limpiar formulario al salir
    }
}