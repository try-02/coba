package com.toko.pos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toko.pos.PosApplication
import com.toko.pos.data.Product
import com.toko.pos.data.SaleSummary
import com.toko.pos.data.TopProduct
import kotlinx.coroutines.flow.*

class DashboardViewModel : ViewModel() {
    private val repo = PosApplication.instance.repository

    val todaySummary: StateFlow<SaleSummary?> =
        repo.getSalesSummary(startOfDay(), endOfDay())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val yesterdaySummary: StateFlow<SaleSummary?> = run {
        val start = startOfDay() - 86400000
        val end = startOfDay() - 1
        repo.getSalesSummary(start, end)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    val lowStockProducts: StateFlow<List<Product>> = repo.getLowStockProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topProducts: StateFlow<List<TopProduct>> = repo.getTopProducts(startOfDay(), endOfDay())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun startOfDay(): Long {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        return cal.timeInMillis
    }
    private fun endOfDay(): Long = startOfDay() + 86399999
}