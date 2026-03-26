package com.carlos.ismartshell.features.seller.presentation.screens

import android.Manifest
import android.location.Location
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.carlos.ismartshell.core.util.LatLng
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
import com.mapbox.maps.plugin.gestures.addOnMapClickListener

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun LocationPickerScreen(
    initialLocation: LatLng?,
    userLocation: Location?,
    onLocationSelected: (LatLng) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

        // Prioridad: 1. Ubicación ya seleccionada, 2. Ubicación GPS actual, 3. Por defecto (Tuxtla)
        var selectedPoint by remember {
            mutableStateOf(
                initialLocation?.let { Point.fromLngLat(it.longitude, it.latitude) }
                    ?: userLocation?.let { Point.fromLngLat(it.longitude, it.latitude) }
                    ?: Point.fromLngLat(-93.1148, 16.7516)
            )
        }

        LaunchedEffect(Unit) {
            if (!locationPermission.status.isGranted) {
                locationPermission.launchPermissionRequest()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Ubicación del Negocio") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            onLocationSelected(
                                LatLng(
                                    selectedPoint.latitude(),
                                    selectedPoint.longitude()
                                )
                            )
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Confirmar", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.White)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        MapView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            val mapboxMap = getMapboxMap()

                            mapboxMap.loadStyleUri(Style.MAPBOX_STREETS) { style ->
                                mapboxMap.setCamera(
                                    CameraOptions.Builder()
                                        .center(selectedPoint)
                                        .zoom(16.0)
                                        .build()
                                )

                                val circleManager = annotations.createCircleAnnotationManager()

                                fun updateMarker(point: Point) {
                                    circleManager.deleteAll()
                                    circleManager.create(
                                        CircleAnnotationOptions()
                                            .withPoint(point)
                                            .withCircleRadius(10.0)
                                            .withCircleColor(Color.Red.toArgb())
                                            .withCircleStrokeWidth(2.0)
                                            .withCircleStrokeColor(Color.White.toArgb())
                                    )
                                }

                                updateMarker(selectedPoint)

                                mapboxMap.addOnMapClickListener { point ->
                                    selectedPoint = point
                                    updateMarker(point)
                                    true
                                }
                            }
                        }
                    }
                )

                // Botón flotante para centrar en mi ubicación
                if (userLocation != null) {
                    SmallFloatingActionButton(
                        onClick = {
                            selectedPoint = Point.fromLngLat(userLocation.longitude, userLocation.latitude)
                            // La cámara se centraría si tuviéramos acceso al mapboxMap aquí fácilmente, 
                            // pero al menos el marcador se moverá.
                        },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).padding(bottom = 80.dp),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Icon(Icons.Default.MyLocation, "Mi ubicación")
                    }
                }

                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        text = "Toca el mapa para marcar el punto exacto de tu negocio",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
