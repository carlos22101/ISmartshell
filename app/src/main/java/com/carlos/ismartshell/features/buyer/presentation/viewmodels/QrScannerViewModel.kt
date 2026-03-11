package com.carlos.ismartshell.features.buyer.presentation.viewmodels

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.carlos.ismartshell.core.managers.QrScannerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class QrScannerViewModel @Inject constructor(
    private val qrScannerManager: QrScannerManager
) : ViewModel() {

    private val _scannedValue = MutableStateFlow<String?>(null)
    val scannedValue: StateFlow<String?> = _scannedValue

    fun startScanning(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        qrScannerManager.startScanning(lifecycleOwner, previewView) { qrValue ->
            if (_scannedValue.value == null) {
                _scannedValue.value = qrValue
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