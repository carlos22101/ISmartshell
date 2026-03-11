package com.carlos.ismartshell.features.buyer.data.mappers

import com.carlos.ismartshell.core.database.entities.QrScanEntity
import com.carlos.ismartshell.features.buyer.domain.entities.QrScan

fun QrScanEntity.toDomain() = QrScan(
    id        = id,
    rawValue  = rawValue,
    scannedAt = scannedAt,
    storeName = storeName,
    storeId   = storeId
)

fun QrScan.toEntity() = QrScanEntity(
    id        = id,
    rawValue  = rawValue,
    scannedAt = scannedAt,
    storeName = storeName,
    storeId   = storeId
)