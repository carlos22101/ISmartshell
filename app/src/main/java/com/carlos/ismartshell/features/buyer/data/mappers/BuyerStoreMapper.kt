package com.carlos.ismartshell.features.buyer.data.mappers

import com.carlos.ismartshell.features.buyer.data.models.BuyerStoreDto
import com.carlos.ismartshell.features.buyer.domain.entities.BuyerStore

fun BuyerStoreDto.toDomain(): BuyerStore {
    return BuyerStore(id, name, description, address, lng, lat)
}