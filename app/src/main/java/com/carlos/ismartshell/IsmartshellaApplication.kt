package com.carlos.ismartshell

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.carlos.ismartshell.core.workers.SyncOrdersWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class IsmartshellaApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    companion object {
        private const val TAG = "IsmartshellApp"
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "🚀 Aplicación iniciada")

        // Worker inmediato para prueba
        runTestWorker()

        // Worker periódico real
        scheduleSyncOrders()
    }

    private fun runTestWorker() {

        Log.e(TAG, "🧪 Ejecutando Worker de prueba")

        val testRequest = OneTimeWorkRequestBuilder<SyncOrdersWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueue(testRequest)

        Log.e(TAG, "✅ Worker de prueba encolado")
    }

    private fun scheduleSyncOrders() {

        Log.e(TAG, "📅 Programando sincronización periódica de pedidos")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncOrdersWorker>(
            15, TimeUnit.MINUTES // mínimo permitido por Android
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SyncOrdersWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        Log.d(TAG, "✅ Worker periódico programado")
    }
}