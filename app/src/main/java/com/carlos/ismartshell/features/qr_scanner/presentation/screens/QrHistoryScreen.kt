package com.carlos.ismartshell.features.qr_scanner.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlos.ismartshell.core.util.QrCodeGenerator
import com.carlos.ismartshell.features.buyer.domain.entities.Order
import com.carlos.ismartshell.features.buyer.presentation.viewmodels.HomeBuyerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrHistoryScreen(viewModel: HomeBuyerViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedOrder by remember { mutableStateOf<Order?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadMyOrders()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Pedidos") },
                actions = {
                    IconButton(onClick = { viewModel.loadMyOrders() }) {
                        Icon(Icons.Default.Refresh, "Actualizar")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.orders.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No tienes pedidos activos", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.orders) { order ->
                    OrderHistoryItem(order, onShowQr = { selectedOrder = order })
                }
            }
        }

        selectedOrder?.let { order ->
            QrCodeDialog(order = order, onDismiss = { selectedOrder = null })
        }
    }
}

@Composable
private fun OrderHistoryItem(order: Order, onShowQr: () -> Unit) {
    Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Pedido #${order.id.take(8)}", fontWeight = FontWeight.Bold)
                Text(
                    order.status.uppercase(),
                    color = when(order.status) {
                        "delivered" -> Color(0xFF4CAF50)
                        "ready"     -> Color(0xFF2196F3)
                        "cancelled" -> Color.Red
                        else        -> Color(0xFFFF9800)
                    },
                    style = MaterialTheme.typography.labelLarge
                )
                Text("Total: $${order.total}", style = MaterialTheme.typography.bodyMedium)
                if (order.status == "reserved" || order.status == "paid" || order.status == "ready") {
                    Text("Expira: ${order.pickupDeadline ?: "N/A"}", style = MaterialTheme.typography.labelSmall)
                }
            }
            if (order.status != "delivered" && order.status != "cancelled") {
                IconButton(onClick = onShowQr) {
                    Icon(Icons.Default.QrCode, "Ver QR", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun QrCodeDialog(order: Order, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tu Código QR", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                val qrBitmap = remember(order.qrCode) {
                    order.qrCode?.let { QrCodeGenerator.generateQrCode(it, 400) }
                }

                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR",
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Muestra este código al vendedor", style = MaterialTheme.typography.bodySmall)
                } else {
                    Text("No se pudo generar el QR")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}
