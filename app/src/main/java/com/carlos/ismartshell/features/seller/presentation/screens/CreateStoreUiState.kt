package com.carlos.ismartshell.features.seller.presentation.screens

import android.location.Location
import com.carlos.ismartshell.features.buyer.domain.entities.Order
import com.carlos.ismartshell.features.buyer.domain.entities.Product
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore

data class CreateStoreUiState(
    val stores: List<SellerStore> = emptyList(),
    val userLocation: Location? = null,
    val selectedStore: SellerStore? = null,
    val products: List<Product> = emptyList(),
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null,
    val scannedOrder: Order? = null
)
