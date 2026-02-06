package com.carlos.ismartshell.features.seller.domain.usecases

import com.carlos.ismartshell.features.seller.domain.entities.SellerStore
import com.carlos.ismartshell.features.seller.domain.repositories.SellerRepository

class GetSellerStoreDetailUseCase(private val repository: SellerRepository) {
    suspend operator fun invoke(id: Int): SellerStore {
        return repository.getStoreDetail(id)
    }
}