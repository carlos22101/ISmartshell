package com.carlos.ismartshell.features.auth.domain.entities

data class User(
    val id: Int,
    val email: String,
    val role: String,
    val token: String?
)