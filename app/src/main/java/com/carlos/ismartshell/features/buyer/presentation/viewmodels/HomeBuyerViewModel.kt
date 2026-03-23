package com.carlos.ismartshell.features.buyer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.core.managers.LocationManager
import com.carlos.ismartshell.features.buyer.domain.repositories.StoreRepository
import com.carlos.ismartshell.features.buyer.domain.usecases.GetStoresUseCase
import com.carlos.ismartshell.features.buyer.presentation.screens.HomeBuyerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeBuyerViewModel @Inject constructor(
    private val getStoresUseCase: GetStoresUseCase,
    private val storeRepository: StoreRepository,
    private val locationManager: LocationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeBuyerUiState())
    val uiState = _uiState.asStateFlow()

    init { loadStores() }

    fun loadStores() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val location = try { locationManager.getLastLocation() } catch(e: Exception) { null }

            val lat = location?.latitude  ?: 16.7516
            val lng = location?.longitude ?: -93.1148

            // El radio es en KM como especifica tu API de Go
            getStoresUseCase(lat, lng, 10.0) // Buscamos en un radio de 10km
                .onSuccess { stores ->
                    _uiState.update { it.copy(stores = stores, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun selectStore(storeId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            storeRepository.getStoreById(storeId)
                .onSuccess { store ->
                    _uiState.update { it.copy(selectedStore = store, products = store.products, isLoading = false) }
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    fun loadMyOrders() {
        viewModelScope.launch {
            storeRepository.getMyOrders()
                .onSuccess { orders -> _uiState.update { it.copy(orders = orders) } }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun createOrder(
        businessId: String,
        type: String,
        items: List<Pair<String, Int>>,
        deliveryPointId: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            storeRepository.createOrder(businessId, type, items, deliveryPointId, 24)
                .onSuccess { order ->
                    _uiState.update { it.copy(orderSuccess = order, isLoading = false) }
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    fun clearError()        { _uiState.update { it.copy(error = null) } }
    fun clearOrderSuccess() { _uiState.update { it.copy(orderSuccess = null) } }
    fun clearSelectedStore(){ _uiState.update { it.copy(selectedStore = null, products = emptyList()) } }
}
