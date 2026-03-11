package com.carlos.ismartshell.features.auth.domain.usecases
import com.carlos.ismartshell.features.auth.domain.entities.User
import com.carlos.ismartshell.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, pass: String, role: String, username: String, fullName: String, phone: String): User =
        repository.register(email, pass, role, username, fullName, phone)
}