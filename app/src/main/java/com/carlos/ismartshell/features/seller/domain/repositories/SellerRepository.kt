package com.carlos.ismartshell.features.seller.domain.repositories

import com.carlos.ismartshell.features.seller.data.models.CreateStoreRequest
import com.carlos.ismartshell.features.seller.data.models.UpdateStoreRequest
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore

interface SellerRepository {
    suspend fun getStores(): List<SellerStore>
    suspend fun createStore(request: CreateStoreRequest): SellerStore
    suspend fun getStoreDetail(id: Int): SellerStore
    suspend fun updateStore(id: Int, request: UpdateStoreRequest): SellerStore
    suspend fun deleteStore(id: Int)
}