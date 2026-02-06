package com.carlos.ismartshell.features.seller.data.models

data class CreateStoreRequest(
    val name: String,
    val slug: String,
    val description: String,
    val address: String,
    val lat: Double,
    val lng: Double
)

data class UpdateStoreRequest(
    val name: String,
    val slug: String,
    val description: String,
    val address: String,
    val lat: Double,
    val lng: Double
)

data class SellerStoreDto(
    val id: Int,
    val name: String,
    val slug: String?,
    val description: String,
    val address: String,
    val lat: Double?,
    val lng: Double?
)