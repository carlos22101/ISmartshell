package com.carlos.ismartshell.features.seller.di

import com.carlos.ismartshell.features.seller.domain.repositories.SellerRepository
import com.carlos.ismartshell.features.seller.domain.usecases.*

object SellerModule {
    fun provideGetStoresUseCase(repo: SellerRepository) = GetSellerStoresUseCase(repo)
    fun provideCreateStoreUseCase(repo: SellerRepository) = CreateStoreUseCase(repo)
    fun provideGetDetailUseCase(repo: SellerRepository) = GetSellerStoreDetailUseCase(repo)
    fun provideUpdateStoreUseCase(repo: SellerRepository) = UpdateStoreUseCase(repo)
    fun provideDeleteStoreUseCase(repo: SellerRepository) = DeleteStoreUseCase(repo)
}