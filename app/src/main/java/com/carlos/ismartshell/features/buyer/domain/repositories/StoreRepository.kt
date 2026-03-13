package com.carlos.ismartshell.features.buyer.domain.repositories

import com.carlos.ismartshell.features.buyer.domain.entities.*

interface StoreRepository {
    suspend fun getNearbyStores(lat: Double, lng: Double, radiusKm: Double): Result<List<BuyerStore>>
    suspend fun getStoreById(id: String): Result<BuyerStore>
    suspend fun getProductsByStore(storeId: String): Result<List<Product>>
    suspend fun createOrder(
        businessId: String,
        type: String,
        items: List<Pair<String, Int>>,
        deliveryPointId: String?,
        reservationHours: Int
    ): Result<Order>
    suspend fun getMyOrders(): Result<List<Order>>
    suspend fun cancelOrder(orderId: String): Result<Order>
}