package com.carlos.ismartshell.features.seller.domain.usecases

import com.carlos.ismartshell.features.seller.data.models.UpdateStoreRequest
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore
import com.carlos.ismartshell.features.seller.domain.repositories.SellerRepository

class UpdateStoreUseCase(private val repository: SellerRepository) {
    suspend operator fun invoke(id: Int, name: String, slug: String, desc: String, address: String, lat: Double, lng: Double): SellerStore {
        val request = UpdateStoreRequest(name, slug, desc, address, lat, lng)
        return repository.updateStore(id, request)
    }
}