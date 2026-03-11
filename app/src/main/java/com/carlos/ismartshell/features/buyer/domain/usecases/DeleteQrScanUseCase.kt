package com.carlos.ismartshell.features.buyer.domain.usecases

import com.carlos.ismartshell.features.buyer.domain.entities.QrScan
import com.carlos.ismartshell.features.buyer.domain.repositories.QrScanRepository
import javax.inject.Inject

class DeleteQrScanUseCase @Inject constructor(
    private val repository: QrScanRepository
) {
    suspend operator fun invoke(scan: QrScan) = repository.deleteQrScan(scan)
}
