package com.carlos.ismartshell.features.seller.data.mappers

import com.carlos.ismartshell.features.seller.data.models.SellerStoreDto
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore

fun SellerStoreDto.toDomain(): SellerStore {
    return SellerStore(
        id = id,
        name = name,
        description = description,
        address = address,
        slug = slug,
        lat = lat,
        lng = lng
    )
}