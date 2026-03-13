package com.carlos.ismartshell.features.buyer.data.mappers

import com.carlos.ismartshell.features.buyer.data.models.BuyerStoreDto
import com.carlos.ismartshell.features.buyer.domain.entities.*

object BuyerStoreMapper {

    fun toDomain(dto: BuyerStoreDto.BusinessDto) = BuyerStore(
        id             = dto.id ?: "",
        ownerId        = dto.ownerId ?: "",
        name           = dto.name ?: "Tienda",
        description    = dto.description ?: "",
        type           = dto.type ?: "",
        latitude       = dto.latitude ?: 0.0,
        longitude      = dto.longitude ?: 0.0,
        deliveryPoints = dto.deliveryPoints?.map { dpToDomain(it) } ?: emptyList()
    )

    fun dpToDomain(dto: BuyerStoreDto.DeliveryPointDto) = DeliveryPoint(
        id         = dto.id ?: "",
        businessId = dto.businessId ?: "",
        name       = dto.name ?: "",
        latitude   = dto.latitude ?: 0.0,
        longitude  = dto.longitude ?: 0.0
    )

    fun productToDomain(dto: BuyerStoreDto.ProductDto) = Product(
        id          = dto.id ?: "",
        businessId  = dto.businessId ?: "",
        name        = dto.name ?: "Producto",
        description = dto.description ?: "",
        price       = dto.price ?: 0.0,
        stock       = dto.stock ?: 0,
        imageUrl    = dto.imageUrl ?: ""
    )

    fun orderToDomain(dto: BuyerStoreDto.OrderDto) = Order(
        id             = dto.id ?: "",
        businessId     = dto.businessId ?: "",
        type           = dto.type ?: "online",
        status         = dto.status ?: "pending",
        total          = dto.total ?: 0.0,
        qrCode         = dto.qrCode,
        pickupDeadline = dto.pickupDeadline,
        createdAt      = dto.createdAt,
        items          = dto.items?.map { itemToDomain(it) } ?: emptyList()
    )

    private fun itemToDomain(dto: BuyerStoreDto.OrderItemDto) = OrderItem(
        productId  = dto.productId ?: "",
        quantity   = dto.quantity ?: 0,
        unitPrice  = dto.unitPrice ?: 0.0
    )
}
