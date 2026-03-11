package com.carlos.ismartshell.features.buyer.data.repositories

import com.carlos.ismartshell.core.network.ApiService
import com.carlos.ismartshell.features.buyer.data.mappers.toDomain
import com.carlos.ismartshell.features.buyer.domain.entities.BuyerStore
import com.carlos.ismartshell.features.buyer.domain.repositories.StoreRepository
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : StoreRepository {
    override suspend fun getStores(): List<BuyerStore> =
        apiService.getStores().map { it.toDomain() }
}