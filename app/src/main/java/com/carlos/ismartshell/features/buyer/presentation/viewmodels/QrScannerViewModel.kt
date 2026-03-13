package com.carlos.ismartshell.features.buyer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.core.managers.VibrationManager
import com.carlos.ismartshell.features.buyer.domain.usecases.SaveQrScanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QrScannerUiState(
    val scannedCode: String? = null,
    val saved: Boolean = false,
    val paused: Boolean = false
)

@HiltViewModel
class QrScannerViewModel @Inject constructor(
    private val saveQrScanUseCase: SaveQrScanUseCase,
    private val vibrationManager: VibrationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(QrScannerUiState())
    val uiState = _uiState.asStateFlow()

    fun onQrDetected(code: String) {
        if (_uiState.value.paused) return
        _uiState.value = _uiState.value.copy(scannedCode = code, paused = true)
        vibrationManager.vibrateSingle()
    }

    fun saveCurrentScan(label: String) {
        val code = _uiState.value.scannedCode ?: return
        viewModelScope.launch {
            saveQrScanUseCase(code, label.ifBlank { code })
            _uiState.value = _uiState.value.copy(saved = true)
        }
    }

    fun resumeScanning() {
        _uiState.value = QrScannerUiState()
    }
}