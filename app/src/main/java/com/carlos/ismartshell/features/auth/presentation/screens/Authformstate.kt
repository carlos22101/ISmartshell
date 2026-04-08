package com.carlos.ismartshell.features.auth.presentation.screens

data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false
)

data class RegisterFormState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isSeller: Boolean = false
)