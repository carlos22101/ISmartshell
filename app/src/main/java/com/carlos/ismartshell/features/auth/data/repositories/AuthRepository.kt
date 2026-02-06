package com.carlos.ismartshell.features.auth.domain.repositories

import com.carlos.ismartshell.features.auth.domain.entities.User

interface AuthRepository {
    suspend fun login(email: String, pass: String): User
    suspend fun register(email: String, pass: String, role: String, username: String, fullName: String, phone: String): User
}