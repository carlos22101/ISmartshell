package com.carlos.ismartshell.features.buyer.data.repositories

import com.carlos.ismartshell.core.database.dao.QrScanDao
import com.carlos.ismartshell.core.database.entities.QrScanEntity
import com.carlos.ismartshell.features.buyer.data.mappers.QrScanMapper
import com.carlos.ismartshell.features.buyer.domain.entities.QrScan
import com.carlos.ismartshell.features.buyer.domain.repositories.QrScanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QrScanRepositoryImpl @Inject constructor(
    private val dao: QrScanDao
) : QrScanRepository {

    override fun getHistory(): Flow<List<QrScan>> =
        dao.getAllScans().map { list -> list.map { QrScanMapper.toDomain(it) } }

    override suspend fun save(code: String, label: String) {
        dao.insert(QrScanEntity(code = code, label = label))
    }

    override suspend fun delete(scan: QrScan) {
        dao.delete(QrScanMapper.toEntity(scan))
    }

    override suspend fun clearAll() {
        dao.deleteAll()
    }
}