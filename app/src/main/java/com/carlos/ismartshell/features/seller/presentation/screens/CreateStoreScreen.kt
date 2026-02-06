package com.carlos.ismartshell.features.seller.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.carlos.ismartshell.features.seller.presentation.viewmodels.CreateStoreViewModel

@Composable
fun CreateStoreScreen(viewModel: CreateStoreViewModel) {
    // Estados del formulario
    var name by remember { mutableStateOf("") }
    var slug by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("") }
    var lng by remember { mutableStateOf("") }

    val uiState = viewModel.uiState
    val stores = viewModel.storesList
    val isEditing = viewModel.isEditing

    // Efecto para limpiar campos si se resetea o rellenar si se edita
    // Nota: Esto es una simplificación. Lo ideal es tener el form state en el ViewModel.

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text(
            text = if (isEditing) "Editar Tienda" else "Crear Nueva Tienda",
            style = MaterialTheme.typography.headlineMedium
        )

        // --- FORMULARIO ---
        Column(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = slug, onValueChange = { slug = it }, label = { Text("Slug") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = lat, onValueChange = { lat = it }, label = { Text("Lat") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = lng, onValueChange = { lng = it }, label = { Text("Lng") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        viewModel.saveStore(name, slug, desc, address, lat, lng)
                        // Limpiar campos visuales tras guardar
                        name = ""; slug = ""; desc = ""; address = ""; lat = ""; lng = ""
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEditing) Color(0xFFE65100) else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (isEditing) "Actualizar" else "Crear")
                }

                if (isEditing) {
                    OutlinedButton(onClick = {
                        viewModel.resetForm()
                        name = ""; slug = ""; desc = ""; address = ""; lat = ""; lng = ""
                    }) {
                        Text("Cancelar")
                    }
                }
            }

            if (uiState.isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            if (uiState.error != null) Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
        }

        Divider()


        Text("Mis Tiendas", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(stores) { store ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(store.name, fontWeight = FontWeight.Bold)
                            Text(store.description, style = MaterialTheme.typography.bodySmall)
                        }

                        // Botones de acción
                        Row {
                            IconButton(onClick = {
                                viewModel.onEditSelected(store)
                                // Rellenar campos UI
                                name = store.name
                                desc = store.description
                                address = store.address
                                // slug, lat, lng no vienen en SellerStore entity actual,
                                // necesitarías agregarlos a la Entity si quieres editarlos
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                            }

                            IconButton(onClick = { viewModel.deleteStore(store.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}