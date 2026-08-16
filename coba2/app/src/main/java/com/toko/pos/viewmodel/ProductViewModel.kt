package com.toko.pos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toko.pos.PosApplication
import com.toko.pos.data.Product
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val repo = PosApplication.instance.repository

    val products: StateFlow<List<Product>> = repo.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun getProductById(id: Long): Product? = repo.getProduct(id)

    fun saveProduct(product: Product, onComplete: () -> Unit) {
        viewModelScope.launch {
            if (product.id == 0L) repo.addProduct(product) else repo.updateProduct(product)
            onComplete()
        }
    }

    fun delete(product: Product) {
        viewModelScope.launch { repo.deleteProduct(product) }
    }
}