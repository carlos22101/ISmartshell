package com.carlos.ismartshell.core.di

import com.carlos.ismartshell.core.notifications.FcmRepository
import com.carlos.ismartshell.core.notifications.FcmRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FcmModule {

    @Binds
    @Singleton
    abstract fun bindFcmRepository(impl: FcmRepositoryImpl): FcmRepository
}