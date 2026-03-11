package com.carlos.ismartshell.features.seller.domain.usecases
import com.carlos.ismartshell.features.seller.data.models.CreateStoreRequest
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore
import com.carlos.ismartshell.features.seller.domain.repositories.SellerRepository
import javax.inject.Inject

class CreateStoreUseCase @Inject constructor(private val repository: SellerRepository) {
    suspend operator fun invoke(sellerId: Int, name: String, slug: String, desc: String, address: String, lat: Double, lng: Double): SellerStore =
        repository.createStore(CreateStoreRequest(name, slug, desc, address, lat, lng, sellerId))
}
