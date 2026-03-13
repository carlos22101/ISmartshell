package com.carlos.ismartshell.features.auth.domain.usecases

import com.carlos.ismartshell.features.auth.data.repositories.AuthRepository
import com.carlos.ismartshell.features.auth.domain.entities.User
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(
        name: String, email: String, password: String, role: String
    ): Result<Pair<User, String>> = repo.register(name, email, password, role)
}