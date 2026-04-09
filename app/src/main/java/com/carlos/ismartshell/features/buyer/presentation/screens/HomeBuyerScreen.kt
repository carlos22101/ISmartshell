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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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

    LaunchedEffect(Unit) {
        while (true) {
            viewModel.refreshAllData()
            kotlinx.coroutines.delay(5000)
        }
    }

    val filteredStores = remember(state.stores, state.selectedCategory) {
        if (state.selectedCategory == "Todas") state.stores
        else state.stores.filter { it.type == state.selectedCategory }
    }

    if (state.selectedStore == null) {
        if (state.isMapViewActive) {
            NearbyStoresMapScreen(
                stores        = filteredStores,
                userLocation  = state.userLocation,
                onBack        = { viewModel.setMapViewActive(false) },
                onSelectStore = { storeId -> viewModel.selectStore(storeId) }
            )
        } else {
            StoreListScreen(
                stores           = filteredStores,
                selectedCategory = state.selectedCategory,
                onCategorySelect = { viewModel.setSelectedCategory(it) },
                isLoading        = state.isLoading,
                onRefresh        = { viewModel.loadStores() },
                onSelectStore    = { viewModel.selectStore(it.id) },
                onMapClick       = onNavigateToMap,
                onOpenMapMode    = { viewModel.setMapViewActive(true) }
            )
        }
    } else {
        StoreDetailScreen(
            store          = state.selectedStore!!,
            products       = state.products,
            onBack         = { viewModel.clearSelectedStore() },
            onMapClick     = { onNavigateToMap(state.selectedStore!!.id) },
            onOrderProduct = { product -> viewModel.onSelectProduct(product) }
        )
    }

    state.error?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            title = { Text("Error", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) },
            text  = { Text(msg, color = MaterialTheme.colorScheme.onSurface) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("OK", color = BrandOrange, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    state.orderSuccess?.let { order ->
        AlertDialog(
            onDismissRequest = { viewModel.clearOrderSuccess() },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            title = {
                Text("¡Pedido creado!", textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = BrandNavy)
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Total: $${order.total}", fontWeight = FontWeight.Bold,
                        fontSize = 20.sp, color = BrandOrange)
                    val qrBitmap = remember(order.qrCode) {
                        order.qrCode?.let { QrCodeGenerator.generateQrCode(it, 400) }
                    }
                    if (qrBitmap != null) {
                        Spacer(Modifier.height(16.dp))
                        Box(Modifier.size(210.dp).clip(RoundedCornerShape(16.dp))
                            .border(2.dp, BrandOrange, RoundedCornerShape(16.dp))
                            .background(Color.White).padding(8.dp),
                            contentAlignment = Alignment.Center) {
                            Image(bitmap = qrBitmap.asImageBitmap(), contentDescription = null,
                                modifier = Modifier.size(180.dp))
                        }
                        Spacer(Modifier.height(10.dp))
                        Text("Muestra este QR al vendedor",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (order.type == "reserved") {
                        Spacer(Modifier.height(8.dp))
                        Text("Recoge antes de:", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(order.pickupDeadline ?: "N/A", color = BrandOrange, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.clearOrderSuccess() }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)) {
                    Text("Entendido", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (state.showOrderDialog && state.selectedProduct != null) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowOrderDialog(false) },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            title = {
                Text("Ordenar: ${state.selectedProduct!!.name}", fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Precio unitario: $${state.selectedProduct!!.price}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium)

                    // Stepper cantidad
                    Card(colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = RoundedCornerShape(14.dp)) {
                        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(36.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                                contentAlignment = Alignment.Center) {
                                IconButton(onClick = { if (state.quantity > 1) viewModel.setQuantity(state.quantity - 1) },
                                    enabled = state.quantity > 1, modifier = Modifier.size(36.dp)) {
                                    Icon(Icons.Default.Remove, null,
                                        tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp))
                                }
                            }
                            Text(state.quantity.toString(), style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Box(Modifier.size(36.dp).clip(CircleShape).background(BrandOrange),
                                contentAlignment = Alignment.Center) {
                                IconButton(
                                    onClick = { if (state.quantity < (state.selectedProduct!!.stock ?: 0)) viewModel.setQuantity(state.quantity + 1) },
                                    enabled = state.quantity < (state.selectedProduct!!.stock ?: 0),
                                    modifier = Modifier.size(36.dp)) {
                                    Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("Total", color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium)
                        Text("$${state.selectedProduct!!.price * state.quantity}",
                            color = BrandOrange, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }

                    if ((state.selectedProduct!!.stock ?: 0) <= 5) {
                        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null,
                                tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("¡Sólo quedan ${state.selectedProduct!!.stock} unidades!",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Text("Tipo de orden", color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = state.orderType == "online", onClick = { viewModel.setOrderType("online") },
                            label = { Text("En línea") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandOrange,
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                labelColor = MaterialTheme.colorScheme.onSurface),
                            border = FilterChipDefaults.filterChipBorder(enabled = true, selected = state.orderType == "online",
                                selectedBorderColor = BrandOrange, borderColor = MaterialTheme.colorScheme.outlineVariant))
                        FilterChip(selected = state.orderType == "reserved", onClick = { viewModel.setOrderType("reserved") },
                            label = { Text("Apartar") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandOrange,
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                labelColor = MaterialTheme.colorScheme.onSurface),
                            border = FilterChipDefaults.filterChipBorder(enabled = true, selected = state.orderType == "reserved",
                                selectedBorderColor = BrandOrange, borderColor = MaterialTheme.colorScheme.outlineVariant))
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val store = state.selectedStore ?: return@Button
                    viewModel.createOrder(store.id, state.orderType, listOf(state.selectedProduct!!.id to state.quantity))
                }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)) {
                    Text("Confirmar ($${state.selectedProduct!!.price * state.quantity})",
                        color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowOrderDialog(false) }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
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
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavy)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyRow(Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(selected = selectedCategory == "Todas", onClick = { onCategorySelect("Todas") },
                        label = { Text("Todas") }, shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandOrange, selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            labelColor = BrandOrange),
                        border = FilterChipDefaults.filterChipBorder(enabled = true,
                            selected = selectedCategory == "Todas",
                            selectedBorderColor = BrandOrange, borderColor = BrandOrange))
                }
                items(STORE_CATEGORIES) { category ->
                    FilterChip(selected = selectedCategory == category, onClick = { onCategorySelect(category) },
                        label = { Text(category) }, shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandOrange, selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            labelColor = BrandOrange),
                        border = FilterChipDefaults.filterChipBorder(enabled = true,
                            selected = selectedCategory == category,
                            selectedBorderColor = BrandOrange, borderColor = BrandOrange))
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandOrange)
                }
            } else if (stores.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.StoreMallDirectory, null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No hay tiendas en esta categoría",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(stores, key = { it.id }) { store -> StoreCard(store, onSelectStore, onMapClick) }
                }
            }
        }
    }
}

@Composable
private fun StoreCard(store: BuyerStore, onSelect: (BuyerStore) -> Unit, onMapClick: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onSelect(store) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            // Franja naranja izquierda
            Box(Modifier.width(5.dp).fillMaxHeight().background(
                Brush.verticalGradient(listOf(BrandOrange, BrandOrange.copy(alpha = 0.6f)))))
            Row(Modifier.weight(1f).padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(store.name, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(5.dp))
                    Surface(shape = RoundedCornerShape(6.dp), color = BrandOrange.copy(alpha = 0.12f)) {
                        Text(store.type, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 11.sp, color = BrandOrange, fontWeight = FontWeight.SemiBold)
                    }
                    if (store.description.isNotBlank()) {
                        Spacer(Modifier.height(5.dp))
                        Text(store.description, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                    }
                }
                IconButton(onClick = { onMapClick(store.id) }) {
                    Icon(Icons.Default.LocationOn, null, tint = BrandOrange)
                }
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
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                },
                actions = {
                    IconButton(onClick = onMapClick) { Icon(Icons.Default.LocationOn, null, tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandNavy)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Productos", style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.width(8.dp))
                    if (products.isNotEmpty()) {
                        Surface(shape = CircleShape, color = BrandOrange) {
                            Text("${products.size}", modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            if (products.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ShoppingBag, null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(56.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Sin productos disponibles",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            } else {
                items(products, key = { it.id }) { product -> ProductCard(product, onOrderProduct) }
            }
        }
    }
}

@Composable
private fun ProductCard(product: Product, onOrder: (Product) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.width(5.dp).fillMaxHeight().background(
                Brush.verticalGradient(listOf(BrandOrange, BrandOrange.copy(alpha = 0.6f)))))
            Row(Modifier.weight(1f).padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(product.name, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(3.dp))
                    Text("$${product.price}", style = MaterialTheme.typography.bodyLarge,
                        color = BrandOrange, fontWeight = FontWeight.Bold)
                    Text("Stock: ${product.stock}", style = MaterialTheme.typography.bodySmall,
                        color = if ((product.stock ?: 0) <= 5) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Button(onClick = { onOrder(product) }, enabled = (product.stock ?: 0) > 0,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) {
                    Text("Ordenar", color = Color.White, fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}