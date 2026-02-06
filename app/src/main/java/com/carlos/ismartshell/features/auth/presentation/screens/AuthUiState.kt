package com.carlos.ismartshell.features.auth.presentation.screens

import com.carlos.ismartshell.features.auth.domain.entities.User

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isSuccess: Boolean = false
)