package com.carlos.ismartshell.features.seller.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.core.managers.LocationManager
import com.carlos.ismartshell.core.managers.VibrationManager
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

    init { loadStores() }

    fun loadStores() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getStoresUseCase()
                .onSuccess { stores -> _uiState.update { it.copy(stores = stores, isLoading = false) } }
                .onFailure { e    -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    fun loadStoreDetail(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getDetailUseCase(id)
                .onSuccess { store ->
                    _uiState.update { it.copy(
                        selectedStore = store,
                        products      = store.products,
                        orders        = store.orders,
                        isLoading     = false
                    )}
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    fun createStore(name: String, description: String, type: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Obtenemos la ubicación actual automáticamente
            val location = try { locationManager.getLastLocation() } catch (e: Exception) { null }
            val lat = location?.latitude ?: 0.0
            val lng = location?.longitude ?: 0.0
            
            if (lat == 0.0 && lng == 0.0) {
                _uiState.update { it.copy(error = "No se pudo obtener tu ubicación. Activa el GPS.", isLoading = false) }
                return@launch
            }

            createStoreUseCase(name, description, type, lat, lng)
                .onSuccess { store ->
                    _uiState.update { it.copy(
                        stores    = it.stores + store,
                        success   = "Tienda \"${store.name}\" creada en tu ubicación actual",
                        isLoading = false
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
                    _uiState.update { it.copy(products = it.products + product, success = "Producto creado") }
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

    @androidx.annotation.RequiresPermission(android.Manifest.permission.VIBRATE)
    fun scanQrOrder(qrCode: String) {
        viewModelScope.launch {
            sellerRepo.scanOrderQr(qrCode)
                .onSuccess  { order ->
                    vibrationManager.vibrateSingle()
                    _uiState.update { it.copy(scannedOrder = order) }
                }
                .onFailure { e ->
                    vibrationManager.vibrateError()
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun clearScannedOrder() { _uiState.update { it.copy(scannedOrder = null) } }
    fun clearSelectedStore() { _uiState.update { it.copy(selectedStore = null, products = emptyList(), orders = emptyList()) } }
    fun clearMessages()      { _uiState.update { it.copy(error = null, success = null) } }
}
