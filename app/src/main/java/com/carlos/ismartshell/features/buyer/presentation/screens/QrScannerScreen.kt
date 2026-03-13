package com.carlos.ismartshell.features.buyer.presentation.screens

import android.Manifest
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlos.ismartshell.core.managers.QrScannerManager
import com.carlos.ismartshell.features.buyer.presentation.viewmodels.QrScannerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.EntryPointAccessors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QrScannerScreen(
    qrScannerManager: QrScannerManager,
    viewModel: QrScannerViewModel = hiltViewModel()
) {
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) cameraPermission.launchPermissionRequest()
    }

    Box(Modifier.fillMaxSize()) {
        if (cameraPermission.status.isGranted) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).also { previewView ->
                        qrScannerManager.startScanning(
                            lifecycleOwner = lifecycleOwner,
                            previewView    = previewView,
                            onQrDetected   = { viewModel.onQrDetected(it) }
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Overlay de visor
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    modifier = Modifier.size(200.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            }
        } else {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Se necesita permiso de cámara")
                Spacer(Modifier.height(8.dp))
                Button(onClick = { cameraPermission.launchPermissionRequest() }) {
                    Text("Permitir")
                }
            }
        }

        // Diálogo cuando se detecta un QR
        uiState.scannedCode?.let { code ->
            var label by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { viewModel.resumeScanning() },
                title = { Text("QR detectado") },
                text  = {
                    Column {
                        Text("Código: $code", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = label, onValueChange = { label = it },
                            label = { Text("Etiqueta (opcional)") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.saveCurrentScan(label)
                        viewModel.resumeScanning()
                    }) { Text("Guardar") }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.resumeScanning() }) { Text("Ignorar") }
                }
            )
        }
    }
}