package com.carlos.ismartshell.features.buyer.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlos.ismartshell.features.buyer.domain.entities.BuyerStore
import com.carlos.ismartshell.features.buyer.domain.entities.Product
import com.carlos.ismartshell.features.buyer.presentation.viewmodels.HomeBuyerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBuyerScreen(
    onNavigateToMap: (businessId: String) -> Unit,
    viewModel: HomeBuyerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showOrderDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var orderType by remember { mutableStateOf("online") }

    LaunchedEffect(state.orderSuccess) {
        if (state.orderSuccess != null) showOrderDialog = false
    }

    if (state.selectedStore == null) {
        // Lista de tiendas
        StoreListScreen(
            stores    = state.stores,
            isLoading = state.isLoading,
            onRefresh  = { viewModel.loadStores() },
            onSelectStore = { viewModel.selectStore(it.id) },
            onMapClick = onNavigateToMap
        )
    } else {
        // Detalle de tienda
        StoreDetailScreen(
            store    = state.selectedStore!!,
            products = state.products,
            onBack   = { viewModel.clearSelectedStore() },
            onMapClick = { onNavigateToMap(state.selectedStore!!.id) },
            onOrderProduct = { product ->
                selectedProduct = product
                showOrderDialog = true
            }
        )
    }

    state.error?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text  = { Text(msg) },
            confirmButton = { TextButton(onClick = { viewModel.clearError() }) { Text("OK") } }
        )
    }

    state.orderSuccess?.let { order ->
        AlertDialog(
            onDismissRequest = { viewModel.clearOrderSuccess() },
            title = { Text("¡Pedido creado!") },
            text  = {
                Column {
                    Text("Total: $${order.total}")
                    order.qrCode?.let { Text("Código QR: $it") }
                    if (order.type == "reserved") Text("Recoge antes de: ${order.pickupDeadline}")
                }
            },
            confirmButton = { TextButton(onClick = { viewModel.clearOrderSuccess() }) { Text("OK") } }
        )
    }

    if (showOrderDialog && selectedProduct != null) {
        AlertDialog(
            onDismissRequest = { showOrderDialog = false },
            title = { Text("Ordenar: ${selectedProduct!!.name}") },
            text  = {
                Column {
                    Text("Precio: $${selectedProduct!!.price}")
                    Spacer(Modifier.height(8.dp))
                    Text("Tipo de orden:")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = orderType == "online",   onClick = { orderType = "online"   }, label = { Text("En línea") })
                        FilterChip(selected = orderType == "reserved", onClick = { orderType = "reserved" }, label = { Text("Apartar") })
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val store = state.selectedStore ?: return@Button
                    viewModel.createOrder(store.id, orderType, listOf(selectedProduct!!.id to 1))
                }) { Text("Confirmar") }
            },
            dismissButton = { TextButton(onClick = { showOrderDialog = false }) { Text("Cancelar") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoreListScreen(
    stores: List<BuyerStore>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onSelectStore: (BuyerStore) -> Unit,
    onMapClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tiendas cercanas") },
                actions = { IconButton(onClick = onRefresh) { Icon(Icons.Default.Refresh, "Actualizar") } }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(stores) { store ->
                    StoreCard(store, onSelectStore, onMapClick)
                }
            }
        }
    }
}

@Composable
private fun StoreCard(
    store: BuyerStore,
    onSelect: (BuyerStore) -> Unit,
    onMapClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onSelect(store) },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(store.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(store.type, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                if (store.description.isNotBlank())
                    Text(store.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
            }
            IconButton(onClick = { onMapClick(store.id) }) {
                Icon(Icons.Default.LocationOn, "Ver en mapa", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoreDetailScreen(
    store: BuyerStore,
    products: List<Product>,
    onBack: () -> Unit,
    onMapClick: () -> Unit,
    onOrderProduct: (Product) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(store.name) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } },
                actions = { IconButton(onClick = onMapClick) { Icon(Icons.Default.LocationOn, "Mapa") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Productos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            items(products) { product ->
                ProductCard(product, onOrderProduct)
            }
        }
    }
}

@Composable
private fun ProductCard(product: Product, onOrder: (Product) -> Unit) {
    Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("$${product.price}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                Text("Stock: ${product.stock}", style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = { onOrder(product) }, enabled = product.stock > 0) {
                Text("Ordenar")
            }
        }
    }
}