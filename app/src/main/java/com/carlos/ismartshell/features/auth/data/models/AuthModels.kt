package com.carlos.ismartshell.features.auth.data.models

import com.google.gson.annotations.SerializedName

object AuthModels {

    data class RegisterRequest(
        val name: String,
        val email: String,
        val password: String,
        val role: String
    )

    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class UserDto(
        @SerializedName("ID") val id: String? = null,
        @SerializedName("Name") val name: String? = null,
        @SerializedName("Email") val email: String? = null,
        @SerializedName("Role") val role: String? = null,
        @SerializedName("Active") val active: Boolean? = null,
        @SerializedName("CreatedAt") val createdAt: String? = null
    )

    data class AuthData(
        @SerializedName("user", alternate = ["User"]) val user: UserDto? = null,
        @SerializedName("token", alternate = ["Token"]) val token: String? = null,
        @SerializedName("Role") val flatRole: String? = null,
        @SerializedName("ID") val flatId: String? = null
    )
}
