package com.toko.pos.session

import com.toko.pos.data.Cashier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SessionManager {
    private val _currentCashier = MutableStateFlow<Cashier?>(null)
    val currentCashier: StateFlow<Cashier?> = _currentCashier

    val currentCashierId: Long?
        get() = _currentCashier.value?.id
}