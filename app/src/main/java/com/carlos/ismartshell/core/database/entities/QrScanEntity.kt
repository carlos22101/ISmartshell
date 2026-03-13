package com.carlos.ismartshell.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_scans")
data class QrScanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val code: String,
    val label: String,          // descripción del QR (ej. nombre del negocio)
    val scannedAt: Long = System.currentTimeMillis()
)