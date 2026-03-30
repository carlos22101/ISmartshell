package com.carlos.ismartshell.features.maps.presentation.viewmodels

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.core.managers.LocationManager
import com.carlos.ismartshell.features.buyer.domain.entities.BuyerStore
import com.carlos.ismartshell.features.buyer.domain.repositories.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoreMapUiState(
    val store: BuyerStore? = null,
    val userLocation: Location? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class StoreMapViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val locationManager: LocationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoreMapUiState())
    val uiState = _uiState.asStateFlow()

    fun loadStore(storeId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val location = locationManager.getLastLocation()
            storeRepository.getStoreById(storeId)
                .onSuccess { store ->
                    _uiState.update { it.copy(store = store, userLocation = location, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }
}