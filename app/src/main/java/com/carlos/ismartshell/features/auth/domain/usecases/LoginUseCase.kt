package com.carlos.ismartshell.features.auth.domain.usecases

import com.carlos.ismartshell.features.auth.data.repositories.AuthRepository
import com.carlos.ismartshell.features.auth.domain.entities.User
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Pair<User, String>> =
        repo.login(email, password)
}