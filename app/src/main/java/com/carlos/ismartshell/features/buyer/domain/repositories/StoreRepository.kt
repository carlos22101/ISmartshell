package com.carlos.ismartshell.features.buyer.domain.repositories

import com.carlos.ismartshell.features.buyer.domain.entities.BuyerStore

interface StoreRepository {
    suspend fun getStores(): List<BuyerStore>
}