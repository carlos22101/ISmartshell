package com.carlos.ismartshell.features.seller.data.models

import com.google.gson.annotations.SerializedName

object SellerModels {

    data class SellerBusinessDto(
        @SerializedName("id") val id: String? = null,
        @SerializedName("owner_id") val ownerId: String? = null,
        @SerializedName("name") val name: String? = null,
        @SerializedName("description") val description: String? = null,
        @SerializedName("type") val type: String? = null,
        @SerializedName("latitude") val latitude: Double? = null,
        @SerializedName("longitude") val longitude: Double? = null,
        @SerializedName("active") val active: Boolean? = null
    )

    data class CreateBusinessRequest(
        val name: String,
        val description: String,
        val type: String,
        val latitude: Double,
        val longitude: Double
    )

    data class UpdateBusinessRequest(
        val name: String,
        val description: String,
        val type: String,
        val latitude: Double,
        val longitude: Double
    )

    data class DeliveryPointRequest(
        val name: String,
        @SerializedName("latitude") val latitude: Double,
        @SerializedName("longitude") val longitude: Double
    )

    data class CreateProductRequest(
        @SerializedName("name") val name: String,
        @SerializedName("description") val description: String,
        @SerializedName("price") val price: Double,
        @SerializedName("stock") val stock: Int,
        @SerializedName("image_url") val imageUrl: String = ""
    )

    data class UpdateProductRequest(
        @SerializedName("name") val name: String,
        @SerializedName("description") val description: String,
        @SerializedName("price") val price: Double,
        @SerializedName("stock") val stock: Int,
        @SerializedName("image_url") val imageUrl: String = ""
    )

    data class ScanQrRequest(
        @SerializedName("qr_code") val qrCode: String
    )
}
