package com.carlos.ismartshell.features.qr_scanner.domain.entities

data class QrScan(
    val id: Int,
    val code: String,
    val label: String,
    val scannedAt: Long
)