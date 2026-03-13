package com.carlos.ismartshell.features.buyer.domain.entities

data class DeliveryPoint(
    val id: String,
    val businessId: String,
    val name: String,
    val latitude: Double,
    val longitude: Double
)

data class Product(
    val id: String,
    val businessId: String,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val imageUrl: String?
)

data class BuyerStore(
    val id: String,
    val ownerId: String,
    val name: String,
    val description: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    val deliveryPoints: List<DeliveryPoint> = emptyList(),
    val products: List<Product> = emptyList()
)

data class OrderItem(
    val productId: String,
    val quantity: Int,
    val unitPrice: Double
)

data class Order(
    val id: String,
    val businessId: String,
    val type: String,
    val status: String,
    val total: Double,
    val qrCode: String?,
    val pickupDeadline: String?,
    val createdAt: String?,
    val items: List<OrderItem> = emptyList()
)