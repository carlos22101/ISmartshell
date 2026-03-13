package com.carlos.ismartshell.core.di

import android.content.Context
import com.carlos.ismartshell.core.managers.LocationManager
import com.carlos.ismartshell.core.managers.QrScannerManager
import com.carlos.ismartshell.core.managers.VibrationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {

    @Provides @Singleton
    fun provideLocationManager(@ApplicationContext ctx: Context): LocationManager =
        LocationManager(ctx)

    @Provides @Singleton
    fun provideQrScannerManager(@ApplicationContext ctx: Context): QrScannerManager =
        QrScannerManager(ctx)

    @Provides @Singleton
    fun provideVibrationManager(@ApplicationContext ctx: Context): VibrationManager =
        VibrationManager(ctx)
}