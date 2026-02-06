package com.carlos.ismartshell.features.seller.domain.entities

data class SellerStore(
    val id: Int,
    val name: String,
    val description: String,
    val address: String,
    val slug: String? = "",
    val lat: Double? = 0.0,
    val lng: Double? = 0.0
)