package com.carlos.ismartshell.features.buyer.presentation.screens

import android.location.Location
import com.carlos.ismartshell.features.buyer.domain.entities.BuyerStore
import com.carlos.ismartshell.features.buyer.domain.entities.Order
import com.carlos.ismartshell.features.buyer.domain.entities.Product

data class HomeBuyerUiState(
    val stores: List<BuyerStore> = emptyList(),
    val userLocation: Location? = null,
    val selectedStore: BuyerStore? = null,
    val products: List<Product> = emptyList(),
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val orderSuccess: Order? = null,
    val showOrderDialog: Boolean = false,
    val selectedProduct: Product? = null,
    val orderType: String = "online",
    val quantity: Int = 1,
    val selectedCategory: String = "Todas",
    val isMapViewActive: Boolean = false
)