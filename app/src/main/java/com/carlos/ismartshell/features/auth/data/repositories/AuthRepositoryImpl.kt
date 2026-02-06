package com.carlos.ismartshell.features.auth.data.repositories

import com.carlos.ismartshell.core.local.TokenManager
import com.carlos.ismartshell.core.network.ApiService
import com.carlos.ismartshell.features.auth.data.mappers.toDomain
import com.carlos.ismartshell.features.auth.data.models.LoginRequest
import com.carlos.ismartshell.features.auth.data.models.RegisterRequest
import com.carlos.ismartshell.features.auth.domain.entities.User
import com.carlos.ismartshell.features.auth.domain.repositories.AuthRepository

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, pass: String): User {
        val response = apiService.login(LoginRequest(email, pass))
        tokenManager.saveToken(response.accessToken)
        tokenManager.saveUserId(response.user.id)
        return response.toDomain()
    }

    override suspend fun register(
        email: String,
        pass: String,
        role: String,
        username: String,
        fullName: String,
        phone: String
    ): User {
        val request = RegisterRequest(
            email = email,
            pass = pass,
            role = role,
            username = username,
            fullName = fullName,
            phone = phone
        )
        val response = apiService.register(request)
        tokenManager.saveToken(response.accessToken)
        tokenManager.saveUserId(response.user.id)
        return response.toDomain()
    }
}
