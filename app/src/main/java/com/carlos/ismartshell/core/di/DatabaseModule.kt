package com.carlos.ismartshell.core.di

import android.content.Context
import androidx.room.Room
import com.carlos.ismartshell.core.database.AppDatabase
import com.carlos.ismartshell.core.database.dao.QrScanDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "ismartshell.db"
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideQrScanDao(db: AppDatabase): QrScanDao = db.qrScanDao()
}
