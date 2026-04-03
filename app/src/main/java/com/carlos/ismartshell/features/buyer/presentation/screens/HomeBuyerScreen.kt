package com.carlos.ismartshell.features.buyer.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.carlos.ismartshell.features.buyer.domain.entities.BuyerStore
import com.carlos.ismartshell.features.buyer.domain.entities.Product
import com.carlos.ismartshell.features.buyer.presentation.viewmodels.HomeBuyerViewModel
import com.carlos.ismartshell.features.maps.presentation.screens.NearbyStoresMapScreen
import com.carlos.ismartshell.features.seller.presentation.screens.STORE_CATEGORIES

private val BrandNavy   = Color(0xFF1E1B4B)
private val BrandOrange = Color(0xFFF97316)
private val WarmWhite   = Color(0xFFFFF9EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBuyerScreen(onNavigateToMap: (businessId: String) -> Unit, viewModel: HomeBuyerViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showOrderDialog  by remember { mutableStateOf(false) }
    var selectedProduct  by remember { mutableStateOf<Product?>(null) }
    var orderType        by remember { mutableStateOf("online") }
    var quantity         by remember { mutableIntStateOf(1) }
    var selectedCategory by remember { mutableStateOf("Todas") }
    val filteredStores = remember(state.stores, selectedCategory) {
        if (selectedCategory == "Todas") state.stores else state.stores.filter { it.type == selectedCategory }
    }
    var isMapViewActive by remember { mutableStateOf(false) }

    LaunchedEffect(state.orderSuccess) { if (state.orderSuccess != null) showOrderDialog = false }

    if (state.selectedStore == null) {
        if (isMapViewActive) {
            NearbyStoresMapScreen(stores = filteredStores, userLocation = state.userLocation,
                onBack = { isMapViewActive = false },
                onSelectStore = { storeId -> viewModel.selectStore(storeId); isMapViewActive = false })
        } else {
            StoreListScreen(stores = filteredStores, selectedCategory = selectedCategory,
                onCategorySelect = { selectedCategory = it }, isLoading = state.isLoading,
                onRefresh = { viewModel.loadStores() }, onSelectStore = { viewModel.selectStore(it.id) },
                onMapClick = onNavigateToMap, onOpenMapMode = { isMapViewActive = true })
        }
    } else {
        StoreDetailScreen(store = state.selectedStore!!, products = state.products,
            onBack = { viewModel.clearSelectedStore() },
            onMapClick = { onNavigateToMap(state.selectedStore!!.id) },
            onOrderProduct = { product -> selectedProduct = product; quantity = 1; showOrderDialog = true })
    }

    state.error?.let { msg ->
        AlertDialog(onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") }, text = { Text(msg) },
            confirmButton = { TextButton(onClick = { viewModel.clearError() }) { Text("OK") } })
    }

    state.orderSuccess?.let { order ->
        AlertDialog(onDismissRequest = { viewModel.clearOrderSuccess() }, containerColor = Color.White,
            title = { Text("¡Pedido creado!", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold, color = BrandNavy) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Total: $${order.total}", fontWeight = FontWeight.Bold, color = BrandOrange)
                    val qrBitmap = remember(order.qrCode) { order.qrCode?.let { QrCodeGenerator.generateQrCode(it, 400) } }
                    if (qrBitmap != null) {
                        Spacer(Modifier.height(16.dp))
                        Image(bitmap = qrBitmap.asImageBitmap(), contentDescription = null, modifier = Modifier.size(200.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Muestra este QR al vendedor", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                    }
                    if (order.type == "reserved") {
                        Spacer(Modifier.height(8.dp))
                        Text("Recoge antes de:", style = MaterialTheme.typography.labelMedium)
                        Text(order.pickupDeadline ?: "N/A", color = BrandOrange)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.clearOrderSuccess() }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)) {
                    Text("Entendido", color = Color.White, fontWeight = FontWeight.Bold)
                }
            })
    }

    if (showOrderDialog && selectedProduct != null) {
        AlertDialog(onDismissRequest = { showOrderDialog = false }, containerColor = Color.White,
            title = { Text("Ordenar: ${selectedProduct!!.name}", fontWeight = FontWeight.Bold, color = BrandNavy) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Precio unitario: $${selectedProduct!!.price}", color = Color(0xFF6B7280))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Box(Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFF3F4F6)),
                            contentAlignment = Alignment.Center) {
                            IconButton(onClick = { if (quantity > 1) quantity-- }, enabled = quantity > 1,
                                modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Remove, null, tint = Color(0xFF6B7280), modifier = Modifier.size(18.dp))
                            }
                        }
                        Text(quantity.toString(), style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold, color = BrandNavy, modifier = Modifier.padding(horizontal = 20.dp))
                        Box(Modifier.size(36.dp).clip(CircleShape).background(BrandOrange), contentAlignment = Alignment.Center) {
                            IconButton(onClick = { if (quantity < (selectedProduct!!.stock ?: 0)) quantity++ },
                                enabled = quantity < (selectedProduct!!.stock ?: 0), modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        Text("Total: $${selectedProduct!!.price * quantity}", color = BrandOrange,
                            fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                    if ((selectedProduct!!.stock ?: 0) <= 5) {
                        Text("¡Sólo quedan ${selectedProduct!!.stock} unidades!",
                            color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                    Text("Tipo de orden", color = BrandNavy, fontWeight = FontWeight.Medium)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = orderType == "online", onClick = { orderType = "online" },
                            label = { Text("En línea") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandOrange,
                                selectedLabelColor = Color.White, containerColor = Color.White, labelColor = BrandOrange),
                            border = FilterChipDefaults.filterChipBorder(enabled = true, selected = orderType == "online",
                                selectedBorderColor = BrandOrange, borderColor = BrandOrange))
                        FilterChip(selected = orderType == "reserved", onClick = { orderType = "reserved" },
                            label = { Text("Apartar") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandOrange,
                                selectedLabelColor = Color.White, containerColor = Color.White, labelColor = BrandOrange),
                            border = FilterChipDefaults.filterChipBorder(enabled = true, selected = orderType == "reserved",
                                selectedBorderColor = BrandOrange, borderColor = BrandOrange))
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val store = state.selectedStore ?: return@Button
                    viewModel.createOrder(store.id, orderType, listOf(selectedProduct!!.id to quantity))
                }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)) {
                    Text("Confirmar ($${selectedProduct!!.price * quantity})", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { showOrderDialog = false }) { Text("Cancelar", color = Color(0xFF6B7280)) } })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoreListScreen(
    stores: List<BuyerStore>, selectedCategory: String, onCategorySelect: (String) -> Unit,
    isLoading: Boolean, onRefresh: () -> Unit, onSelectStore: (BuyerStore) -> Unit,
    onMapClick: (String) -> Unit, onOpenMapMode: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tiendas cercanas", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onOpenMapMode) { Icon(Icons.Default.Map, null, tint = Color.White) }
                    IconButton(onClick = onRefresh) { Icon(Icons.Default.Refresh, null, tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavy)
            )
        },
        containerColor = WarmWhite
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyRow(Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(selected = selectedCategory == "Todas", onClick = { onCategorySelect("Todas") },
                        label = { Text("Todas") }, shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandOrange,
                            selectedLabelColor = Color.White, containerColor = WarmWhite, labelColor = BrandOrange),
                        border = FilterChipDefaults.filterChipBorder(enabled = true, selected = selectedCategory == "Todas",
                            selectedBorderColor = BrandOrange, borderColor = BrandOrange))
                }
                items(STORE_CATEGORIES) { category ->
                    FilterChip(selected = selectedCategory == category, onClick = { onCategorySelect(category) },
                        label = { Text(category) }, shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandOrange,
                            selectedLabelColor = Color.White, containerColor = WarmWhite, labelColor = BrandOrange),
                        border = FilterChipDefaults.filterChipBorder(enabled = true, selected = selectedCategory == category,
                            selectedBorderColor = BrandOrange, borderColor = BrandOrange))
                }
            }
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandOrange)
                }
            } else if (stores.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay tiendas en esta categoría", color = Color(0xFF6B7280))
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(stores) { store -> StoreCard(store, onSelectStore, onMapClick) }
                }
            }
        }
    }
}

@Composable
private fun StoreCard(store: BuyerStore, onSelect: (BuyerStore) -> Unit, onMapClick: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(14.dp))
        .background(Color.White).clickable { onSelect(store) }) {
        Box(Modifier.width(4.dp).fillMaxHeight().background(BrandOrange))
        Row(Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(store.name, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = Color(0xFF1E1B13))
                Spacer(Modifier.height(4.dp))
                Box(Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFFFFF3E0))
                    .padding(horizontal = 8.dp, vertical = 2.dp)) {
                    Text(store.type, fontSize = 11.sp, color = BrandOrange, fontWeight = FontWeight.Medium)
                }
                if (store.description.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(store.description, style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B7280), maxLines = 2)
                }
            }
            IconButton(onClick = { onMapClick(store.id) }) {
                Icon(Icons.Default.LocationOn, null, tint = BrandOrange)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoreDetailScreen(
    store: BuyerStore, products: List<Product>,
    onBack: () -> Unit, onMapClick: () -> Unit, onOrderProduct: (Product) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(store.name, color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                actions = { IconButton(onClick = onMapClick) { Icon(Icons.Default.LocationOn, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavy)
            )
        },
        containerColor = WarmWhite
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("Productos", style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold, color = Color(0xFF1E1B13)) }
            items(products) { product -> ProductCard(product, onOrderProduct) }
        }
    }
}

@Composable
private fun ProductCard(product: Product, onOrder: (Product) -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(14.dp)).background(Color.White)) {
        Box(Modifier.width(4.dp).fillMaxHeight().background(BrandOrange))
        Row(Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = Color(0xFF1E1B13))
                Text("$${product.price}", style = MaterialTheme.typography.bodyLarge,
                    color = BrandOrange, fontWeight = FontWeight.Bold)
                Text("Stock disponible: ${product.stock}",
                    style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
            }
            Button(onClick = { onOrder(product) }, enabled = (product.stock ?: 0) > 0,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange,
                    disabledContainerColor = Color(0xFFE5E7EB))) {
                Text("Ordenar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}