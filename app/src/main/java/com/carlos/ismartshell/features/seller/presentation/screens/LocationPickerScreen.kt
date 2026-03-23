package com.carlos.ismartshell.features.seller.presentation.screens

import android.Manifest
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
    onLocationSelected: (LatLng) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

        // Estado interno para la ubicación seleccionada
        var selectedPoint by remember {
            mutableStateOf(
                initialLocation?.let { Point.fromLngLat(it.longitude, it.latitude) }
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
                    title = { Text("Seleccionar Ubicación") },
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
                            Icon(Icons.Default.Check, contentDescription = "Confirmar")
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
                                        .zoom(15.0)
                                        .build()
                                )

                                // Gestor de círculos para marcar la ubicación
                                val circleManager = annotations.createCircleAnnotationManager()

                                // Función para actualizar el marcador
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

                                // Marcador inicial
                                updateMarker(selectedPoint)

                                // Escuchar clics en el mapa
                                mapboxMap.addOnMapClickListener { point ->
                                    selectedPoint = point
                                    updateMarker(point)
                                    true
                                }
                            }
                        }
                    }
                )

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