package com.carlos.ismartshell.core.database.dao

import androidx.room.*
import com.carlos.ismartshell.core.database.entities.QrScanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QrScanDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(scan: QrScanEntity)

    @Query("SELECT * FROM qr_scan_history ORDER BY scanned_at DESC")
    fun getAllScans(): Flow<List<QrScanEntity>>

    @Query("SELECT * FROM qr_scan_history ORDER BY scanned_at DESC LIMIT :limit")
    fun getRecentScans(limit: Int): Flow<List<QrScanEntity>>
    @Query("SELECT * FROM qr_scan_history WHERE raw_value LIKE '%' || :query || '%' ORDER BY scanned_at DESC")
    fun searchScans(query: String): Flow<List<QrScanEntity>>

    @Query("SELECT COUNT(*) FROM qr_scan_history")
    suspend fun countScans(): Int

    @Delete
    suspend fun delete(scan: QrScanEntity)

    @Query("DELETE FROM qr_scan_history")
    suspend fun clearAll()
}
