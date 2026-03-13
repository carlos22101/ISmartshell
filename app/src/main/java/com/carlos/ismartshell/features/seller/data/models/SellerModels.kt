package com.carlos.ismartshell.features.seller.data.models

import com.google.gson.annotations.SerializedName

object SellerModels {

    data class SellerBusinessDto(
        @SerializedName("ID") val id: String? = null,
        @SerializedName("OwnerID") val ownerId: String? = null,
        @SerializedName("Name") val name: String? = null,
        @SerializedName("Description") val description: String? = null,
        @SerializedName("Type") val type: String? = null,
        @SerializedName("Latitude") val latitude: Double? = null,
        @SerializedName("Longitude") val longitude: Double? = null,
        @SerializedName("Active") val active: Boolean? = null
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
        val latitude: Double,
        val longitude: Double
    )

    data class CreateProductRequest(
        val name: String,
        val description: String,
        val price: Double,
        val stock: Int,
        @SerializedName("ImageURL") val imageUrl: String = ""
    )

    data class UpdateProductRequest(
        val name: String,
        val description: String,
        val price: Double,
        val stock: Int,
        @SerializedName("ImageURL") val imageUrl: String = ""
    )

    data class ScanQrRequest(
        @SerializedName("QRCode") val qrCode: String
    )
}
