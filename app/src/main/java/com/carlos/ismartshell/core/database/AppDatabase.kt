package com.carlos.ismartshell.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.carlos.ismartshell.core.database.dao.QrScanDao
import com.carlos.ismartshell.core.database.entities.QrScanEntity

@Database(
    entities = [QrScanEntity::class],
    version  = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun qrScanDao(): QrScanDao
}
