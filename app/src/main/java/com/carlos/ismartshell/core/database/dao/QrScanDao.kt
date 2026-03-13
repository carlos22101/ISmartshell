package com.carlos.ismartshell.core.database.dao

import androidx.room.*
import com.carlos.ismartshell.core.database.entities.QrScanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QrScanDao {

    @Query("SELECT * FROM qr_scans ORDER BY scannedAt DESC")
    fun getAllScans(): Flow<List<QrScanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scan: QrScanEntity)

    @Delete
    suspend fun delete(scan: QrScanEntity)

    @Query("DELETE FROM qr_scans")
    suspend fun deleteAll()
}