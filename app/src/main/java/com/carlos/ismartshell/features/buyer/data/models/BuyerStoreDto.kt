package com.carlos.ismartshell.features.buyer.data.models

data class BuyerStoreDto(
    val id: Int,
    val name: String,
    val description: String,
    val address: String,
    val slug: String
)