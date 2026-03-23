package com.carlos.ismartshell.features.buyer.presentation.screens

import android.Manifest
import android.graphics.Color
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.carlos.ismartshell.features.buyer.domain.entities.BuyerStore
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun NearbyStoresMapScreen(
    stores: List<BuyerStore>,
    onBack: () -> Unit,
    onSelectStore: (String) -> Unit
) {
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        if (!locationPermission.status.isGranted) {
            locationPermission.launchPermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Negocios Cercanos (${stores.size})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            factory = { context ->
                MapView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    
                    getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
                        val initialPoint = if (stores.isNotEmpty()) {
                            Point.fromLngLat(stores.first().longitude, stores.first().latitude)
                        } else {
                            Point.fromLngLat(-93.1148, 16.7516)
                        }

                        getMapboxMap().setCamera(
                            CameraOptions.Builder()
                                .center(initialPoint)
                                .zoom(12.0)
                                .build()
                        )

                        // Gestor de anotaciones de círculo (muy estables y visibles)
                        val circleAnnotationManager = annotations.createCircleAnnotationManager()
                        
                        stores.forEach { store ->
                            val point = Point.fromLngLat(store.longitude, store.latitude)
                            
                            val circleOptions = CircleAnnotationOptions()
                                .withPoint(point)
                                .withCircleRadius(12.0)
                                .withCircleColor(Color.parseColor("#E91E63")) // Rosa
                                .withCircleStrokeWidth(2.0)
                                .withCircleStrokeColor("#FFFFFF")

                            val annotation = circleAnnotationManager.create(circleOptions)
                            
                            circleAnnotationManager.addClickListener { clicked ->
                                if (clicked.id == annotation.id) {
                                    onSelectStore(store.id)
                                    true
                                } else false
                            }
                        }
                    }
                }
            }
        )
    }
}