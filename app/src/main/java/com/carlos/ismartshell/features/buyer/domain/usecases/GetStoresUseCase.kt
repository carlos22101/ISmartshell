package com.carlos.ismartshell.features.buyer.domain.usecases

import com.carlos.ismartshell.features.buyer.domain.entities.*
import com.carlos.ismartshell.features.buyer.domain.repositories.QrScanRepository
import com.carlos.ismartshell.features.buyer.domain.repositories.StoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStoresUseCase @Inject constructor(private val repo: StoreRepository) {
    suspend operator fun invoke(lat: Double, lng: Double, radiusKm: Double = 5.0): Result<List<BuyerStore>> =
        repo.getNearbyStores(lat, lng, radiusKm)
}

class SaveQrScanUseCase @Inject constructor(private val repo: QrScanRepository) {
    suspend operator fun invoke(code: String, label: String) = repo.save(code, label)
}

class GetQrHistoryUseCase @Inject constructor(private val repo: QrScanRepository) {
    operator fun invoke(): Flow<List<QrScan>> = repo.getHistory()
}

class DeleteQrScanUseCase @Inject constructor(private val repo: QrScanRepository) {
    suspend operator fun invoke(scan: QrScan) = repo.delete(scan)
}

class ClearQrHistoryUseCase @Inject constructor(private val repo: QrScanRepository) {
    suspend operator fun invoke() = repo.clearAll()
}