package com.carlos.ismartshell.core.di

import com.carlos.ismartshell.features.auth.data.repositories.AuthRepositoryImpl
import com.carlos.ismartshell.features.auth.domain.repositories.AuthRepository
import com.carlos.ismartshell.features.buyer.data.repositories.QrScanRepositoryImpl
import com.carlos.ismartshell.features.buyer.data.repositories.StoreRepositoryImpl
import com.carlos.ismartshell.features.buyer.domain.repositories.QrScanRepository
import com.carlos.ismartshell.features.buyer.domain.repositories.StoreRepository
import com.carlos.ismartshell.features.seller.data.repositories.SellerRepositoryImpl
import com.carlos.ismartshell.features.seller.domain.repositories.SellerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindStoreRepository(impl: StoreRepositoryImpl): StoreRepository

    @Binds
    @Singleton
    abstract fun bindSellerRepository(impl: SellerRepositoryImpl): SellerRepository

    @Binds
    @Singleton
    abstract fun bindQrScanRepository(impl: QrScanRepositoryImpl): QrScanRepository
}