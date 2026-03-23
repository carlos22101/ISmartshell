package com.carlos.ismartshell.features.buyer.presentation.screens

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.carlos.ismartshell.features.buyer.domain.entities.BuyerStore
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun NearbyStoresMapScreen(
    stores: List<BuyerStore>,
    onBack: () -> Unit,
    onSelectStore: (String) -> Unit
) {
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    val initialPoint = if (stores.isNotEmpty()) {
        Point.fromLngLat(stores.first().longitude, stores.first().latitude)
    } else {
        Point.fromLngLat(-93.1148, 16.7516)
    }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(initialPoint)
            zoom(12.0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Negocios Cercanos") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }
                }
            )
        }
    ) { padding ->
        MapboxMap(
            modifier = Modifier.fillMaxSize().padding(padding),
            mapViewportState = mapViewportState
        ) {
            stores.forEach { store ->
                PointAnnotation(
                    point = Point.fromLngLat(store.longitude, store.latitude),
                    onClick = {
                        onSelectStore(store.id)
                        true
                    }
                )
            }
        }
    }
}