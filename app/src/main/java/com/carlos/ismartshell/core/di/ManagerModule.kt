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

    @Provides
    @Singleton
    fun provideQrScannerManager(@ApplicationContext context: Context): QrScannerManager =
        QrScannerManager(context)

    @Provides
    @Singleton
    fun provideLocationManager(@ApplicationContext context: Context): LocationManager =
        LocationManager(context)

    @Provides
    @Singleton
    fun provideVibrationManager(@ApplicationContext context: Context): VibrationManager =
        VibrationManager(context)
}
