package com.carlos.ismartshell.features.seller.domain.usecases

import com.carlos.ismartshell.features.seller.data.models.CreateStoreRequest
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore
import com.carlos.ismartshell.features.seller.domain.repositories.SellerRepository

class CreateStoreUseCase(private val repository: SellerRepository) {
    suspend operator fun invoke(sellerId: Int, name: String, slug: String, desc: String, address: String, lat: Double, lng: Double): SellerStore {
        val request = CreateStoreRequest(name, slug, desc, address, lat, lng, sellerId)
        return repository.createStore(request)
    }
}
