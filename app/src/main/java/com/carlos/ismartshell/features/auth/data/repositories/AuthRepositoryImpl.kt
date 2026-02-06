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
    private val tokenManager: TokenManager // <--- AGREGADO: Ahora acepta el TokenManager
) : AuthRepository {

    override suspend fun login(email: String, pass: String): User {
        // 1. Llamada a la API
        val response = apiService.login(LoginRequest(email, pass))

        // 2. IMPORTANTE: Guardamos el token en el celular
        // Usamos response.accessToken porque así lo definimos en el DTO nuevo
        tokenManager.saveToken(response.accessToken)

        // 3. Devolvemos el usuario al dominio
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

        // También guardamos el token al registrarse
        tokenManager.saveToken(response.accessToken)

        return response.toDomain()
    }
}