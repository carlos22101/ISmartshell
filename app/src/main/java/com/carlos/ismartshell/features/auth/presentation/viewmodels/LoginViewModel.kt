package com.carlos.ismartshell.features.auth.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.features.auth.domain.usecases.LoginUseCase
import com.carlos.ismartshell.features.auth.presentation.screens.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            uiState = AuthUiState(isLoading = true)
            try {
                val user = loginUseCase(email, pass)
                uiState = AuthUiState(user = user, isSuccess = true)
            } catch (e: Exception) {
                uiState = AuthUiState(error = e.message ?: "Error desconocido")
            }
        }
    }
}