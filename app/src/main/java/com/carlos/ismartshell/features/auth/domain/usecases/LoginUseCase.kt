package com.carlos.ismartshell.features.auth.domain.usecases

import com.carlos.ismartshell.features.auth.domain.entities.User
import com.carlos.ismartshell.features.auth.domain.repositories.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, pass: String): User {
        return repository.login(email, pass)
    }
}