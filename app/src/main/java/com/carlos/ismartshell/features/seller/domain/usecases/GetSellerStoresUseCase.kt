package com.carlos.ismartshell.features.seller.domain.usecases

import com.carlos.ismartshell.features.seller.domain.entities.SellerStore
import com.carlos.ismartshell.features.seller.domain.repositories.SellerRepository

class GetSellerStoresUseCase(private val repository: SellerRepository) {
    suspend operator fun invoke(): List<SellerStore> {
        return repository.getStores()
    }
}