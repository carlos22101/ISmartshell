package com.carlos.ismartshell.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.core.local.TokenManager
import com.carlos.ismartshell.core.notifications.FcmTokenSync
import com.carlos.ismartshell.features.auth.domain.usecases.LoginUseCase
import com.carlos.ismartshell.features.auth.presentation.screens.AuthUiState
import com.carlos.ismartshell.features.auth.presentation.screens.LoginFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val tokenManager: TokenManager,
    private val fcmTokenSync: FcmTokenSync
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(LoginFormState())
    val formState = _formState.asStateFlow()

    fun onEmailChange(value: String)    { _formState.update { it.copy(email = value) } }
    fun onPasswordChange(value: String) { _formState.update { it.copy(password = value) } }
    fun onTogglePasswordVisible()       { _formState.update { it.copy(passwordVisible = !it.passwordVisible) } }

    fun login() {
        val form = _formState.value
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            loginUseCase(form.email.trim(), form.password)
                .onSuccess { (user, token) ->
                    tokenManager.saveSession(token, user.id, user.role, user.name)
                    _uiState.value = AuthUiState.Success(user.role)
                    launch { fcmTokenSync.syncToken() }
                }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Error") }
        }
    }

    fun resetState() { _uiState.value = AuthUiState.Idle }
}