package com.carlos.ismartshell.features.buyer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.core.managers.LatLng
import com.carlos.ismartshell.core.managers.LocationManager
import com.carlos.ismartshell.features.buyer.domain.entities.BuyerStore
import com.carlos.ismartshell.features.buyer.domain.repositories.StoreRepository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.CameraPositionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.*

@HiltViewModel
class StoreMapViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val locationManager: LocationManager
) : ViewModel() {

    private val _store = MutableStateFlow<BuyerStore?>(null)
    val store: StateFlow<BuyerStore?> = _store

    private val _storeLocation = MutableStateFlow<LatLng?>(null)
    val storeLocation: StateFlow<LatLng?> = _storeLocation

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var locationJob: Job? = null

    fun loadStore(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val store = storeRepository.getStores().firstOrNull { it.id == id }
                _store.value = store
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setStoreLocation(lat: Double, lng: Double) {
        _storeLocation.value = LatLng(lat, lng)
    }

    fun startLocationTracking() {
        locationJob = viewModelScope.launch {
            try {
                val last = locationManager.getLastLocation()
                if (last != null) _userLocation.value = last

                locationManager.locationUpdates().collect { latLng ->
                    _userLocation.value = latLng
                }
            } catch (e: SecurityException) {
            }
        }
    }

    fun stopLocationTracking() {
        locationJob?.cancel()
    }

    fun centerOnUser(cameraPositionState: CameraPositionState) {
        val user = _userLocation.value ?: return
        viewModelScope.launch {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    com.google.android.gms.maps.model.LatLng(user.lat, user.lng),
                    16f
                )
            )
        }
    }

    fun calculateDistance(): Double? {
        val user = _userLocation.value ?: return null
        val store = _storeLocation.value ?: return null

        val r = 6_371_000.0 // Radio de la Tierra en metros
        val lat1 = Math.toRadians(user.lat)
        val lat2 = Math.toRadians(store.lat)
        val dLat = Math.toRadians(store.lat - user.lat)
        val dLng = Math.toRadians(store.lng - user.lng)

        val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return r * c
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationTracking()
    }
}
