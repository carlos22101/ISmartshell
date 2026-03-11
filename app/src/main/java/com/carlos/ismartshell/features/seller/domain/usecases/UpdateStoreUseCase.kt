package com.carlos.ismartshell.features.seller.domain.usecases
import com.carlos.ismartshell.features.seller.data.models.UpdateStoreRequest
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore
import com.carlos.ismartshell.features.seller.domain.repositories.SellerRepository
import javax.inject.Inject

class UpdateStoreUseCase @Inject constructor(private val repository: SellerRepository) {
    suspend operator fun invoke(id: Int, name: String, slug: String, desc: String, address: String, lat: Double, lng: Double): SellerStore =
        repository.updateStore(id, UpdateStoreRequest(name, slug, desc, address, lat, lng))
}