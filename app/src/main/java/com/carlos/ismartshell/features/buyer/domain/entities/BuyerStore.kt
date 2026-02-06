package com.carlos.ismartshell.features.buyer.domain.entities

data class BuyerStore(
    val id: Int,
    val name: String,
    val description: String,
    val address: String
)