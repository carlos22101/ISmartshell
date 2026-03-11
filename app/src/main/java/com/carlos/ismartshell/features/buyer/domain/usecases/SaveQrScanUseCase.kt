package com.carlos.ismartshell.features.buyer.domain.usecases

import com.carlos.ismartshell.features.buyer.domain.repositories.QrScanRepository
import javax.inject.Inject

class SaveQrScanUseCase @Inject constructor(
    private val repository: QrScanRepository
) {
    suspend operator fun invoke(
        rawValue: String,
        storeName: String? = null,
        storeId: Int? = null
    ) = repository.saveQrScan(rawValue, storeName, storeId)
}