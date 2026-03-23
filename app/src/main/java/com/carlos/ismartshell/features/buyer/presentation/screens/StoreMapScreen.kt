package com.carlos.ismartshell.features.buyer.presentation.screens

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlos.ismartshell.features.buyer.presentation.viewmodels.StoreMapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun StoreMapScreen(
    businessId: String,
    onBack: () -> Unit,
    viewModel: StoreMapViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(businessId) {
        if (!locationPermission.status.isGranted) locationPermission.launchPermissionRequest()
        viewModel.loadStore(businessId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.store?.name ?: "Mapa") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        state.store?.let { store ->
            val storePoint = Point.fromLngLat(store.longitude, store.latitude)
            val mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    center(storePoint)
                    zoom(15.0)
                }
            }

            MapboxMap(
                modifier = Modifier.fillMaxSize().padding(padding),
                mapViewportState = mapViewportState
            ) {
                // Marcador principal del negocio
                PointAnnotation(
                    point = storePoint
                )

                // Puntos de entrega alternativos
                store.deliveryPoints.forEach { dp ->
                    PointAnnotation(
                        point = Point.fromLngLat(dp.longitude, dp.latitude)
                    )
                }
            }
        }
    }
}