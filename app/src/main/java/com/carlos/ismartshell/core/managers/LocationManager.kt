package com.carlos.ismartshell.core.managers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class LatLng(val lat: Double, val lng: Double)

@Singleton
class LocationManager @Inject constructor(
    private val context: Context
) {
    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation

    fun isPermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(): LatLng? {
        if (!isPermissionGranted()) throw SecurityException("Permiso de ubicación no concedido")

        return kotlinx.coroutines.suspendCancellableCoroutine { cont ->
            fusedClient.lastLocation
                .addOnSuccessListener { loc: Location? ->
                    val result = loc?.let { LatLng(it.latitude, it.longitude) }
                    _currentLocation.value = result
                    cont.resume(result) {}
                }
                .addOnFailureListener { cont.resume(null) {} }
        }
    }

    @SuppressLint("MissingPermission")
    fun locationUpdates(
        intervalMs: Long = 5_000L,
        fastestIntervalMs: Long = 2_000L
    ): Flow<LatLng> = callbackFlow {

        if (!isPermissionGranted()) {
            close(SecurityException("Permiso de ubicación no concedido"))
            return@callbackFlow
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
            .setMinUpdateIntervalMillis(fastestIntervalMs)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    val latLng = LatLng(loc.latitude, loc.longitude)
                    _currentLocation.value = latLng
                    trySend(latLng)
                }
            }
        }

        fusedClient.requestLocationUpdates(request, callback, context.mainLooper)

        awaitClose { fusedClient.removeLocationUpdates(callback) }
    }
}
