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
        val dto = apiService.login(LoginRequest(email, pass))
        dto.token?.let { tokenManager.saveToken(it) }
        return dto.toDomain()
    }

    override suspend fun register(email: String, pass: String, role: String, username: String, fullName: String, phone: String): User {
        val request = RegisterRequest(
            email = email,
            pass = pass,
            role = role,
            username = username,
            fullName = fullName,
            phone = phone
        )
        val dto = apiService.register(request)
        // Opcional: guardar token si el registro lo devuelve
        dto.token?.let { tokenManager.saveToken(it) }
        return dto.toDomain()
    }
}