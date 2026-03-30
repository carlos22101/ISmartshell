package com.carlos.ismartshell.features.qr_scanner.data.mappers

import com.carlos.ismartshell.core.database.entities.QrScanEntity
import com.carlos.ismartshell.features.qr_scanner.domain.entities.QrScan

object QrScanMapper {
    fun toDomain(entity: QrScanEntity) = QrScan(
        id = entity.id,
        code = entity.code,
        label = entity.label,
        scannedAt = entity.scannedAt
    )

    fun toEntity(domain: QrScan) = QrScanEntity(
        id = domain.id,
        code = domain.code,
        label = domain.label,
        scannedAt = domain.scannedAt
    )
}