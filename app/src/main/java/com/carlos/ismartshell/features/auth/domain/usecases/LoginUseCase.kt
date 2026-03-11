package com.carlos.ismartshell.features.auth.domain.usecases
import com.carlos.ismartshell.features.auth.domain.entities.User
import com.carlos.ismartshell.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, pass: String): User = repository.login(email, pass)
}
