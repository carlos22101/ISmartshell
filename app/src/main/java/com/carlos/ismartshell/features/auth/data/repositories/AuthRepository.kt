package com.carlos.ismartshell.features.auth.data.repositories

import com.carlos.ismartshell.features.auth.domain.entities.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Pair<User, String>>
    suspend fun register(name: String, email: String, password: String, role: String): Result<Pair<User, String>>
    suspend fun getMe(): Result<User>
}