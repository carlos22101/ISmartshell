package com.carlos.ismartshell.features.buyer.domain.repositories

import com.carlos.ismartshell.features.buyer.domain.entities.QrScan
import kotlinx.coroutines.flow.Flow

interface QrScanRepository {
    suspend fun saveQrScan(rawValue: String, storeName: String?, storeId: Int?)
    fun getAllScans(): Flow<List<QrScan>>
    fun getRecentScans(limit: Int = 10): Flow<List<QrScan>>
    fun searchScans(query: String): Flow<List<QrScan>>
    suspend fun deleteQrScan(scan: QrScan)
    suspend fun clearHistory()
}