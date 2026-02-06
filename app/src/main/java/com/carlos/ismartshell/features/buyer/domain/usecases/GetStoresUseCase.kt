package com.carlos.ismartshell.features.buyer.domain.usecases

import com.carlos.ismartshell.features.buyer.domain.entities.BuyerStore
import com.carlos.ismartshell.features.buyer.domain.repositories.StoreRepository

class GetStoresUseCase(private val repository: StoreRepository) {
    suspend operator fun invoke(): List<BuyerStore> {
        return repository.getStores()
    }
}