package com.carlos.ismartshell.features.seller.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.core.local.TokenManager
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore
import com.carlos.ismartshell.features.seller.domain.usecases.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. Estado para los campos del formulario
data class StoreFormState(
    val name: String = "",
    val slug: String = "",
    val description: String = "",
    val address: String = "",
    val lat: String = "",
    val lng: String = ""
)

// 2. Estado para la UI (Carga, Errores, Lista de datos)
data class CreateStoreUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val stores: List<SellerStore> = emptyList(),
    val isEditing: Boolean = false,
    val storeToEdit: SellerStore? = null
)

class CreateStoreViewModel(
    private val getStoresUseCase: GetSellerStoresUseCase,
    private val createStoreUseCase: CreateStoreUseCase,
    private val updateStoreUseCase: UpdateStoreUseCase,
    private val deleteStoreUseCase: DeleteStoreUseCase,
    private val getDetailUseCase: GetSellerStoreDetailUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateStoreUiState())
    val uiState: StateFlow<CreateStoreUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(StoreFormState())
    val formState: StateFlow<StoreFormState> = _formState.asStateFlow()

    init {
        loadStores()
    }

    // --- Funciones para actualizar los campos (Delegación de UI) ---
    fun onNameChange(v: String) = _formState.update { it.copy(name = v) }
    fun onSlugChange(v: String) = _formState.update { it.copy(slug = v) }
    fun onDescChange(v: String) = _formState.update { it.copy(description = v) }
    fun onAddressChange(v: String) = _formState.update { it.copy(address = v) }
    fun onLatChange(v: String) = _formState.update { it.copy(lat = v) }
    fun onLngChange(v: String) = _formState.update { it.copy(lng = v) }

    fun loadStores() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val stores = getStoresUseCase()
                _uiState.update { it.copy(isLoading = false, stores = stores) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar: ${e.message}") }
            }
        }
    }

    fun onEditSelected(store: SellerStore) {
        // Activamos modo edición y rellenamos el formState desde el objeto store
        _uiState.update { it.copy(isEditing = true, storeToEdit = store) }
        _formState.update {
            it.copy(
                name = store.name,
                slug = store.slug,
                description = store.description,
                address = store.address,
                lat = store.lat.toString(),
                lng = store.lng.toString()
            )
        }
    }

    fun cancelEditing() {
        _uiState.update { it.copy(isEditing = false, storeToEdit = null) }
        _formState.update { StoreFormState() } // Limpiar formulario
    }

    fun saveStore() {
        viewModelScope.launch {
            val form = _formState.value
            val sellerId = tokenManager.getUserId()

            if (sellerId == -1) {
                _uiState.update { it.copy(error = "Sesión no válida") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val latD = form.lat.toDoubleOrNull() ?: 0.0
                val lngD = form.lng.toDoubleOrNull() ?: 0.0

                if (_uiState.value.isEditing) {
                    val storeId = _uiState.value.storeToEdit!!.id
                    updateStoreUseCase(storeId, form.name, form.slug, form.description, form.address, latD, lngD)
                } else {
                    createStoreUseCase(sellerId, form.name, form.slug, form.description, form.address, latD, lngD)
                }

                // Éxito: Limpiar formulario y recargar lista
                _formState.update { StoreFormState() }
                _uiState.update { it.copy(isSuccess = true, isEditing = false, storeToEdit = null) }
                loadStores()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deleteStore(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                deleteStoreUseCase(id)
                loadStores()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun resetSuccessFlag() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}