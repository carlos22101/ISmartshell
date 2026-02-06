package com.carlos.ismartshell.features.auth.di

import com.carlos.ismartshell.features.auth.domain.repositories.AuthRepository
import com.carlos.ismartshell.features.auth.domain.usecases.LoginUseCase
import com.carlos.ismartshell.features.auth.domain.usecases.RegisterUseCase

object AuthModule {
    fun provideLoginUseCase(repo: AuthRepository) = LoginUseCase(repo)
    fun provideRegisterUseCase(repo: AuthRepository) = RegisterUseCase(repo)
}