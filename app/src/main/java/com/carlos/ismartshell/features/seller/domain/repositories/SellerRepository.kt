package com.carlos.ismartshell.features.seller.domain.repositories

import com.carlos.ismartshell.features.buyer.domain.entities.Order
import com.carlos.ismartshell.features.buyer.domain.entities.Product
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore

interface SellerRepository {
    suspend fun getMyStores(): Result<List<SellerStore>>
    suspend fun getStoreDetail(id: String): Result<SellerStore>
    suspend fun createStore(name: String, description: String, type: String, lat: Double, lng: Double): Result<SellerStore>
    suspend fun updateStore(id: String, name: String, description: String, type: String, lat: Double, lng: Double): Result<SellerStore>
    suspend fun deleteStore(id: String): Result<Unit>
    suspend fun createProduct(businessId: String, name: String, description: String, price: Double, stock: Int): Result<Product>
    suspend fun updateProduct(productId: String, name: String, description: String, price: Double, stock: Int): Result<Product>
    suspend fun deleteProduct(productId: String): Result<Unit>
    suspend fun getOrdersByBusiness(businessId: String): Result<List<Order>>
    suspend fun scanOrderQr(qrCode: String): Result<Order>
    suspend fun markOrderAsReady(orderId: String): Result<Order>
}