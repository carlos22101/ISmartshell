package com.carlos.ismartshell.features.seller.domain.usecases

import com.carlos.ismartshell.features.seller.domain.repositories.SellerRepository

class DeleteStoreUseCase(private val repository: SellerRepository) {
    suspend operator fun invoke(id: Int) {
        repository.deleteStore(id)
    }
}