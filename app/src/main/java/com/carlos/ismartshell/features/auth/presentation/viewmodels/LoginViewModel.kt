package com.carlos.ismartshell.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.core.local.TokenManager
import com.carlos.ismartshell.features.auth.domain.usecases.LoginUseCase
import com.carlos.ismartshell.features.auth.presentation.screens.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            loginUseCase(email, password)
                .onSuccess { (user, token) ->
                    tokenManager.saveSession(token, user.id, user.role, user.name)
                    _uiState.value = AuthUiState.Success(user.role)
                }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Error") }
        }
    }

    fun resetState() { _uiState.value = AuthUiState.Idle }
}