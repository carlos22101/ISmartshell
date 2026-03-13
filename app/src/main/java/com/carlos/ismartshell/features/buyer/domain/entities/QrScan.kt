package com.carlos.ismartshell.features.buyer.domain.entities

data class QrScan(
    val id: Int,
    val code: String,
    val label: String,
    val scannedAt: Long
)