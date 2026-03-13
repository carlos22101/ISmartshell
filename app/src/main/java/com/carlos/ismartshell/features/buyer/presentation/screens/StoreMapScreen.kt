package com.carlos.ismartshell.features.buyer.presentation.screens

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlos.ismartshell.features.buyer.presentation.viewmodels.StoreMapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

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
            val storeLatLng = LatLng(store.latitude, store.longitude)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(storeLatLng, 15f)
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize().padding(padding),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = locationPermission.status.isGranted),
                uiSettings = MapUiSettings(myLocationButtonEnabled = true)
            ) {
                // Marcador principal del negocio
                Marker(
                    state = MarkerState(position = storeLatLng),
                    title = store.name,
                    snippet = store.type
                )

                // Puntos de entrega alternativos
                store.deliveryPoints.forEach { dp ->
                    Marker(
                        state = MarkerState(position = LatLng(dp.latitude, dp.longitude)),
                        title = dp.name,
                        snippet = "Punto de entrega"
                    )
                }
            }
        }
    }
}