package com.toko.pos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toko.pos.PosApplication
import com.toko.pos.data.Cashier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repo = PosApplication.instance.repository

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun login(username: String, password: String, onSuccess: (Cashier) -> Unit) {
        viewModelScope.launch {
            val cashier = repo.login(username, password)
            if (cashier != null) {
                onSuccess(cashier)
            } else {
                _error.value = "Username atau password salah"
            }
        }
    }
}