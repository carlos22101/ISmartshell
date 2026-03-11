package com.carlos.ismartshell.features.buyer.domain.usecases

import com.carlos.ismartshell.features.buyer.domain.entities.QrScan
import com.carlos.ismartshell.features.buyer.domain.repositories.QrScanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQrHistoryUseCase @Inject constructor(
    private val repository: QrScanRepository
) {
    operator fun invoke(): Flow<List<QrScan>> = repository.getAllScans()
}
