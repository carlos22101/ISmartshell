package com.carlos.ismartshell.features.buyer.data.repositories

import com.carlos.ismartshell.core.database.dao.QrScanDao
import com.carlos.ismartshell.core.database.entities.QrScanEntity
import com.carlos.ismartshell.features.buyer.data.mappers.toDomain
import com.carlos.ismartshell.features.buyer.data.mappers.toEntity
import com.carlos.ismartshell.features.buyer.domain.entities.QrScan
import com.carlos.ismartshell.features.buyer.domain.repositories.QrScanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QrScanRepositoryImpl @Inject constructor(
    private val dao: QrScanDao
) : QrScanRepository {

    override suspend fun saveQrScan(rawValue: String, storeName: String?, storeId: Int?) {
        dao.insert(QrScanEntity(rawValue = rawValue, storeName = storeName, storeId = storeId))
    }

    override fun getAllScans(): Flow<List<QrScan>> =
        dao.getAllScans().map { list -> list.map { it.toDomain() } }

    override fun getRecentScans(limit: Int): Flow<List<QrScan>> =
        dao.getRecentScans(limit).map { list -> list.map { it.toDomain() } }

    override fun searchScans(query: String): Flow<List<QrScan>> =
        dao.searchScans(query).map { list -> list.map { it.toDomain() } }

    override suspend fun deleteQrScan(scan: QrScan) = dao.delete(scan.toEntity())

    override suspend fun clearHistory() = dao.clearAll()
}