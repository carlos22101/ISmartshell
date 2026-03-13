package com.carlos.ismartshell.features.seller.domain.usecases

import com.carlos.ismartshell.features.buyer.domain.entities.Order
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore
import com.carlos.ismartshell.features.seller.domain.repositories.SellerRepository
import javax.inject.Inject

class GetSellerStoresUseCase @Inject constructor(private val repo: SellerRepository) {
    suspend operator fun invoke(): Result<List<SellerStore>> = repo.getMyStores()
}

class GetSellerStoreDetailUseCase @Inject constructor(private val repo: SellerRepository) {
    suspend operator fun invoke(id: String): Result<SellerStore> = repo.getStoreDetail(id)
}

class CreateStoreUseCase @Inject constructor(private val repo: SellerRepository) {
    suspend operator fun invoke(
        name: String, description: String, type: String, lat: Double, lng: Double
    ): Result<SellerStore> = repo.createStore(name, description, type, lat, lng)
}

class UpdateStoreUseCase @Inject constructor(private val repo: SellerRepository) {
    suspend operator fun invoke(
        id: String, name: String, description: String, type: String, lat: Double, lng: Double
    ): Result<SellerStore> = repo.updateStore(id, name, description, type, lat, lng)
}

class DeleteStoreUseCase @Inject constructor(private val repo: SellerRepository) {
    suspend operator fun invoke(id: String): Result<Unit> = repo.deleteStore(id)
}