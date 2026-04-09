package com.carlos.ismartshell.features.seller.presentation.screens

import android.Manifest
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlos.ismartshell.core.managers.QrScannerManager
import com.carlos.ismartshell.core.util.LatLng
import com.carlos.ismartshell.features.buyer.domain.entities.Order
import com.carlos.ismartshell.features.buyer.domain.entities.Product
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore
import com.carlos.ismartshell.features.seller.presentation.viewmodels.CreateStoreViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

private val BrandNavy   = Color(0xFF1E1B4B)
private val BrandOrange = Color(0xFFF97316)
private val BrandPurple = Color(0xFF8B5CF6)
private val WarmWhite   = Color(0xFFFFF9EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStoreScreen(qrScannerManager: QrScannerManager, viewModel: CreateStoreViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        while (true) {
            viewModel.refreshSellerData()
            kotlinx.coroutines.delay(5000)
        }
    }

    state.success?.let { msg ->
        LaunchedEffect(msg) { viewModel.clearMessages() }
        Snackbar(modifier = Modifier.padding(16.dp)) { Text(msg) }
    }

    if (state.selectedStore == null) {
        SellerStoreListScreen(state.stores, state.isLoading,
            onCreateClick = { viewModel.setShowCreateDialog(true) },
            onSelectStore = { viewModel.loadStoreDetail(it.id) })
    } else {
        SellerStoreDetailScreen(
            store           = state.selectedStore!!,
            products        = state.products,
            orders          = state.orders,
            scannedOrder    = state.scannedOrder,
            onBack          = { viewModel.clearSelectedStore() },
            onAddProduct    = { viewModel.setShowProductDialog(true) },
            onDeleteProduct = { viewModel.deleteProduct(it) },
            onUpdateStock   = { id, s -> viewModel.updateProductStock(id, s) },
            onScanQr        = { viewModel.setShowQrScanner(true) },
            onDeleteStore   = { viewModel.deleteStore(state.selectedStore!!.id) },
            onClearScanned  = { viewModel.clearScannedOrder() },
            onMarkAsReady   = { viewModel.markOrderAsReady(it) }
        )
    }

    state.error?.let { msg ->
        AlertDialog(onDismissRequest = { viewModel.clearMessages() },
            title = { Text("Error") }, text = { Text(msg) },
            confirmButton = { TextButton(onClick = { viewModel.clearMessages() }) { Text("OK") } })
    }

    if (state.showCreateDialog) {
        CreateStoreDialog(
            selectedLocation = state.selectedLocation,
            onPickLocation   = { viewModel.setShowLocationPicker(true) },
            onDismiss        = { viewModel.setShowCreateDialog(false) },
            onCreate         = { name, desc, type, loc ->
                viewModel.createStore(name, desc, type, loc.latitude, loc.longitude)
            }
        )
    }

    if (state.showLocationPicker) {
        LocationPickerScreen(
            initialLocation    = state.selectedLocation,
            userLocation       = state.userLocation,
            onLocationSelected = { viewModel.setSelectedLocation(it) },
            onDismiss          = { viewModel.setShowLocationPicker(false) }
        )
    }

    if (state.showProductDialog && state.selectedStore != null) {
        CreateProductDialog(
            onDismiss = { viewModel.setShowProductDialog(false) },
            onCreate  = { name, desc, price, stock ->
                viewModel.createProduct(state.selectedStore!!.id, name, desc, price, stock)
            }
        )
    }

    if (state.showQrScanner) {
        QrScannerOverlay(
            qrScannerManager = qrScannerManager,
            onQrDetected     = { code -> viewModel.scanQrOrder(code) },
            onDismiss        = { viewModel.setShowQrScanner(false) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SellerStoreListScreen(
    stores: List<SellerStore>, isLoading: Boolean,
    onCreateClick: () -> Unit, onSelectStore: (SellerStore) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis Tiendas", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavy))
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick, containerColor = BrandOrange) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
        },
        containerColor = WarmWhite
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandOrange)
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(stores) { store ->
                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(14.dp))
                        .background(Color.White).clickable { onSelectStore(store) }) {
                        Box(Modifier.width(4.dp).fillMaxHeight().background(BrandPurple))
                        Row(Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(store.name, fontWeight = FontWeight.Bold, color = Color(0xFF1E1B13))
                                Spacer(Modifier.height(4.dp))
                                Box(Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFFEDE9FE))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)) {
                                    Text(store.type, fontSize = 11.sp, color = BrandPurple, fontWeight = FontWeight.Medium)
                                }
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = BrandPurple)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SellerStoreDetailScreen(
    store: SellerStore, products: List<Product>, orders: List<Order>, scannedOrder: Order?,
    onBack: () -> Unit, onAddProduct: () -> Unit, onDeleteProduct: (String) -> Unit,
    onUpdateStock: (String, Int) -> Unit, onScanQr: () -> Unit, onDeleteStore: () -> Unit,
    onClearScanned: () -> Unit, onMarkAsReady: (String) -> Unit
) {
    var tabIndex          by remember { mutableIntStateOf(0) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(store.name, color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                actions = {
                    IconButton(onClick = onScanQr) { Icon(Icons.Default.QrCodeScanner, null, tint = Color.White) }
                    IconButton(onClick = { showDeleteConfirm = true }) { Icon(Icons.Default.Delete, null, tint = Color(0xFFFFB4AB)) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavy))
        },
        floatingActionButton = {
            if (tabIndex == 0) FloatingActionButton(onClick = onAddProduct, containerColor = BrandOrange) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
        },
        containerColor = WarmWhite
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Productos", products.size.toString(), BrandOrange, Modifier.weight(1f))
                StatCard("Pedidos", orders.size.toString(), BrandPurple, Modifier.weight(1f))
                StatCard("Pendientes", orders.count { it.status == "paid" || it.status == "reserved" }.toString(),
                    Color(0xFFF59E0B), Modifier.weight(1f))
            }
            TabRow(selectedTabIndex = tabIndex, containerColor = Color.White, contentColor = BrandOrange) {
                listOf("Productos", "Órdenes").forEachIndexed { index, title ->
                    Tab(selected = tabIndex == index, onClick = { tabIndex = index }, text = {
                        Text(title,
                            color = if (tabIndex == index) BrandOrange else Color(0xFF6B7280),
                            fontWeight = if (tabIndex == index) FontWeight.Bold else FontWeight.Normal)
                    })
                }
            }
            when (tabIndex) {
                0 -> ProductsTab(products, onDeleteProduct, onUpdateStock)
                1 -> OrdersTab(orders, onMarkAsReady)
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Eliminar Negocio", fontWeight = FontWeight.Bold) },
            text = { Text("¿Deseas eliminar '${store.name}' permanentemente?") },
            confirmButton = {
                Button(onClick = { onDeleteStore(); showDeleteConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A)),
                    shape = RoundedCornerShape(50)) { Text("Eliminar", color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancelar", color = Color(0xFF6B7280)) } })
    }

    if (scannedOrder != null) {
        AlertDialog(onDismissRequest = onClearScanned,
            title = { Text("Orden Escaneada", fontWeight = FontWeight.Bold, color = BrandNavy) },
            text = {
                Column {
                    Text("ID: ${scannedOrder.id.take(8)}", fontWeight = FontWeight.Bold)
                    Text("Total: $${scannedOrder.total}")
                    Text(scannedOrder.status.uppercase(),
                        color = when(scannedOrder.status) {
                            "delivered" -> Color(0xFF4CAF50)
                            "ready"     -> Color(0xFF2196F3)
                            "cancelled" -> Color(0xFFEF4444)
                            else        -> BrandOrange
                        },
                        fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(8.dp))
                    Text(when(scannedOrder.status) {
                        "delivered" -> "Esta orden YA FUE ENTREGADA."
                        "ready"     -> "Orden LISTA. Se ha marcado como ENTREGADA ahora."
                        "cancelled" -> "Esta orden fue CANCELADA por tiempo o usuario."
                        else        -> "ATENCIÓN: La orden aún no está marcada como 'READY'."
                    }, color = Color(0xFF6B7280))
                }
            },
            confirmButton = { TextButton(onClick = onClearScanned) { Text("CERRAR", color = BrandOrange, fontWeight = FontWeight.Bold) } })
    }
}

@Composable
private fun StatCard(label: String, value: String, color: Color, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(12.dp)).background(Color.White)
        .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp)).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 11.sp, color = Color(0xFF6B7280))
    }
}

@Composable
private fun ProductsTab(products: List<Product>, onDelete: (String) -> Unit, onUpdateStock: (String, Int) -> Unit) {
    var productToDelete      by remember { mutableStateOf<Product?>(null) }
    var productToUpdateStock by remember { mutableStateOf<Product?>(null) }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(products, key = { it.id }) { product ->
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(14.dp)).background(Color.White)) {
                Box(Modifier.width(4.dp).fillMaxHeight().background(BrandOrange))
                Row(Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(product.name, fontWeight = FontWeight.Bold, color = Color(0xFF1E1B13))
                        Text("$${product.price}", color = BrandOrange, fontWeight = FontWeight.Bold)
                        Text("Stock: ${product.stock}", style = MaterialTheme.typography.bodySmall,
                            color = if ((product.stock ?: 0) <= 5) Color(0xFFF59E0B) else Color(0xFF6B7280))
                    }
                    IconButton(onClick = { if ((product.stock ?: 0) > 0) onUpdateStock(product.id, (product.stock ?: 0) - 1) }) {
                        Icon(Icons.Default.Remove, null, tint = BrandOrange, modifier = Modifier.size(18.dp))
                    }
                    Text("${product.stock}", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterVertically))
                    IconButton(onClick = { onUpdateStock(product.id, (product.stock ?: 0) + 1) }) {
                        Icon(Icons.Default.Add, null, tint = BrandOrange, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = { productToDelete = product }) {
                        Icon(Icons.Default.Delete, null, tint = Color(0xFFBA1A1A), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }

    productToDelete?.let { p ->
        AlertDialog(onDismissRequest = { productToDelete = null },
            title = { Text("Eliminar Producto", fontWeight = FontWeight.Bold) },
            text = { Text("¿Deseas eliminar '${p.name}'?") },
            confirmButton = {
                Button(onClick = { onDelete(p.id); productToDelete = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A)),
                    shape = RoundedCornerShape(50)) { Text("Eliminar", color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { productToDelete = null }) { Text("Cancelar", color = Color(0xFF6B7280)) } })
    }

    productToUpdateStock?.let { p ->
        var newStock by remember { mutableStateOf(p.stock.toString()) }
        AlertDialog(onDismissRequest = { productToUpdateStock = null },
            title = { Text("Actualizar Stock", fontWeight = FontWeight.Bold, color = BrandNavy) },
            text = {
                Column {
                    Text("Producto: ${p.name}", color = Color(0xFF6B7280))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = newStock, onValueChange = { newStock = it },
                        label = { Text("Nuevo Stock") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true, shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandOrange, unfocusedBorderColor = Color(0xFFE5E7EB)))
                }
            },
            confirmButton = {
                Button(onClick = { onUpdateStock(p.id, newStock.toIntOrNull() ?: p.stock ?: 0); productToUpdateStock = null },
                    shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)) {
                    Text("Guardar", color = Color.White)
                }
            },
            dismissButton = { TextButton(onClick = { productToUpdateStock = null }) { Text("Cancelar", color = Color(0xFF6B7280)) } })
    }
}

@Composable
private fun OrdersTab(orders: List<Order>, onMarkAsReady: (String) -> Unit) {
    if (orders.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay órdenes registradas", color = Color(0xFF6B7280))
        }
    } else {
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(orders, key = { it.id }) { order ->
                val accentColor = when(order.status) {
                    "delivered" -> Color(0xFF10B981)
                    "ready"     -> Color(0xFF06B6D4)
                    "cancelled" -> Color(0xFFEF4444)
                    else        -> Color(0xFFF59E0B)
                }
                val (bgBadge, txtBadge, labelBadge) = when(order.status) {
                    "delivered" -> Triple(Color(0xFFD1FAE5), Color(0xFF065F46), "Entregado")
                    "ready"     -> Triple(Color(0xFFCFFAFE), Color(0xFF155E75), "Listo")
                    "cancelled" -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), "Cancelado")
                    else        -> Triple(Color(0xFFFEF3C7), Color(0xFF92400E), "Pendiente")
                }
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(14.dp)).background(Color.White)) {
                    Box(Modifier.width(4.dp).fillMaxHeight().background(accentColor))
                    Row(Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text("Orden ${order.id.take(8)}", fontWeight = FontWeight.Bold, color = Color(0xFF1E1B13))
                            Spacer(Modifier.height(4.dp))
                            Box(Modifier.clip(RoundedCornerShape(6.dp)).background(bgBadge).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                Text(labelBadge, fontSize = 11.sp, color = txtBadge, fontWeight = FontWeight.Medium)
                            }
                            Text("Total: $${order.total}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                        }
                        if (order.status == "paid" || order.status == "reserved") {
                            Button(onClick = { onMarkAsReady(order.id) },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp), shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)) {
                                Text("LISTO", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

val STORE_CATEGORIES = listOf("Abarrotes", "Restaurante", "Farmacia", "Ferretería", "Ropa", "Electrónica", "Salud", "Papelería", "Construcción", "Otros")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateStoreDialog(
    selectedLocation: LatLng?, onPickLocation: () -> Unit,
    onDismiss: () -> Unit, onCreate: (String, String, String, LatLng) -> Unit
) {
    var name     by remember { mutableStateOf("") }
    var desc     by remember { mutableStateOf("") }
    var type     by remember { mutableStateOf(STORE_CATEGORIES.first()) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Nueva Tienda", fontWeight = FontWeight.Bold, color = BrandNavy) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DialogField("Nombre", name, { name = it })
                DialogField("Descripción", desc, { desc = it }, singleLine = false)
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = type, onValueChange = {}, readOnly = true,
                        label = { Text("Tipo de negocio") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandOrange, unfocusedBorderColor = Color(0xFFE5E7EB)))
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        STORE_CATEGORIES.forEach { cat ->
                            DropdownMenuItem(text = { Text(cat) }, onClick = { type = cat; expanded = false })
                        }
                    }
                }
                Button(onClick = onPickLocation, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = if (selectedLocation != null) Color(0xFF10B981) else BrandOrange)) {
                    Icon(if (selectedLocation != null) Icons.Default.LocationOn else Icons.Default.AddLocation, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text(if (selectedLocation != null) "Ubicación seleccionada ✓" else "Seleccionar ubicación",
                        color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(enabled = name.isNotBlank() && selectedLocation != null,
                onClick = { onCreate(name, desc, type, selectedLocation!!) },
                shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)) {
                Text("Crear Tienda", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = Color(0xFF6B7280)) } })
}

@Composable
private fun CreateProductDialog(onDismiss: () -> Unit, onCreate: (String, String, Double, Int) -> Unit) {
    var name  by remember { mutableStateOf("") }
    var desc  by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Nuevo Producto", fontWeight = FontWeight.Bold, color = BrandNavy) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LabeledField("Nombre", name, { name = it })
                LabeledField("Descripción (opcional)", desc, { desc = it })
                LabeledField("Precio", price, { price = it }, keyboardType = KeyboardType.Decimal)
                LabeledField("Stock inicial", stock, { stock = it }, keyboardType = KeyboardType.Number)
            }
        },
        confirmButton = {
            Button(onClick = { onCreate(name, desc, price.toDoubleOrNull() ?: 0.0, stock.toIntOrNull() ?: 0) },
                shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)) {
                Text("Agregar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = Color(0xFF6B7280)) } })
}

@Composable
private fun LabeledField(label: String, value: String, onValueChange: (String) -> Unit, keyboardType: KeyboardType = KeyboardType.Text) {
    Column {
        Text(label, fontSize = 12.sp, color = Color(0xFF6B7280))
        Spacer(Modifier.height(4.dp))
        DialogField(null, value, onValueChange, keyboardType = keyboardType)
    }
}

@Composable
private fun DialogField(label: String?, value: String, onValueChange: (String) -> Unit,
                        singleLine: Boolean = true, keyboardType: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(value = value, onValueChange = onValueChange,
        label = label?.let { { Text(it) } }, singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandOrange,
            unfocusedBorderColor = Color(0xFFE5E7EB), focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun QrScannerOverlay(qrScannerManager: QrScannerManager, onQrDetected: (String) -> Unit, onDismiss: () -> Unit) {
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val lifecycleOwner   = LocalLifecycleOwner.current

    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Escanear QR del cliente", fontWeight = FontWeight.Bold, color = BrandNavy) },
        text = {
            Box(Modifier.fillMaxWidth().height(250.dp)) {
                if (cameraPermission.status.isGranted) {
                    AndroidView(factory = { ctx ->
                        PreviewView(ctx).also { pv ->
                            qrScannerManager.startScanning(lifecycleOwner, pv, onQrDetected = onQrDetected)
                        }
                    }, modifier = Modifier.fillMaxSize())
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cerrar", color = BrandOrange, fontWeight = FontWeight.Bold) } })
}