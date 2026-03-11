package com.carlos.ismartshell.features.auth.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.features.auth.domain.usecases.RegisterUseCase
import com.carlos.ismartshell.features.auth.presentation.screens.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun register(email: String, pass: String, role: String, username: String, fullName: String, phone: String) {
        viewModelScope.launch {
            uiState = AuthUiState(isLoading = true)
            try {
                val user = registerUseCase(email, pass, role, username, fullName, phone)
                uiState = AuthUiState(user = user, isSuccess = true)
            } catch (e: Exception) {
                uiState = AuthUiState(error = e.message ?: "Error al registrar")
            }
        }
    }
}