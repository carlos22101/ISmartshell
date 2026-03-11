package com.carlos.ismartshell.features.buyer.domain.entities

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class QrScan(
    val id: Int,
    val rawValue: String,
    val scannedAt: Long,
    val storeName: String?,
    val storeId: Int?
) {
    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return sdf.format(Date(scannedAt))
        }
}