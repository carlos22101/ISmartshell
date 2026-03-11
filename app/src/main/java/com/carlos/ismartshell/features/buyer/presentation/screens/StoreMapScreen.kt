package com.carlos.ismartshell.features.buyer.presentation.screens

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carlos.ismartshell.features.buyer.presentation.viewmodels.StoreMapViewModel
import com.carlos.ismartshell.ui.theme.Primary
import com.google.accompanist.permissions.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StoreMapScreen(
    storeId: Int,
    viewModel: StoreMapViewModel,
    onBack: () -> Unit
) {
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val storeLocation by viewModel.storeLocation.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val store by viewModel.store.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(storeId) { viewModel.loadStore(storeId) }

    LaunchedEffect(locationPermission.status) {
        if (locationPermission.status.isGranted) {
            viewModel.startLocationTracking()
        } else {
            locationPermission.launchPermissionRequest()
        }
    }

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(storeLocation) {
        storeLocation?.let { loc ->
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(LatLng(loc.lat, loc.lng), 15f)
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(store?.name ?: "Mapa de Tienda", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (locationPermission.status.isGranted && userLocation != null) {
                FloatingActionButton(
                    onClick = {
                        userLocation?.let { loc ->
                            viewModel.centerOnUser(cameraPositionState)
                        }
                    },
                    containerColor = Primary
                ) {
                    Icon(Icons.Default.MyLocation, "Mi ubicación", tint = Color.White)
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Primary)
            } else {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = locationPermission.status.isGranted
                    ),
                    uiSettings = MapUiSettings(myLocationButtonEnabled = false)
                ) {
                    storeLocation?.let { loc ->
                        Marker(
                            state = MarkerState(position = LatLng(loc.lat, loc.lng)),
                            title = store?.name ?: "Tienda",
                            snippet = store?.address ?: "",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
                        )
                    }
                }
            }

            if (locationPermission.status.isGranted && userLocation != null && storeLocation != null) {
                val distance = viewModel.calculateDistance()
                if (distance != null) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .fillMaxWidth(),
                        color = Primary,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        tonalElevation = 4.dp
                    ) {
                        Text(
                            text = if (distance < 1000)
                                "📍 A ${distance.toInt()} metros de la tienda"
                            else
                                "📍 A ${"%.1f".format(distance / 1000)} km de la tienda",
                            modifier = Modifier.padding(16.dp),
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopLocationTracking() }
    }
}
