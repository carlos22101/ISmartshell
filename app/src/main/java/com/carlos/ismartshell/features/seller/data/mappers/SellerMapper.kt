package com.carlos.ismartshell.features.seller.data.mappers

import com.carlos.ismartshell.features.seller.data.models.SellerModels
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore

object SellerMapper {
    fun toDomain(dto: SellerModels.SellerBusinessDto) = SellerStore(
        id          = dto.id ?: "",
        ownerId     = dto.ownerId ?: "",
        name        = dto.name ?: "Sin nombre",
        description = dto.description ?: "",
        type        = dto.type ?: "",
        latitude    = dto.latitude ?: 0.0,
        longitude   = dto.longitude ?: 0.0,
        active      = dto.active ?: true
    )
}
