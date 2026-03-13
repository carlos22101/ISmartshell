package com.carlos.ismartshell.features.auth.data.repositories

import com.carlos.ismartshell.core.network.ApiService
import com.carlos.ismartshell.features.auth.data.mappers.AuthMapper
import com.carlos.ismartshell.features.auth.data.models.AuthModels
import com.carlos.ismartshell.features.auth.domain.entities.User
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Pair<User, String>> =
        runCatching {
            val response = api.login(AuthModels.LoginRequest(email, password))
            val body = response.body()?.data
                ?: error(response.body()?.error ?: "Error al iniciar sesión")
            
            // Pasamos tanto el UserDto como el AuthData completo al Mapper
            val user = AuthMapper.toDomain(body.user, body)
            val token = body.token ?: error("No se recibió el token de autenticación")
            
            Pair(user, token)
        }

    override suspend fun register(
        name: String, email: String, password: String, role: String
    ): Result<Pair<User, String>> = runCatching {
        val response = api.register(AuthModels.RegisterRequest(name, email, password, role))
        val body = response.body()?.data
            ?: error(response.body()?.error ?: "Error al registrarse")
        
        val user = AuthMapper.toDomain(body.user, body)
        val token = body.token ?: error("No se recibió el token tras el registro")
        
        Pair(user, token)
    }

    override suspend fun getMe(): Result<User> = runCatching {
        val response = api.me()
        val dto = response.body()?.data ?: error(response.body()?.error ?: "Sin sesión")
        AuthMapper.toDomain(dto)
    }
}
