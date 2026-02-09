package com.carlos.ismartshell.features.buyer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.features.buyer.domain.usecases.GetStoresUseCase
import com.carlos.ismartshell.features.buyer.presentation.screens.HomeBuyerUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeBuyerViewModel(private val getStoresUseCase: GetStoresUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeBuyerUiState())
    val uiState: StateFlow<HomeBuyerUiState> = _uiState.asStateFlow()
    init {
        loadStores()
    }
    fun loadStores() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val stores = getStoresUseCase()
                _uiState.update {
                    it.copy(isLoading = false, stores = stores, error = null)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }
}