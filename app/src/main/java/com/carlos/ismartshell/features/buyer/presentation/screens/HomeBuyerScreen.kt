package com.carlos.ismartshell.features.buyer.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlos.ismartshell.features.buyer.presentation.viewmodels.HomeBuyerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBuyerScreen(viewModel: HomeBuyerViewModel) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Tiendas Disponibles") }) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn {
                    items(uiState.stores) { store ->
                        Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(store.name, style = MaterialTheme.typography.titleMedium)
                                Text(store.description)
                            }
                        }
                    }
                }
            }
        }
    }
}