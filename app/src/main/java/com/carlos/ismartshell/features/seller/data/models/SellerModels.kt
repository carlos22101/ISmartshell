package com.carlos.ismartshell.features.seller.data.models

import com.google.gson.annotations.SerializedName

data class CreateStoreRequest(
    val name: String,
    val slug: String,
    val description: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    @SerializedName("seller_id") val sellerId: Int
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
    @SerializedName("seller_id") val seller_id: Int?,
    val name: String,
    val slug: String?,
    val description: String?,
    val address: String?,
    val lat: Double?,
    val lng: Double?
)
