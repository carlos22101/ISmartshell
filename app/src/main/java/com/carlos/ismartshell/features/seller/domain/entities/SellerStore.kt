package com.carlos.ismartshell.features.seller.domain.entities

import com.carlos.ismartshell.features.buyer.domain.entities.Order
import com.carlos.ismartshell.features.buyer.domain.entities.Product

data class SellerStore(
    val id: String,
    val ownerId: String,
    val name: String,
    val description: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    val active: Boolean,
    val products: List<Product> = emptyList(),
    val orders: List<Order> = emptyList()
)