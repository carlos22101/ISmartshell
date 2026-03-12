package com.carlos.ismartshell.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.features.auth.domain.usecases.LoginUseCase
import com.carlos.ismartshell.features.auth.presentation.screens.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    fun onEmailChange(email: String) {
        _formState.update { it.copy(email = email) }
    }

    fun onPasswordChange(pass: String) {
        _formState.update { it.copy(pass = pass) }
    }

    fun login() {
        val email = _formState.value.email
        val pass = _formState.value.pass
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = loginUseCase(email, pass)
                _uiState.update { it.copy(user = user, isSuccess = true, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error desconocido", isLoading = false) }
            }
        }
    }

    fun resetState() {
        _uiState.update { AuthUiState() }
    }
}

data class LoginFormState(
    val email: String = "",
    val pass: String = ""
)
