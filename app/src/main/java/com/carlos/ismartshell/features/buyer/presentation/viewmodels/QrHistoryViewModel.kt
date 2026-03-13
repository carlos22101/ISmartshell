package com.carlos.ismartshell.features.buyer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.features.buyer.domain.entities.QrScan
import com.carlos.ismartshell.features.buyer.domain.usecases.ClearQrHistoryUseCase
import com.carlos.ismartshell.features.buyer.domain.usecases.DeleteQrScanUseCase
import com.carlos.ismartshell.features.buyer.domain.usecases.GetQrHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrHistoryViewModel @Inject constructor(
    getQrHistoryUseCase: GetQrHistoryUseCase,
    private val deleteQrScanUseCase: DeleteQrScanUseCase,
    private val clearQrHistoryUseCase: ClearQrHistoryUseCase
) : ViewModel() {

    val history = getQrHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun delete(scan: QrScan) {
        viewModelScope.launch { deleteQrScanUseCase(scan) }
    }

    fun clearAll() {
        viewModelScope.launch { clearQrHistoryUseCase() }
    }
}