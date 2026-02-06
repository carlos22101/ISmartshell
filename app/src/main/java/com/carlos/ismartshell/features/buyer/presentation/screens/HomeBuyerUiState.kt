package com.carlos.ismartshell.features.buyer.presentation.screens

import com.carlos.ismartshell.features.buyer.domain.entities.BuyerStore

data class HomeBuyerUiState(
    val isLoading: Boolean = false,
    val stores: List<BuyerStore> = emptyList(),
    val error: String? = null
)