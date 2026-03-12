package com.carlos.ismartshell.features.seller.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.core.local.TokenManager
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore
import com.carlos.ismartshell.features.seller.domain.usecases.*
import com.carlos.ismartshell.features.seller.presentation.screens.CreateStoreUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _uiState = MutableStateFlow(CreateStoreUiState())
    val uiState: StateFlow<CreateStoreUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(StoreFormState())
    val formState: StateFlow<StoreFormState> = _formState.asStateFlow()

    private var currentStoreId: Int? = null

    init {
        loadStores()
    }

    fun onNameChange(v: String) = _formState.update { it.copy(name = v) }
    fun onSlugChange(v: String) = _formState.update { it.copy(slug = v) }
    fun onDescChange(v: String) = _formState.update { it.copy(description = v) }
    fun onAddressChange(v: String) = _formState.update { it.copy(address = v) }
    fun onLatChange(v: String) = _formState.update { it.copy(lat = v) }
    fun onLngChange(v: String) = _formState.update { it.copy(lng = v) }

    fun loadStores() {
        viewModelScope.launch {
            try {
                val stores = getStoresUseCase()
                _uiState.update { it.copy(stores = stores, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al cargar: ${e.message}") }
            }
        }
    }

    fun onEditSelected(store: SellerStore) {
        currentStoreId = store.id
        _formState.update { StoreFormState(
            name = store.name,
            slug = store.slug,
            description = store.description,
            address = store.address,
            lat = store.lat?.toString() ?: "",
            lng = store.lng?.toString() ?: ""
        )}
        _uiState.update { it.copy(isEditing = true) }
    }

    fun cancelEditing() {
        currentStoreId = null
        _formState.update { StoreFormState() }
        _uiState.update { it.copy(isEditing = false, error = null) }
    }

    fun saveStore() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val f = _formState.value
                val lat = f.lat.toDoubleOrNull() ?: 0.0
                val lng = f.lng.toDoubleOrNull() ?: 0.0
                val sellerId = tokenManager.getUserId()
                
                if (sellerId == -1) {
                    _uiState.update { it.copy(isLoading = false, error = "Sesión no válida") }
                    return@launch
                }

                if (_uiState.value.isEditing && currentStoreId != null) {
                    updateStoreUseCase(currentStoreId!!, f.name, f.slug, f.description, f.address, lat, lng)
                } else {
                    createStoreUseCase(sellerId, f.name, f.slug, f.description, f.address, lat, lng)
                }

                loadStores()
                cancelEditing()
                _uiState.update { it.copy(isSuccess = true, isLoading = false) }
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
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun resetSuccessFlag() = _uiState.update { it.copy(isSuccess = false) }
}

data class StoreFormState(
    val name: String = "",
    val slug: String = "",
    val description: String = "",
    val address: String = "",
    val lat: String = "",
    val lng: String = ""
)
