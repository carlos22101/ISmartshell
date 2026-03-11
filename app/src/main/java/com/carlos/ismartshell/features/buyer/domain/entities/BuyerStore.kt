package com.carlos.ismartshell.features.buyer.domain.entities

data class BuyerStore(
    val id: Int,
    val name: String,
    val description: String,
    val address: String,
    val lng: Double = 0.0,
    val lat: Double = 0.0
)