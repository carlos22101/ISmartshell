package com.carlos.ismartshell.features.seller.presentation.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlos.ismartshell.features.seller.presentation.viewmodels.CreateStoreViewModel
import com.carlos.ismartshell.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStoreScreen(viewModel: CreateStoreViewModel) {
    var name by remember { mutableStateOf("") }
    var slug by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("") }
    var lng by remember { mutableStateOf("") }

    val uiState = viewModel.uiState
    val stores = viewModel.storesList
    val isEditing = viewModel.isEditing

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Panel de Vendedor",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Primary,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Sección de Formulario (Card)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Edit else Icons.Default.AddBusiness,
                            contentDescription = null,
                            tint = if (isEditing) Color(0xFFE65100) else Primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEditing) "Actualizar Tienda" else "Nueva Tienda",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre de la tienda") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Storefront, null, tint = Primary) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = slug,
                        onValueChange = { slug = it },
                        label = { Text("Slug (ej: mi-tienda)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Dirección física") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = Primary) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = lat,
                            onValueChange = { lat = it },
                            label = { Text("Latitud") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = lng,
                            onValueChange = { lng = it },
                            label = { Text("Longitud") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                viewModel.saveStore(name, slug, desc, address, lat, lng)
                                name = ""; slug = ""; desc = ""; address = ""; lat = ""; lng = ""
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEditing) Color(0xFFE65100) else Primary
                            )
                        ) {
                            Icon(Icons.Default.Save, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isEditing) "Actualizar" else "Guardar")
                        }

                        if (isEditing) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.resetForm()
                                    name = ""; slug = ""; desc = ""; address = ""; lat = ""; lng = ""
                                },
                                modifier = Modifier.weight(0.5f).height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Cancelar")
                            }
                        }
                    }

                    if (uiState.isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), color = Primary)
                    if (uiState.error != null) {
                        Text(
                            text = uiState.error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            Text(
                text = "Tus Tiendas Registradas",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(stores) { store ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Store, contentDescription = null, tint = Primary)
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = store.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = store.address,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                                )
                            }

                            IconButton(onClick = {
                                viewModel.onEditSelected(store)
                                name = store.name
                                desc = store.description
                                address = store.address

                            }) {
                                Icon(Icons.Default.Edit, "Editar", tint = Primary)
                            }

                            IconButton(onClick = { viewModel.deleteStore(store.id) }) {
                                Icon(Icons.Default.Delete, "Borrar", tint = Error)
                            }
                        }
                    }
                }
            }
        }
    }
}
