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

    // Mensajes
    state.success?.let { msg ->
        LaunchedEffect(msg) { viewModel.clearMessages() }
        Snackbar(modifier = Modifier.padding(16.dp)) { Text(msg) }
    }

    if (state.selectedStore == null) {
        // Lista de tiendas del vendedor
        SellerStoreListScreen(
            stores    = state.stores,
            isLoading = state.isLoading,
            onCreateClick = { showCreateDialog = true },
            onSelectStore = { viewModel.loadStoreDetail(it.id) }
        )
    } else {
        // Detalle de tienda
        SellerStoreDetailScreen(
            store         = state.selectedStore!!,
            products      = state.products,
            orders        = state.orders,
            onBack        = { viewModel.clearSelectedStore() },
            onAddProduct  = { showProductDialog = true },
            onDeleteProduct = { viewModel.deleteProduct(it) },
            onScanQr      = { showQrScanner = true },
            onDeleteStore  = { viewModel.deleteStore(state.selectedStore!!.id) }
        )
    }

    state.error?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.clearMessages() },
            title = { Text("Error") }, text = { Text(msg) },
            confirmButton = { TextButton(onClick = { viewModel.clearMessages() }) { Text("OK") } }
        )
    }

    state.scannedOrder?.let { order ->
        AlertDialog(
            onDismissRequest = { viewModel.clearScannedOrder() },
            title = { Text("✅ Entrega confirmada") },
            text  = {
                Column {
                    Text("Orden: ${order.id.take(8)}...")
                    Text("Total: $${order.total}")
                    Text("Estado: ${order.status}")
                }
            },
            confirmButton = { Button(onClick = { viewModel.clearScannedOrder() }) { Text("OK") } }
        )
    }

    if (showCreateDialog) {
        CreateStoreDialog(
            onDismiss = { showCreateDialog = false },
            onCreate  = { name, desc, type ->
                viewModel.createStore(name, desc, type)
                showCreateDialog = false
            }
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
        } else if (stores.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Store, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(Modifier.height(8.dp))
                    Text("Aún no tienes tiendas", color = MaterialTheme.colorScheme.outline)
                }
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
                            if (store.description.isNotBlank()) Text(store.description)
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
    onBack: () -> Unit,
    onAddProduct: () -> Unit,
    onDeleteProduct: (String) -> Unit,
    onScanQr: () -> Unit,
    onDeleteStore: () -> Unit
) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Productos", "Órdenes")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(store.name) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } },
                actions = {
                    IconButton(onClick = onScanQr) { Icon(Icons.Default.QrCodeScanner, "Escanear QR") }
                    IconButton(onClick = onDeleteStore) { Icon(Icons.Default.Delete, "Eliminar tienda", tint = MaterialTheme.colorScheme.error) }
                }
            )
        },
        floatingActionButton = {
            if (tabIndex == 0) {
                FloatingActionButton(onClick = onAddProduct) { Icon(Icons.Default.Add, "Agregar producto") }
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
                0 -> ProductsTab(products, onDeleteProduct)
                1 -> OrdersTab(orders)
            }
        }
    }
}

@Composable
private fun ProductsTab(products: List<Product>, onDelete: (String) -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products, key = { it.id }) { product ->
            Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(product.name, fontWeight = FontWeight.Bold)
                        Text("$${product.price} · Stock: ${product.stock}", style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { onDelete(product.id) }) {
                        Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun OrdersTab(orders: List<Order>) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(orders, key = { it.id }) { order ->
            Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Orden ${order.id.take(8)}...", fontWeight = FontWeight.Bold)
                        AssistChip(onClick = {}, label = { Text(order.status) })
                    }
                    Text("Total: $${order.total}")
                    Text("Tipo: ${order.type}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun CreateStoreDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva tienda") },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Tipo (ej. abarrotes)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                onCreate(name, description, type)
            }) { Text("Crear") }
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
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Precio") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                    OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onCreate(name, description, price.toDoubleOrNull() ?: 0.0, stock.toIntOrNull() ?: 0)
            }) { Text("Agregar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
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
    var paused by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) cameraPermission.launchPermissionRequest()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Escanear QR de orden") },
        text  = {
            Box(Modifier.fillMaxWidth().height(280.dp)) {
                if (cameraPermission.status.isGranted) {
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).also { pv ->
                                qrScannerManager.startScanning(
                                    lifecycleOwner = lifecycleOwner,
                                    previewView    = pv,
                                    onQrDetected   = { code ->
                                        if (!paused) {
                                            paused = true
                                            onQrDetected(code)
                                        }
                                    }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Se necesita permiso de cámara")
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
