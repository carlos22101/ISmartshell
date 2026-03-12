package com.carlos.ismartshell.features.buyer.presentation.screens

import android.Manifest
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.carlos.ismartshell.features.buyer.presentation.viewmodels.QrScannerViewModel
import com.carlos.ismartshell.ui.theme.Primary
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    viewModel: QrScannerViewModel,
    onQrResult: (String) -> Unit,
    onBack: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val lifecycleOwner = LocalLifecycleOwner.current
    val scannedValue by viewModel.scannedValue.collectAsState()

    LaunchedEffect(scannedValue) {
        scannedValue?.let { onQrResult(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escanear QR", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "Historial")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = androidx.compose.ui.graphics.Color.White,
                    navigationIconContentColor = androidx.compose.ui.graphics.Color.White,
                    actionIconContentColor = androidx.compose.ui.graphics.Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                cameraPermission.status.isGranted -> {
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).also { previewView ->
                                viewModel.startScanning(lifecycleOwner, previewView)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    Surface(
                        modifier = Modifier
                            .size(250.dp)
                            .align(Alignment.Center),
                        color = androidx.compose.ui.graphics.Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(
                            3.dp, Primary
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                    ) {}
                }

                cameraPermission.status.shouldShowRationale -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.QrCodeScanner, null, tint = Primary, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("Necesitamos acceso a la cámara para escanear códigos QR.")
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { cameraPermission.launchPermissionRequest() }) {
                            Text("Conceder permiso")
                        }
                    }
                }

                else -> {
                    LaunchedEffect(Unit) { cameraPermission.launchPermissionRequest() }
                    CircularProgressIndicator(color = Primary)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopScanning() }
    }
}
