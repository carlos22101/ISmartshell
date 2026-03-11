package com.carlos.ismartshell.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_scan_history")
data class QrScanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "raw_value")
    val rawValue: String,

    @ColumnInfo(name = "scanned_at")
    val scannedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "store_name")
    val storeName: String? = null,

    @ColumnInfo(name = "store_id")
    val storeId: Int? = null
)
