package com.carlos.ismartshell.features.auth.data.models

import com.google.gson.annotations.SerializedName

// --- REQUESTS (Lo que envías) ---
data class LoginRequest(
    val email: String,
    @SerializedName("password") val pass: String
)

data class RegisterRequest(
    val email: String,
    @SerializedName("password") val pass: String,
    val role: String,
    val username: String? = null,
    @SerializedName("full_name") val fullName: String? = null,
    val phone: String? = null
)

// --- RESPONSES (Lo que recibes) ---

// 1. Esta clase representa la respuesta COMPLETA (el JSON entero)
data class LoginResponseDto(
    @SerializedName("access_token")
    val accessToken: String, // El token está afuera

    @SerializedName("token_type")
    val tokenType: String,

    val user: UserDto // Aquí adentro vienen los datos del usuario
)

// 2. Esta clase representa solo el objeto "user" de adentro
data class UserDto(
    val id: Int,
    val email: String,
    val username: String,

    val role: String, // ¡AQUÍ ESTÁ EL ROL! (Por fin lo leerá bien)

    @SerializedName("full_name")
    val fullName: String,

    val phone: String
)