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
        apiService.getStores().map { 
            SellerStore(
                id = it.id, 
                name = it.name, 
                description = it.description, 
                address = it.address,
                slug = it.slug,
                lat = it.lat,
                lng = it.lng
            ) 
        }

    override suspend fun createStore(request: CreateStoreRequest): SellerStore =
        apiService.createStore(request).toDomain()

    override suspend fun getStoreDetail(id: Int): SellerStore {
        val dto = apiService.getStoreDetail(id)
        return SellerStore(
            id = dto.id, 
            name = dto.name, 
            description = dto.description, 
            address = dto.address,
            slug = dto.slug,
            lat = dto.lat,
            lng = dto.lng
        )
    }

    override suspend fun updateStore(id: Int, request: UpdateStoreRequest): SellerStore =
        apiService.updateStore(id, request).toDomain()

    override suspend fun deleteStore(id: Int) = apiService.deleteStore(id)
}
