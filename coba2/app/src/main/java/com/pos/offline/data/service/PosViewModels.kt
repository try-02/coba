package com.pos.offline.data.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.offline.data.model.CheckoutRequest
import com.pos.offline.data.model.CheckoutResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CheckoutUiState {
    data object Idle : CheckoutUiState
    data object Processing : CheckoutUiState
    data class Success(val result: CheckoutResult) : CheckoutUiState
    data class Error(val message: String) : CheckoutUiState
}

class CheckoutViewModel(
    private val checkoutService: CheckoutService,
) : ViewModel() {
    private val _state = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Idle)
    val state: StateFlow<CheckoutUiState> = _state.asStateFlow()

    fun checkout(request: CheckoutRequest) {
        if (_state.value is CheckoutUiState.Processing) return
        viewModelScope.launch {
            _state.value = CheckoutUiState.Processing
            val result = checkoutService.checkout(request)
            _state.value = result.fold(
                onSuccess = { CheckoutUiState.Success(it) },
                onFailure = { CheckoutUiState.Error(it.message ?: "Checkout gagal") },
            )
        }
    }

    fun reset() {
        _state.value = CheckoutUiState.Idle
    }
}
