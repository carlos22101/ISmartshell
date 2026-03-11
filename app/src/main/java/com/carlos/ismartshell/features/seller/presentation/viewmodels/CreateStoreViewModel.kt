package com.carlos.ismartshell.features.seller.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.core.local.TokenManager
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore
import com.carlos.ismartshell.features.seller.domain.usecases.*
import com.carlos.ismartshell.features.seller.presentation.screens.CreateStoreUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateStoreViewModel @Inject constructor(
    private val getStoresUseCase: GetSellerStoresUseCase,
    private val createStoreUseCase: CreateStoreUseCase,
    private val updateStoreUseCase: UpdateStoreUseCase,
    private val deleteStoreUseCase: DeleteStoreUseCase,
    private val getDetailUseCase: GetSellerStoreDetailUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    var uiState by mutableStateOf(CreateStoreUiState())
        private set

    var storesList by mutableStateOf<List<SellerStore>>(emptyList())
        private set

    var isEditing by mutableStateOf(false)
        private set

    var currentStoreId by mutableStateOf<Int?>(null)
        private set

    init { loadStores() }

    fun loadStores() {
        viewModelScope.launch {
            try {
                storesList = getStoresUseCase()
            } catch (e: Exception) {
                uiState = uiState.copy(error = "Error al cargar lista: ${e.message}")
            }
        }
    }

    fun resetForm() {
        isEditing = false
        currentStoreId = null
        uiState = CreateStoreUiState()
    }

    fun onEditSelected(store: SellerStore) {
        isEditing = true
        currentStoreId = store.id
    }

    fun saveStore(name: String, slug: String, desc: String, address: String, lat: String, lng: String) {
        viewModelScope.launch {
            uiState = CreateStoreUiState(isLoading = true)
            try {
                val latDouble = lat.toDoubleOrNull() ?: 0.0
                val lngDouble = lng.toDoubleOrNull() ?: 0.0
                val sellerId = tokenManager.getUserId()

                if (sellerId == -1) { uiState = CreateStoreUiState(error = "Sesión no válida"); return@launch }

                if (isEditing && currentStoreId != null)
                    updateStoreUseCase(currentStoreId!!, name, slug, desc, address, latDouble, lngDouble)
                else
                    createStoreUseCase(sellerId, name, slug, desc, address, latDouble, lngDouble)

                uiState = CreateStoreUiState(isSuccess = true)
                loadStores()
                resetForm()
            } catch (e: Exception) {
                uiState = CreateStoreUiState(error = e.message)
            }
        }
    }

    fun deleteStore(id: Int) {
        viewModelScope.launch {
            uiState = CreateStoreUiState(isLoading = true)
            try {
                deleteStoreUseCase(id)
                loadStores()
                uiState = CreateStoreUiState(isSuccess = true)
            } catch (e: Exception) {
                uiState = CreateStoreUiState(error = e.message)
            }
        }
    }
}