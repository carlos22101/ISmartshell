package com.carlos.ismartshell.features.buyer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlos.ismartshell.features.buyer.domain.entities.QrScan
import com.carlos.ismartshell.features.buyer.domain.usecases.ClearQrHistoryUseCase
import com.carlos.ismartshell.features.buyer.domain.usecases.DeleteQrScanUseCase
import com.carlos.ismartshell.features.buyer.domain.usecases.GetQrHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QrHistoryUiState(
    val scans: List<QrScan> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = ""
)

@HiltViewModel
class QrHistoryViewModel @Inject constructor(
    private val getQrHistoryUseCase: GetQrHistoryUseCase,
    private val deleteQrScanUseCase: DeleteQrScanUseCase,
    private val clearQrHistoryUseCase: ClearQrHistoryUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Combina el historial con el filtro de búsqueda en tiempo real
    val uiState: StateFlow<QrHistoryUiState> = combine(
        getQrHistoryUseCase(),
        _searchQuery
    ) { scans, query ->
        val filtered = if (query.isBlank()) scans
        else scans.filter { it.rawValue.contains(query, ignoreCase = true) }

        QrHistoryUiState(scans = filtered, searchQuery = query)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = QrHistoryUiState(isLoading = true)
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun deleteQrScan(scan: QrScan) {
        viewModelScope.launch { deleteQrScanUseCase(scan) }
    }

    fun clearHistory() {
        viewModelScope.launch { clearQrHistoryUseCase() }
    }
}
