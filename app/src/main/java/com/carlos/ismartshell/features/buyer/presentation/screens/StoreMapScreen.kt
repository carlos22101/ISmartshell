package com.carlos.ismartshell.features.buyer.presentation.screens

import android.Manifest
import android.graphics.Color
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import java.util.*

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
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                state.store?.let { store ->
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            MapView(context).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )

                                getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
                                    val storePoint = Point.fromLngLat(store.longitude, store.latitude)
                                    
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
                                            .withCircleColor(Color.parseColor("#E91E63"))
                                            .withCircleStrokeWidth(2.0)
                                            .withCircleStrokeColor("#FFFFFF")
                                    )

                                    // Marcador del USUARIO (Azul)
                                    state.userLocation?.let { loc ->
                                        annotationManager.create(
                                            CircleAnnotationOptions()
                                                .withPoint(Point.fromLngLat(loc.longitude, loc.latitude))
                                                .withCircleRadius(10.0)
                                                .withCircleColor(Color.parseColor("#2196F3"))
                                                .withCircleStrokeWidth(2.0)
                                                .withCircleStrokeColor("#FFFFFF")
                                        )
                                    }
                                }
                            }
                        }
                    )

                    // Overlay de distancia
                    DistanceOverlay(
                        userLat = state.userLocation?.latitude,
                        userLng = state.userLocation?.longitude,
                        storeLat = store.latitude,
                        storeLng = store.longitude
                    )
                }
            }
        }
    }
}

@Composable
private fun BoxScope.DistanceOverlay(
    userLat: Double?,
    userLng: Double?,
    storeLat: Double,
    storeLng: Double
) {
    if (userLat != null && userLng != null) {
        val distance = remember(userLat, userLng, storeLat, storeLng) {
            val results = FloatArray(1)
            android.location.Location.distanceBetween(userLat, userLng, storeLat, storeLng, results)
            results[0]
        }

        val distanceText = if (distance >= 1000) {
            String.format(Locale.getDefault(), "%.1f km", distance / 1000)
        } else {
            "${distance.toInt()} m"
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.DirectionsWalk,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Distancia: $distanceText",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
