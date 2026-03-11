package com.carlos.ismartshell.features.buyer.domain.usecases

import com.carlos.ismartshell.features.buyer.domain.repositories.QrScanRepository
import javax.inject.Inject

class ClearQrHistoryUseCase @Inject constructor(
    private val repository: QrScanRepository
) {
    suspend operator fun invoke() = repository.clearHistory()
}
