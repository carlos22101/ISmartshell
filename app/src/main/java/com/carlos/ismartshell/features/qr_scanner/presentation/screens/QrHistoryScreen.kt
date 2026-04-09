package com.carlos.ismartshell.features.qr_scanner.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlos.ismartshell.core.util.QrCodeGenerator
import com.carlos.ismartshell.features.buyer.domain.entities.Order
import com.carlos.ismartshell.features.buyer.presentation.viewmodels.HomeBuyerViewModel

private val BrandNavy   = Color(0xFF1E1B4B)
private val BrandOrange = Color(0xFFF97316)
private val WarmWhite   = Color(0xFFFFF9EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrHistoryScreen(viewModel: HomeBuyerViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedOrder    by remember { mutableStateOf<Order?>(null) }
    var selectedFilter   by remember { mutableStateOf("Todos") }

    LaunchedEffect(Unit) {
        while (true) {
            viewModel.loadMyOrders(silent = true)
            kotlinx.coroutines.delay(5000)
        }
    }

    val filteredOrders = remember(state.orders, selectedFilter) {
        when (selectedFilter) {
            "Pendientes"  -> state.orders.filter { it.status == "reserved" || it.status == "paid" }
            "Entregados"  -> state.orders.filter { it.status == "delivered" }
            "Cancelados"  -> state.orders.filter { it.status == "cancelled" }
            else          -> state.orders
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mis Pedidos", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Tu historial de reservas", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavy)
            )
        },
        containerColor = WarmWhite
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf("Todos", "Pendientes", "Entregados", "Cancelados")) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandOrange,
                            selectedLabelColor = Color.White,
                            containerColor = WarmWhite,
                            labelColor = BrandOrange),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true, selected = selectedFilter == filter,
                            selectedBorderColor = BrandOrange, borderColor = BrandOrange)
                    )
                }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandOrange)
                }
            } else if (filteredOrders.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes pedidos activos", color = Color(0xFF6B7280))
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredOrders, key = { it.id }) { order ->
                        OrderHistoryItem(order, onShowQr = { selectedOrder = order })
                    }
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
    val (borderColor, bgBadge, txtBadge, labelBadge) = when (order.status) {
        "delivered" -> listOf(Color(0xFF10B981), Color(0xFFD1FAE5), Color(0xFF065F46), "Entregado ✓")
        "ready"     -> listOf(Color(0xFF06B6D4), Color(0xFFCFFAFE), Color(0xFF155E75), "Listo para recoger")
        "cancelled" -> listOf(Color(0xFFEF4444), Color(0xFFFEE2E2), Color(0xFF991B1B), "Cancelado")
        else        -> listOf(Color(0xFFF59E0B), Color(0xFFFEF3C7), Color(0xFF92400E), "Pendiente")
    }

    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(14.dp)).background(Color.White)) {
        Box(Modifier.width(4.dp).fillMaxHeight().background(borderColor as Color))
        Column(Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 14.dp)) {
            Text("Pedido #${order.id.take(8)}", fontWeight = FontWeight.Bold, color = Color(0xFF1E1B13))
            Spacer(Modifier.height(4.dp))
            Box(Modifier.clip(RoundedCornerShape(6.dp)).background(bgBadge as Color)
                .padding(horizontal = 8.dp, vertical = 2.dp)) {
                Text(labelBadge as String, fontSize = 11.sp, color = txtBadge as Color, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(6.dp))
            Text("$${order.total}", color = BrandOrange, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            if (order.status == "reserved" || order.status == "paid" || order.status == "ready") {
                Text("Recoger antes de: ${order.pickupDeadline ?: "N/A"}", fontSize = 12.sp, color = Color(0xFF6B7280))
            }
            order.createdAt?.let {
                Text("Creado: $it", fontSize = 11.sp, color = Color(0xFF9CA3AF))
            }
        }
        if (order.status != "delivered" && order.status != "cancelled") {
            IconButton(onClick = onShowQr, modifier = Modifier.align(Alignment.CenterVertically).padding(end = 4.dp)) {
                Box(Modifier.size(40.dp).clip(CircleShape).background(BrandOrange),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.QrCode, "Ver QR", tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}

@Composable
private fun QrCodeDialog(order: Order, onDismiss: () -> Unit) {
    val qrBitmap = remember(order.qrCode) {
        order.qrCode?.let { QrCodeGenerator.generateQrCode(it, 400) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text("Tu Código QR", textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold, color = BrandOrange, fontSize = 20.sp)
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                if (qrBitmap != null) {
                    Box(Modifier.size(200.dp).clip(RoundedCornerShape(16.dp))
                        .border(2.dp, BrandOrange, RoundedCornerShape(16.dp)).background(Color.White),
                        contentAlignment = Alignment.Center) {
                        Image(bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "QR", modifier = Modifier.size(180.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                    Box(Modifier.clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEDE9FE)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text(order.id.take(12), fontSize = 13.sp,
                            color = Color(0xFF5B21B6), fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Muestra este código al vendedor",
                        style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                } else {
                    Text("No se pudo generar el QR", color = Color(0xFF6B7280))
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)) {
                Text("Cerrar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}