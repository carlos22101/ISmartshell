package com.carlos.ismartshell.core.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.carlos.ismartshell.features.buyer.domain.repositories.StoreRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncOrdersWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val storeRepository: StoreRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            storeRepository.getMyOrders()
                .onSuccess { return Result.success() }
                .onFailure { return Result.retry() }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "sync_orders_worker"
    }
}