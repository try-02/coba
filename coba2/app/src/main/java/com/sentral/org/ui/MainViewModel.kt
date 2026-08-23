package com.sentral.org.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    // State flow untuk menandai apakah aplikasi sudah siap dirender
    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    init {
        // viewModelScope berjalan secara asinkron di belakang layar
        viewModelScope.launch {
            // Beri waktu 1 detik (1000ms) untuk pemanasan Compose dan Koin.
            // Di masa depan, kamu bisa mengganti delay ini dengan logika 
            // sinkronisasi data awal dari Room Database jika dibutuhkan.
            delay(3000) 
            _isReady.value = true
        }
    }
}
