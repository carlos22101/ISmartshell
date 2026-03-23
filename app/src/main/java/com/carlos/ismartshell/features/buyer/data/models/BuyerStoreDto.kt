package com.carlos.ismartshell.features.buyer.data.models

import com.google.gson.annotations.SerializedName

object BuyerStoreDto {

    data class DeliveryPointDto(
        @SerializedName("id") val id: String? = null,
        @SerializedName("business_id") val businessId: String? = null,
        @SerializedName("name") val name: String? = null,
        @SerializedName("latitude") val latitude: Double? = null,
        @SerializedName("longitude") val longitude: Double? = null,
        @SerializedName("active") val active: Boolean? = null
    )

    data class BusinessDto(
        @SerializedName("id") val id: String? = null,
        @SerializedName("owner_id") val ownerId: String? = null,
        @SerializedName("name") val name: String? = null,
        @SerializedName("description") val description: String? = null,
        @SerializedName("type") val type: String? = null,
        @SerializedName("latitude") val latitude: Double? = null,
        @SerializedName("longitude") val longitude: Double? = null,
        @SerializedName("active") val active: Boolean? = null,
        @SerializedName("delivery_points") val deliveryPoints: List<DeliveryPointDto>? = null,
        @SerializedName("distance") val distance: Double? = null
    )

    data class ProductDto(
        @SerializedName("id") val id: String? = null,
        @SerializedName("business_id") val businessId: String? = null,
        @SerializedName("name") val name: String? = null,
        @SerializedName("description") val description: String? = null,
        @SerializedName("price") val price: Double? = null,
        @SerializedName("stock") val stock: Int? = null,
        @SerializedName("image_url") val imageUrl: String? = null,
        @SerializedName("active") val active: Boolean? = null
    )

    data class OrderItemDto(
        @SerializedName("id") val id: String? = null,
        @SerializedName("order_id") val orderId: String? = null,
        @SerializedName("product_id") val productId: String? = null,
        @SerializedName("quantity") val quantity: Int? = null,
        @SerializedName("unit_price") val unitPrice: Double? = null
    )

    data class OrderDto(
        @SerializedName("id") val id: String? = null,
        @SerializedName("buyer_id") val buyerId: String? = null,
        @SerializedName("business_id") val businessId: String? = null,
        @SerializedName("type") val type: String? = null,
        @SerializedName("status") val status: String? = null,
        @SerializedName("total") val total: Double? = null,
        @SerializedName("qr_code") val qrCode: String? = null,
        @SerializedName("delivery_point_id") val deliveryPointId: String? = null,
        @SerializedName("pickup_deadline") val pickupDeadline: String? = null,
        @SerializedName("created_at") val createdAt: String? = null,
        @SerializedName("items") val items: List<OrderItemDto>? = null
    )

    data class CreateOrderItemRequest(
        @SerializedName("product_id") val productId: String,
        @SerializedName("quantity") val quantity: Int
    )

    data class CreateOrderRequest(
        @SerializedName("business_id") val businessId: String,
        @SerializedName("type") val type: String,
        @SerializedName("items") val items: List<CreateOrderItemRequest>,
        @SerializedName("delivery_point_id") val deliveryPointId: String? = null,
        @SerializedName("reservation_hours") val reservationHours: Int = 24
    )
}
