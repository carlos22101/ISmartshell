package com.carlos.ismartshell.features.buyer.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.features.buyer.domain.usecases.GetStoresUseCase
import com.carlos.ismartshell.features.buyer.presentation.screens.HomeBuyerUiState
import kotlinx.coroutines.launch

class HomeBuyerViewModel(private val getStoresUseCase: GetStoresUseCase) : ViewModel() {
    var uiState by mutableStateOf(HomeBuyerUiState())
        private set

    init {
        loadStores()
    }

    fun loadStores() {
        viewModelScope.launch {
            uiState = HomeBuyerUiState(isLoading = true)
            try {
                val stores = getStoresUseCase()
                uiState = HomeBuyerUiState(stores = stores)
            } catch (e: Exception) {
                uiState = HomeBuyerUiState(error = e.message)
            }
        }
    }
}