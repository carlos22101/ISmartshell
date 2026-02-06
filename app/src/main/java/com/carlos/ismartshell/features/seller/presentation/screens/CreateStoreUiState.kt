package com.carlos.ismartshell.features.seller.presentation.screens

data class CreateStoreUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)