package com.carlos.ismartshell.features.buyer.data.models

import com.google.gson.annotations.SerializedName

object BuyerStoreDto {

    data class DeliveryPointDto(
        @SerializedName("ID") val id: String? = null,
        @SerializedName("BusinessID") val businessId: String? = null,
        @SerializedName("Name") val name: String? = null,
        @SerializedName("Latitude") val latitude: Double? = null,
        @SerializedName("Longitude") val longitude: Double? = null,
        @SerializedName("Active") val active: Boolean? = null
    )

    data class BusinessDto(
        @SerializedName("ID") val id: String? = null,
        @SerializedName("OwnerID") val ownerId: String? = null,
        @SerializedName("Name") val name: String? = null,
        @SerializedName("Description") val description: String? = null,
        @SerializedName("Type") val type: String? = null,
        @SerializedName("Latitude") val latitude: Double? = null,
        @SerializedName("Longitude") val longitude: Double? = null,
        @SerializedName("Active") val active: Boolean? = null,
        @SerializedName("DeliveryPoints") val deliveryPoints: List<DeliveryPointDto>? = null
    )

    data class ProductDto(
        @SerializedName("ID") val id: String? = null,
        @SerializedName("BusinessID") val businessId: String? = null,
        @SerializedName("Name") val name: String? = null,
        @SerializedName("Description") val description: String? = null,
        @SerializedName("Price") val price: Double? = null,
        @SerializedName("Stock") val stock: Int? = null,
        @SerializedName("ImageURL") val imageUrl: String? = null,
        @SerializedName("Active") val active: Boolean? = null
    )

    data class OrderItemDto(
        @SerializedName("ID") val id: String? = null,
        @SerializedName("OrderID") val orderId: String? = null,
        @SerializedName("ProductID") val productId: String? = null,
        @SerializedName("Quantity") val quantity: Int? = null,
        @SerializedName("UnitPrice") val unitPrice: Double? = null
    )

    data class OrderDto(
        @SerializedName("ID") val id: String? = null,
        @SerializedName("BuyerID") val buyerId: String? = null,
        @SerializedName("BusinessID") val businessId: String? = null,
        @SerializedName("Type") val type: String? = null,
        @SerializedName("Status") val status: String? = null,
        @SerializedName("Total") val total: Double? = null,
        @SerializedName("QRCode") val qrCode: String? = null,
        @SerializedName("DeliveryPointID") val deliveryPointId: String? = null,
        @SerializedName("PickupDeadline") val pickupDeadline: String? = null,
        @SerializedName("CreatedAt") val createdAt: String? = null,
        @SerializedName("Items") val items: List<OrderItemDto>? = null
    )

    data class CreateOrderItemRequest(
        @SerializedName("ProductID") val productId: String,
        @SerializedName("Quantity") val quantity: Int
    )

    data class CreateOrderRequest(
        @SerializedName("BusinessID") val businessId: String,
        @SerializedName("Type") val type: String,
        @SerializedName("Items") val items: List<CreateOrderItemRequest>,
        @SerializedName("DeliveryPointID") val deliveryPointId: String? = null,
        @SerializedName("ReservationHours") val reservationHours: Int = 24
    )
}
