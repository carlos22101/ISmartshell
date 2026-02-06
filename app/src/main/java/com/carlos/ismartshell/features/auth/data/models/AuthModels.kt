package com.carlos.ismartshell.features.auth.data.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    @SerializedName("password") val pass: String
)

data class RegisterRequest(
    val email: String,
    @SerializedName("password") val pass: String,
    val role: String, // "BUYER", "SELLER", "ADMIN"
    val username: String? = null,
    @SerializedName("full_name") val fullName: String? = null,
    val phone: String? = null
)

data class UserDto(
    val id: Int,
    val email: String,
    val role: String,
    val token: String?
)