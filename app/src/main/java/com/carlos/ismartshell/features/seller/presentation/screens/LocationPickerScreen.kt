package com.carlos.ismartshell.features.seller.presentation.screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.carlos.ismartshell.core.util.LatLng

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun LocationPickerScreen(
    initialLocation: LatLng?,
    onLocationSelected: (LatLng) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
        
        val initialPoint = initialLocation?.let { Point.fromLngLat(it.longitude, it.latitude) }
            ?: Point.fromLngLat(-93.1148, 16.7516)

        var selectedPoint by remember { mutableStateOf(initialPoint) }
        
        val mapViewportState = rememberMapViewportState {
            setCameraOptions {
                center(initialPoint)
                zoom(14.0)
            }
        }

        LaunchedEffect(Unit) {
            if (!locationPermission.status.isGranted) {
                locationPermission.launchPermissionRequest()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Ubicación con Mapbox") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Cerrar") }
                    },
                    actions = {
                        IconButton(onClick = { 
                            onLocationSelected(LatLng(selectedPoint.latitude(), selectedPoint.longitude())) 
                        }) {
                            Icon(Icons.Default.Check, "Confirmar")
                        }
                    }
                )
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding).background(Color.White)) {
                MapboxMap(
                    modifier = Modifier.fillMaxSize(),
                    mapViewportState = mapViewportState,
                    onMapClickListener = { point ->
                        selectedPoint = point
                        true
                    }
                ) {
                    PointAnnotation(
                        point = selectedPoint
                    )
                }
                
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                    )
                ) {
                    Text(
                        text = "Toca el mapa para marcar la ubicación de tu negocio",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}