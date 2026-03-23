package com.carlos.ismartshell.features.buyer.presentation.screens

import android.Manifest
import android.graphics.Color
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlos.ismartshell.features.buyer.presentation.viewmodels.StoreMapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager

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
        if (!locationPermission.status.isGranted) {
            locationPermission.launchPermissionRequest()
        }
        viewModel.loadStore(businessId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.store?.name ?: "Mapa de la Tienda") },
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
            AndroidView(
                modifier = Modifier.fillMaxSize().padding(padding),
                factory = { context ->
                    MapView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
                            val storePoint = Point.fromLngLat(store.longitude, store.latitude)
                            
                            // Centrar cámara en la tienda
                            getMapboxMap().setCamera(
                                CameraOptions.Builder()
                                    .center(storePoint)
                                    .zoom(14.0)
                                    .build()
                            )

                            val annotationManager = annotations.createCircleAnnotationManager()

                            // Marcador de la TIENDA (Rosa)
                            annotationManager.create(
                                CircleAnnotationOptions()
                                    .withPoint(storePoint)
                                    .withCircleRadius(12.0)
                                    .withCircleColor(Color.parseColor("#E91E63")) // Rosa
                                    .withCircleStrokeWidth(2.0)
                                    .withCircleStrokeColor("#FFFFFF")
                            )

                            // Marcador del USUARIO (Azul) si tenemos la ubicación
                            state.userLocation?.let { loc ->
                                val userPoint = Point.fromLngLat(loc.longitude, loc.latitude)
                                annotationManager.create(
                                    CircleAnnotationOptions()
                                        .withPoint(userPoint)
                                        .withCircleRadius(10.0)
                                        .withCircleColor(Color.parseColor("#2196F3")) // Azul
                                        .withCircleStrokeWidth(2.0)
                                        .withCircleStrokeColor("#FFFFFF")
                                )
                            }

                            // Puntos de entrega adicionales
                            store.deliveryPoints.forEach { dp ->
                                annotationManager.create(
                                    CircleAnnotationOptions()
                                        .withPoint(Point.fromLngLat(dp.longitude, dp.latitude))
                                        .withCircleRadius(8.0)
                                        .withCircleColor(Color.parseColor("#FF9800")) // Naranja
                                        .withCircleStrokeWidth(1.5)
                                        .withCircleStrokeColor("#FFFFFF")
                                )
                            }
                        }
                    }
                },
                update = { mapView ->
                    // Aquí podrías actualizar la cámara si la ubicación del usuario cambia dinámicamente
                }
            )
        }
    }
}
