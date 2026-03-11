package com.carlos.ismartshell.features.seller.data.repositories

import com.carlos.ismartshell.core.network.ApiService
import com.carlos.ismartshell.features.seller.data.mappers.toDomain
import com.carlos.ismartshell.features.seller.data.models.CreateStoreRequest
import com.carlos.ismartshell.features.seller.data.models.UpdateStoreRequest
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore
import com.carlos.ismartshell.features.seller.domain.repositories.SellerRepository
import javax.inject.Inject

class SellerRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : SellerRepository {

    override suspend fun getStores(): List<SellerStore> =
        apiService.getStores().map { SellerStore(it.id, it.name, it.description, it.address) }

    override suspend fun createStore(request: CreateStoreRequest): SellerStore =
        apiService.createStore(request).toDomain()

    override suspend fun getStoreDetail(id: Int): SellerStore {
        val dto = apiService.getStoreDetail(id)
        return SellerStore(dto.id, dto.name, dto.description, dto.address)
    }

    override suspend fun updateStore(id: Int, request: UpdateStoreRequest): SellerStore =
        apiService.updateStore(id, request).toDomain()

    override suspend fun deleteStore(id: Int) = apiService.deleteStore(id)
}