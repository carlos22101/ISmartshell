package com.carlos.ismartshell.features.seller.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.core.managers.LocationManager
import com.carlos.ismartshell.core.managers.VibrationManager
import com.carlos.ismartshell.core.util.LatLng
import com.carlos.ismartshell.features.seller.domain.usecases.*
import com.carlos.ismartshell.features.seller.presentation.screens.CreateStoreUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateStoreViewModel @Inject constructor(
    private val getStoresUseCase: GetSellerStoresUseCase,
    private val getDetailUseCase: GetSellerStoreDetailUseCase,
    private val createStoreUseCase: CreateStoreUseCase,
    private val updateStoreUseCase: UpdateStoreUseCase,
    private val deleteStoreUseCase: DeleteStoreUseCase,
    private val vibrationManager: VibrationManager,
    private val locationManager: LocationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateStoreUiState())
    val uiState = _uiState.asStateFlow()

    @Inject lateinit var sellerRepo: com.carlos.ismartshell.features.seller.domain.repositories.SellerRepository

    init {
        loadStores()
        fetchUserLocation()
    }

    fun refreshSellerData() {
        viewModelScope.launch {
            if (_uiState.value.selectedStore != null) {
                loadStoreDetail(_uiState.value.selectedStore!!.id, silent = true)
            } else {
                loadStores(silent = true)
            }
        }
    }

    private fun fetchUserLocation() {
        viewModelScope.launch {
            val location = try { locationManager.getLastLocation() } catch (e: Exception) { null }
            _uiState.update { it.copy(userLocation = location) }
        }
    }

    fun loadStores(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) _uiState.update { it.copy(isLoading = true) }
            getStoresUseCase()
                .onSuccess { stores -> _uiState.update { it.copy(stores = stores, isLoading = false) } }
                .onFailure { e -> if (!silent) _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    fun loadStoreDetail(id: String, silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) _uiState.update { it.copy(isLoading = true) }
            getDetailUseCase(id)
                .onSuccess { store ->
                    _uiState.update { it.copy(
                        selectedStore = store,
                        products      = store.products,
                        orders        = store.orders,
                        isLoading     = false
                    )}
                }
                .onFailure { e -> if (!silent) _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    fun createStore(name: String, description: String, type: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            if (lat == 0.0 && lng == 0.0) {
                _uiState.update { it.copy(error = "Ubicación inválida.", isLoading = false) }
                return@launch
            }
            createStoreUseCase(name, description, type, lat, lng)
                .onSuccess { store ->
                    _uiState.update { it.copy(
                        stores          = it.stores + store,
                        success         = "Tienda \"${store.name}\" creada correctamente",
                        isLoading       = false,
                        showCreateDialog = false,
                        selectedLocation = null
                    )}
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    fun updateStore(id: String, name: String, description: String, type: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            updateStoreUseCase(id, name, description, type, lat, lng)
                .onSuccess { updated ->
                    _uiState.update { state ->
                        state.copy(
                            stores    = state.stores.map { if (it.id == id) updated else it },
                            success   = "Tienda actualizada",
                            isLoading = false
                        )
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    fun deleteStore(id: String) {
        viewModelScope.launch {
            deleteStoreUseCase(id)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            stores        = state.stores.filter { it.id != id },
                            selectedStore = null,
                            success       = "Tienda eliminada"
                        )
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun createProduct(businessId: String, name: String, description: String, price: Double, stock: Int) {
        viewModelScope.launch {
            sellerRepo.createProduct(businessId, name, description, price, stock)
                .onSuccess { product ->
                    _uiState.update { it.copy(
                        products         = it.products + product,
                        success          = "Producto creado",
                        showProductDialog = false
                    )}
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun updateProductStock(productId: String, newStock: Int) {
        viewModelScope.launch {
            sellerRepo.updateProductStock(productId, newStock)
                .onSuccess { updatedProduct ->
                    _uiState.update { state ->
                        state.copy(
                            products = state.products.map { if (it.id == productId) updatedProduct else it },
                            success  = "Stock actualizado correctamente"
                        )
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            sellerRepo.deleteProduct(productId)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(products = state.products.filter { it.id != productId })
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    private var isScanningInternal = false

    @androidx.annotation.RequiresPermission(android.Manifest.permission.VIBRATE)
    fun scanQrOrder(qrCode: String) {
        if (isScanningInternal) return
        isScanningInternal = true
        
        // Limpiamos errores previos y cerramos el scanner inmediatamente
        _uiState.update { it.copy(showQrScanner = false, error = null) }

        viewModelScope.launch {
            sellerRepo.scanOrderQr(qrCode)
                .onSuccess { order ->
                    vibrationManager.vibrateSingle()
                    _uiState.update { state ->
                        state.copy(
                            scannedOrder  = order,
                            error         = null,
                            orders        = state.orders.map { if (it.id == order.id) order else it }
                        )
                    }
                }
                .onFailure { e ->
                    vibrationManager.vibrateError()
                    _uiState.update { it.copy(error = e.message) }
                }
            isScanningInternal = false
        }
    }

    fun markOrderAsReady(orderId: String) {
        viewModelScope.launch {
            sellerRepo.markOrderAsReady(orderId)
                .onSuccess { updatedOrder ->
                    _uiState.update { state ->
                        state.copy(
                            orders  = state.orders.map { if (it.id == orderId) updatedOrder else it },
                            success = "Orden lista para entrega"
                        )
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    suspend fun getCurrentLocation() = try { locationManager.getLastLocation() } catch (e: Exception) { null }

    fun setShowCreateDialog(show: Boolean) {
        _uiState.update { it.copy(showCreateDialog = show, selectedLocation = if (!show) null else it.selectedLocation) }
    }
    fun setShowProductDialog(show: Boolean)  { _uiState.update { it.copy(showProductDialog = show) } }
    fun setShowQrScanner(show: Boolean)      { _uiState.update { it.copy(showQrScanner = show) } }
    fun setShowLocationPicker(show: Boolean) { _uiState.update { it.copy(showLocationPicker = show) } }
    fun setSelectedLocation(location: LatLng?) {
        _uiState.update { it.copy(selectedLocation = location, showLocationPicker = false) }
    }
    fun clearScannedOrder()  { _uiState.update { it.copy(scannedOrder = null) } }
    fun clearSelectedStore() { _uiState.update { it.copy(selectedStore = null, products = emptyList(), orders = emptyList()) } }
    fun clearMessages()      { _uiState.update { it.copy(error = null, success = null) } }
}