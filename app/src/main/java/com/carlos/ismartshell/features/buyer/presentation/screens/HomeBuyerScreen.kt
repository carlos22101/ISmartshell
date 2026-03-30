package com.carlos.ismartshell.features.buyer.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlos.ismartshell.core.util.QrCodeGenerator
import com.carlos.ismartshell.features.buyer.domain.entities.BuyerStore
import com.carlos.ismartshell.features.buyer.domain.entities.Product
import com.carlos.ismartshell.features.buyer.presentation.viewmodels.HomeBuyerViewModel
import com.carlos.ismartshell.features.maps.presentation.screens.NearbyStoresMapScreen
import com.carlos.ismartshell.features.seller.presentation.screens.STORE_CATEGORIES

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
    var quantity by remember { mutableIntStateOf(1) }
    
    // Estado de filtro
    var selectedCategory by remember { mutableStateOf("Todas") }
    val filteredStores = remember(state.stores, selectedCategory) {
        if (selectedCategory == "Todas") state.stores
        else state.stores.filter { it.type == selectedCategory }
    }

    // Estado para alternar entre lista y mapa general
    var isMapViewActive by remember { mutableStateOf(false) }

    LaunchedEffect(state.orderSuccess) {
        if (state.orderSuccess != null) showOrderDialog = false
    }

    if (state.selectedStore == null) {
        if (isMapViewActive) {
            NearbyStoresMapScreen(
                stores = filteredStores,
                userLocation = state.userLocation,
                onBack = { isMapViewActive = false },
                onSelectStore = { storeId ->
                    viewModel.selectStore(storeId)
                    isMapViewActive = false
                }
            )
        } else {
            StoreListScreen(
                stores = filteredStores,
                selectedCategory = selectedCategory,
                onCategorySelect = { selectedCategory = it },
                isLoading = state.isLoading,
                onRefresh = { viewModel.loadStores() },
                onSelectStore = { viewModel.selectStore(it.id) },
                onMapClick = onNavigateToMap,
                onOpenMapMode = { isMapViewActive = true }
            )
        }
    } else {
        StoreDetailScreen(
            store    = state.selectedStore!!,
            products = state.products,
            onBack   = { viewModel.clearSelectedStore() },
            onMapClick = { onNavigateToMap(state.selectedStore!!.id) },
            onOrderProduct = { product ->
                selectedProduct = product
                quantity = 1
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
            title = { Text("¡Pedido creado!", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            text  = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Total: $${order.total}", fontWeight = FontWeight.Bold)
                    
                    val qrBitmap = remember(order.qrCode) {
                        order.qrCode?.let { QrCodeGenerator.generateQrCode(it, 400) }
                    }

                    if (qrBitmap != null) {
                        Spacer(Modifier.height(16.dp))
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "Código QR del pedido",
                            modifier = Modifier.size(200.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Muestra este QR al vendedor",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    if (order.type == "reserved") {
                        Spacer(Modifier.height(8.dp))
                        Text("Recoge antes de:", style = MaterialTheme.typography.labelMedium)
                        Text(order.pickupDeadline ?: "N/A", color = MaterialTheme.colorScheme.primary)
                    }
                }
            },
            confirmButton = { 
                Button(
                    onClick = { viewModel.clearOrderSuccess() },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Entendido") }
            }
        )
    }

    if (showOrderDialog && selectedProduct != null) {
        AlertDialog(
            onDismissRequest = { showOrderDialog = false },
            title = { Text("Ordenar: ${selectedProduct!!.name}") },
            text  = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Precio unitario: $${selectedProduct!!.price}")
                    
                    // Selector de cantidad
                    Column {
                        Text("Cantidad:", style = MaterialTheme.typography.labelMedium)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                enabled = quantity > 1
                            ) {
                                Icon(Icons.Default.Remove, "Menos")
                            }
                            
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            
                            IconButton(
                                onClick = { if (quantity < (selectedProduct!!.stock ?: 0)) quantity++ },
                                enabled = quantity < (selectedProduct!!.stock ?: 0)
                            ) {
                                Icon(Icons.Default.Add, "Más")
                            }
                            
                            Spacer(Modifier.weight(1f))
                            Text(
                                "Total: $${selectedProduct!!.price * quantity}",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        if ((selectedProduct!!.stock ?: 0) <= 5) {
                            Text(
                                "¡Sólo quedan ${selectedProduct!!.stock} unidades!",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Text("Tipo de orden:")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = orderType == "online",   onClick = { orderType = "online"   }, label = { Text("En línea") })
                        FilterChip(selected = orderType == "reserved", onClick = { orderType = "reserved" }, label = { Text("Apartar") })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val store = state.selectedStore ?: return@Button
                        viewModel.createOrder(store.id, orderType, listOf(selectedProduct!!.id to quantity))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Confirmar ($${selectedProduct!!.price * quantity})") }
            },
            dismissButton = { TextButton(onClick = { showOrderDialog = false }) { Text("Cancelar") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoreListScreen(
    stores: List<BuyerStore>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onSelectStore: (BuyerStore) -> Unit,
    onMapClick: (String) -> Unit,
    onOpenMapMode: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tiendas cercanas") },
                actions = {
                    IconButton(onClick = onOpenMapMode) { Icon(Icons.Default.Map, "Ver Mapa") }
                    IconButton(onClick = onRefresh) { Icon(Icons.Default.Refresh, "Actualizar") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Fila de Categorías
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == "Todas",
                        onClick = { onCategorySelect("Todas") },
                        label = { Text("Todas") }
                    )
                }
                items(STORE_CATEGORIES) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onCategorySelect(category) },
                        label = { Text(category) }
                    )
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (stores.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay tiendas en esta categoría", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
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
                Text("Stock disponible: ${product.stock}", style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = { onOrder(product) }, enabled = (product.stock ?: 0) > 0) {
                Text("Ordenar")
            }
        }
    }
}
