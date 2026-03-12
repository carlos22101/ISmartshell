package com.carlos.ismartshell.features.buyer.presentation.viewmodels

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.core.managers.QrScannerManager
import com.carlos.ismartshell.core.managers.VibrationManager
import com.carlos.ismartshell.features.buyer.domain.usecases.SaveQrScanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrScannerViewModel @Inject constructor(
    private val qrScannerManager: QrScannerManager,
    private val vibrationManager: VibrationManager,
    private val saveQrScanUseCase: SaveQrScanUseCase
) : ViewModel() {

    private val _scannedValue = MutableStateFlow<String?>(null)
    val scannedValue: StateFlow<String?> = _scannedValue.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    fun startScanning(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        qrScannerManager.startScanning(lifecycleOwner, previewView) { qrValue ->
            if (_scannedValue.value == null && !_isSaving.value) {
                onQrDetected(qrValue)
            }
        }
    }

    private fun onQrDetected(qrValue: String) {
        viewModelScope.launch {
            _isSaving.value = true
            
            // 1. Vibrar
            vibrationManager.qrSuccess()
            
            // 2. Guardar en local (Room/DataStore vía UseCase)
            try {
                saveQrScanUseCase(rawValue = qrValue)
                // 3. Emitir el valor para navegar o mostrar resultado
                _scannedValue.value = qrValue
            } catch (e: Exception) {
                // Opcional: manejar error de guardado
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun stopScanning() {
        qrScannerManager.stopScanning()
    }

    fun resetScan() {
        _scannedValue.value = null
    }

    override fun onCleared() {
        super.onCleared()
        qrScannerManager.stopScanning()
    }
}
