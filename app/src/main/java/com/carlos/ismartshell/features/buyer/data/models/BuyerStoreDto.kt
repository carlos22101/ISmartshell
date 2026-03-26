package com.carlos.ismartshell.features.buyer.data.models

import com.google.gson.annotations.SerializedName

object BuyerStoreDto {

    data class DeliveryPointDto(
        @SerializedName("id", alternate = ["ID"]) val id: String? = null,
        @SerializedName("business_id", alternate = ["BusinessID"]) val businessId: String? = null,
        @SerializedName("name", alternate = ["Name"]) val name: String? = null,
        @SerializedName("latitude", alternate = ["Latitude", "lat", "Lat"]) val latitude: Double? = null,
        @SerializedName("longitude", alternate = ["Longitude", "lng", "Lng"]) val longitude: Double? = null,
        @SerializedName("active", alternate = ["Active"]) val active: Boolean? = null
    )

    data class BusinessDto(
        @SerializedName("id", alternate = ["ID"]) val id: String? = null,
        @SerializedName("owner_id", alternate = ["OwnerID"]) val ownerId: String? = null,
        @SerializedName("name", alternate = ["Name"]) val name: String? = null,
        @SerializedName("description", alternate = ["Description"]) val description: String? = null,
        @SerializedName("type", alternate = ["Type"]) val type: String? = null,
        @SerializedName("latitude", alternate = ["Latitude", "lat", "Lat"]) val latitude: Double? = null,
        @SerializedName("longitude", alternate = ["Longitude", "lng", "Lng"]) val longitude: Double? = null,
        @SerializedName("active", alternate = ["Active"]) val active: Boolean? = null,
        @SerializedName("delivery_points", alternate = ["DeliveryPoints"]) val deliveryPoints: List<DeliveryPointDto>? = null,
        @SerializedName("products", alternate = ["Products"]) val products: List<ProductDto>? = null,
        @SerializedName("distance", alternate = ["Distance"]) val distance: Double? = null
    )

    data class ProductDto(
        @SerializedName("id", alternate = ["ID"]) val id: String? = null,
        @SerializedName("business_id", alternate = ["BusinessID"]) val businessId: String? = null,
        @SerializedName("name", alternate = ["Name"]) val name: String? = null,
        @SerializedName("description", alternate = ["Description"]) val description: String? = null,
        @SerializedName("price", alternate = ["Price"]) val price: Double? = null,
        @SerializedName("stock", alternate = ["Stock"]) val stock: Int? = null,
        @SerializedName("image_url", alternate = ["ImageURL"]) val imageUrl: String? = null,
        @SerializedName("active", alternate = ["Active"]) val active: Boolean? = null
    )

    data class OrderItemDto(
        @SerializedName("id", alternate = ["ID"]) val id: String? = null,
        @SerializedName("order_id", alternate = ["OrderID"]) val orderId: String? = null,
        @SerializedName("product_id", alternate = ["ProductID"]) val productId: String? = null,
        @SerializedName("quantity", alternate = ["Quantity"]) val quantity: Int? = null,
        @SerializedName("unit_price", alternate = ["UnitPrice"]) val unitPrice: Double? = null
    )

    data class OrderDto(
        @SerializedName("id", alternate = ["ID"]) val id: String? = null,
        @SerializedName("buyer_id", alternate = ["BuyerID"]) val buyerId: String? = null,
        @SerializedName("business_id", alternate = ["BusinessID"]) val businessId: String? = null,
        @SerializedName("type", alternate = ["Type"]) val type: String? = null,
        @SerializedName("status", alternate = ["Status"]) val status: String? = null,
        @SerializedName("total", alternate = ["Total"]) val total: Double? = null,
        @SerializedName("qr_code", alternate = ["QRCode"]) val qrCode: String? = null,
        @SerializedName("delivery_point_id", alternate = ["DeliveryPointID"]) val deliveryPointId: String? = null,
        @SerializedName("pickup_deadline", alternate = ["PickupDeadline"]) val pickupDeadline: String? = null,
        @SerializedName("created_at", alternate = ["CreatedAt"]) val createdAt: String? = null,
        @SerializedName("items", alternate = ["Items"]) val items: List<OrderItemDto>? = null
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
        @SerializedName("reservation_hours") val reservationHours: Int = 1
    )
}
