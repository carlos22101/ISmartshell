package com.carlos.ismartshell.features.seller.presentation.screens

import android.Manifest
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlos.ismartshell.core.managers.QrScannerManager
import com.carlos.ismartshell.features.buyer.domain.entities.Order
import com.carlos.ismartshell.features.buyer.domain.entities.Product
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore
import com.carlos.ismartshell.features.seller.presentation.viewmodels.CreateStoreViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.carlos.ismartshell.core.util.LatLng

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStoreScreen(
    qrScannerManager: QrScannerManager,
    viewModel: CreateStoreViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showProductDialog by remember { mutableStateOf(false) }
    var showQrScanner by remember { mutableStateOf(false) }
    
    var showLocationPicker by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    state.success?.let { msg ->
        LaunchedEffect(msg) { viewModel.clearMessages() }
        Snackbar(modifier = Modifier.padding(16.dp)) { Text(msg) }
    }

    if (state.selectedStore == null) {
        SellerStoreListScreen(
            stores    = state.stores,
            isLoading = state.isLoading,
            onCreateClick = { 
                selectedLocation = null
                showCreateDialog = true 
            },
            onSelectStore = { viewModel.loadStoreDetail(it.id) }
        )
    } else {
        SellerStoreDetailScreen(
            store         = state.selectedStore!!,
            products      = state.products,
            orders        = state.orders,
            scannedOrder  = state.scannedOrder,
            onBack        = { viewModel.clearSelectedStore() },
            onAddProduct  = { showProductDialog = true },
            onDeleteProduct = { viewModel.deleteProduct(it) },
            onUpdateStock = { id, stock -> viewModel.updateProductStock(id, stock) },
            onScanQr      = { showQrScanner = true },
            onDeleteStore  = { viewModel.deleteStore(state.selectedStore!!.id) },
            onClearScanned = { viewModel.clearScannedOrder() },
            onMarkAsReady = { viewModel.markOrderAsReady(it) }
        )
    }

    state.error?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.clearMessages() },
            title = { Text("Error") }, text = { Text(msg) },
            confirmButton = { TextButton(onClick = { viewModel.clearMessages() }) { Text("OK") } }
        )
    }

    if (showCreateDialog) {
        CreateStoreDialog(
            selectedLocation = selectedLocation,
            onPickLocation = { showLocationPicker = true },
            onDismiss = { showCreateDialog = false },
            onCreate  = { name, desc, type, loc ->
                viewModel.createStore(name, desc, type, loc.latitude, loc.longitude)
                showCreateDialog = false
            }
        )
    }

    if (showLocationPicker) {
        LocationPickerScreen(
            initialLocation = selectedLocation,
            userLocation = state.userLocation,
            onLocationSelected = {
                selectedLocation = it
                showLocationPicker = false
            },
            onDismiss = { showLocationPicker = false }
        )
    }

    if (showProductDialog && state.selectedStore != null) {
        CreateProductDialog(
            onDismiss = { showProductDialog = false },
            onCreate  = { name, desc, price, stock ->
                viewModel.createProduct(state.selectedStore!!.id, name, desc, price, stock)
                showProductDialog = false
            }
        )
    }

    if (showQrScanner) {
        QrScannerOverlay(
            qrScannerManager = qrScannerManager,
            onQrDetected     = { code ->
                viewModel.scanQrOrder(code)
                showQrScanner = false
            },
            onDismiss = { showQrScanner = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SellerStoreListScreen(
    stores: List<SellerStore>,
    isLoading: Boolean,
    onCreateClick: () -> Unit,
    onSelectStore: (SellerStore) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis tiendas") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick) {
                Icon(Icons.Default.Add, "Nueva tienda")
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(stores) { store ->
                    Card(
                        Modifier.fillMaxWidth().clickable { onSelectStore(store) },
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(store.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(store.type, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
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
    store: SellerStore,
    products: List<Product>,
    orders: List<Order>,
    scannedOrder: Order?,
    onBack: () -> Unit,
    onAddProduct: () -> Unit,
    onDeleteProduct: (String) -> Unit,
    onUpdateStock: (String, Int) -> Unit,
    onScanQr: () -> Unit,
    onDeleteStore: () -> Unit,
    onClearScanned: () -> Unit,
    onMarkAsReady: (String) -> Unit
) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Productos", "Órdenes")
    var showDeleteStoreConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(store.name) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } },
                actions = {
                    IconButton(onClick = onScanQr) { Icon(Icons.Default.QrCodeScanner, "Escanear QR") }
                    IconButton(onClick = { showDeleteStoreConfirm = true }) { 
                        Icon(Icons.Default.Delete, "Eliminar tienda", tint = MaterialTheme.colorScheme.error) 
                    }
                }
            )
        },
        floatingActionButton = {
            if (tabIndex == 0) {
                FloatingActionButton(onClick = onAddProduct) { Icon(Icons.Default.Add, "Producto") }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = tabIndex == index, onClick = { tabIndex = index }, text = { Text(title) })
                }
            }
            when (tabIndex) {
                0 -> ProductsTab(products, onDeleteProduct, onUpdateStock)
                1 -> OrdersTab(orders, onMarkAsReady)
            }
        }
    }

    if (showDeleteStoreConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteStoreConfirm = false },
            title = { Text("Eliminar Negocio") },
            text = { Text("¿Deseas eliminar '${store.name}' permanentemente? Se borrarán todos sus productos y pedidos.") },
            confirmButton = {
                Button(
                    onClick = { onDeleteStore(); showDeleteStoreConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { showDeleteStoreConfirm = false }) { Text("Cancelar") } }
        )
    }

    if (scannedOrder != null) {
        AlertDialog(
            onDismissRequest = onClearScanned,
            title = { Text("Orden Escaneada") },
            text = {
                Column {
                    Text("ID: ${scannedOrder.id.take(8)}", fontWeight = FontWeight.Bold)
                    Text("Total: $${scannedOrder.total}")
                    Text("Estado: ${scannedOrder.status.uppercase()}", 
                        color = when(scannedOrder.status) {
                            "delivered" -> Color(0xFF4CAF50)
                            "ready"     -> Color(0xFF2196F3)
                            else        -> Color(0xFFFF9800)
                        },
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        when(scannedOrder.status) {
                            "delivered" -> "Esta orden YA FUE ENTREGADA."
                            "ready"     -> "Orden LISTA. Se ha marcado como ENTREGADA ahora."
                            else        -> "ATENCIÓN: La orden aún no está marcada como 'READY'."
                        },
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onClearScanned) { Text("CERRAR") }
            }
        )
    }
}

@Composable
private fun ProductsTab(
    products: List<Product>, 
    onDelete: (String) -> Unit,
    onUpdateStock: (String, Int) -> Unit
) {
    var productToDelete by remember { mutableStateOf<Product?>(null) }
    var productToUpdateStock by remember { mutableStateOf<Product?>(null) }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products, key = { it.id }) { product ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(product.name, fontWeight = FontWeight.Bold)
                        Text("$${product.price}")
                        Text(
                            "Stock: ${product.stock}", 
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { productToUpdateStock = product }
                        )
                    }
                    Row {
                        IconButton(onClick = { productToUpdateStock = product }) {
                            Icon(Icons.Default.Edit, "Editar stock")
                        }
                        IconButton(onClick = { productToDelete = product }) {
                            Icon(Icons.Default.Delete, "Eliminar producto", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Eliminar Producto") },
            text = { Text("¿Deseas eliminar '${product.name}' permanentemente?") },
            confirmButton = {
                Button(
                    onClick = { onDelete(product.id); productToDelete = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { productToDelete = null }) { Text("Cancelar") } }
        )
    }

    productToUpdateStock?.let { product ->
        var newStock by remember { mutableStateOf(product.stock.toString()) }
        AlertDialog(
            onDismissRequest = { productToUpdateStock = null },
            title = { Text("Actualizar Stock") },
            text = {
                Column {
                    Text("Producto: ${product.name}")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newStock,
                        onValueChange = { newStock = it },
                        label = { Text("Nuevo Stock") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val stockInt = newStock.toIntOrNull() ?: product.stock
                        onUpdateStock(product.id, stockInt)
                        productToUpdateStock = null
                    }
                ) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { productToUpdateStock = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun OrdersTab(orders: List<Order>, onMarkAsReady: (String) -> Unit) {
    if (orders.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay órdenes registradas", color = MaterialTheme.colorScheme.outline)
        }
    } else {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(orders, key = { it.id }) { order ->
                Card(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Orden ${order.id.take(8)}", fontWeight = FontWeight.Bold)
                            Text(
                                order.status.uppercase(),
                                style = MaterialTheme.typography.labelLarge,
                                color = when(order.status) {
                                    "delivered" -> Color(0xFF4CAF50)
                                    "ready"     -> Color(0xFF2196F3)
                                    else        -> Color(0xFFFF9800)
                                }
                            )
                            Text("Total: $${order.total}", style = MaterialTheme.typography.bodySmall)
                        }
                        
                        if (order.status == "paid" || order.status == "reserved") {
                            Button(
                                onClick = { onMarkAsReady(order.id) },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("MARCAR LISTO", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

val STORE_CATEGORIES = listOf(
    "Abarrotes", "Restaurante", "Farmacia", "Ferretería", "Ropa", "Electrónica", "Salud", "Papelería", "Construcción", "Otros"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateStoreDialog(
    selectedLocation: LatLng?,
    onPickLocation: () -> Unit,
    onDismiss: () -> Unit,
    onCreate: (String, String, String, LatLng) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(STORE_CATEGORIES.first()) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva tienda") },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name, 
                    onValueChange = { name = it }, 
                    label = { Text("Nombre de la tienda") }, 
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description, 
                    onValueChange = { description = it }, 
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Giro / Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        STORE_CATEGORIES.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    type = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                Button(
                    onClick = onPickLocation, 
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedLocation != null) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(if (selectedLocation != null) Icons.Default.LocationOn else Icons.Default.AddLocation, null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (selectedLocation != null) "Ubicación seleccionada" else "Seleccionar ubicación")
                }
            }
        },
        confirmButton = {
            Button(
                enabled = name.isNotBlank() && selectedLocation != null,
                onClick = { onCreate(name, description, type, selectedLocation!!) }
            ) { Text("Crear Tienda") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun CreateProductDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, Double, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo producto") },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Precio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        },
        confirmButton = {
            Button(onClick = {
                onCreate(name, description, price.toDoubleOrNull() ?: 0.0, stock.toIntOrNull() ?: 0)
            }) { Text("Agregar") }
        },
        dismissButton = { TextButton(onClick = { onDismiss() }) { Text("Cancelar") } }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun QrScannerOverlay(
    qrScannerManager: QrScannerManager,
    onQrDetected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val lifecycleOwner = LocalLifecycleOwner.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Escanear QR") },
        text  = {
            Box(Modifier.fillMaxWidth().height(250.dp)) {
                if (cameraPermission.status.isGranted) {
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).also { pv ->
                                qrScannerManager.startScanning(lifecycleOwner, pv, onQrDetected = onQrDetected)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}
