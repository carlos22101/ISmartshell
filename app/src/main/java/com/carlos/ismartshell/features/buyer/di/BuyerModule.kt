package com.carlos.ismartshell.features.buyer.di

import com.carlos.ismartshell.features.buyer.domain.repositories.StoreRepository
import com.carlos.ismartshell.features.buyer.domain.usecases.GetStoresUseCase

object BuyerModule {
    fun provideGetStoresUseCase(repo: StoreRepository) = GetStoresUseCase(repo)
}