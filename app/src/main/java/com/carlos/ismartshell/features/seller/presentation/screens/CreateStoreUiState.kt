package com.carlos.ismartshell.features.seller.presentation.screens

import com.carlos.ismartshell.features.seller.domain.entities.SellerStore

data class CreateStoreUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isEditing: Boolean = false,
    val error: String? = null,
    val stores: List<SellerStore> = emptyList()
)
