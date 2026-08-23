package com.sentral.org.data.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentral.org.data.model.CheckoutRequest
import com.sentral.org.data.model.CheckoutResult
import kotlinx.coroutines.Dispatchers
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
    // 1. Ubah parameter menjadi Lazy untuk menunda perakitan 12 DAO
    private val checkoutServiceLazy: Lazy<CheckoutService>,
) : ViewModel() {
    private val _state = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Idle)
    val state: StateFlow<CheckoutUiState> = _state.asStateFlow()

    // 2. Ambil instance service sebenarnya secara aman
    private val checkoutService by lazy { checkoutServiceLazy.value }

    fun checkout(request: CheckoutRequest) {
        if (_state.value is CheckoutUiState.Processing) return
        
        // 3. Pindahkan seluruh proses inisialisasi dan eksekusi ke Background Thread
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = CheckoutUiState.Processing
            
            // Saat checkoutService dipanggil pertama kali di sini, 
            // perakitan berat akan terjadi di IO thread, membebaskan main thread!
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
