package com.carlos.ismartshell.features.buyer.domain.repositories

import com.carlos.ismartshell.features.buyer.domain.entities.QrScan
import kotlinx.coroutines.flow.Flow

interface QrScanRepository {
    fun getHistory(): Flow<List<QrScan>>
    suspend fun save(code: String, label: String)
    suspend fun delete(scan: QrScan)
    suspend fun clearAll()
}