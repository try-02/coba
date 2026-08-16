package com.toko.pos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toko.pos.PosApplication
import com.toko.pos.data.CartItem
import com.toko.pos.data.Product
import com.toko.pos.data.TransactionWithItems
import com.toko.pos.ui.models.CartItemUi
import com.toko.pos.ui.models.ProductUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModel : ViewModel() {
    private val repo = PosApplication.instance.repository

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    val searchResults: StateFlow<ImmutableList<ProductUi>> = _query
        .debounce(200)
        .flatMapLatest { repo.searchProducts(it) }
        .map { products -> products.map { it.toProductUi() }.toImmutableList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), persistentListOf())

    private val _cart = MutableStateFlow<ImmutableList<CartItemUi>>(persistentListOf())
    val cart: StateFlow<ImmutableList<CartItemUi>> = _cart.asStateFlow()

    val totalPrice: StateFlow<Long> = _cart
        .map { list -> list.sumOf { it.subtotal } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun updateSearch(query: String) { _query.value = query }

    fun addProduct(product: Product) {
        _cart.update { current ->
            val existingIndex = current.indexOfFirst { it.productId == product.id }
            if (existingIndex >= 0) {
                val existing = current[existingIndex]
                val newQty = existing.quantity + 1.0
                current.set(existingIndex, existing.copy(
                    quantity = newQty,
                    subtotal = (existing.price * newQty).toLong()
                ))
            } else {
                current.add(
                    CartItemUi(
                        productId = product.id,
                        productName = product.name,
                        price = product.price,
                        quantity = 1.0,
                        subtotal = product.price
                    )
                )
            }
        }
    }

    fun updateQuantity(productId: Long, newQty: Double) {
        _cart.update { current ->
            val index = current.indexOfFirst { it.productId == productId }
            if (index >= 0) {
                val item = current[index]
                val qty = if (newQty <= 0) 1.0 else newQty
                current.set(index, item.copy(
                    quantity = qty,
                    subtotal = (item.price * qty).toLong()
                ))
            } else current
        }
    }

    fun removeProduct(productId: Long) {
        _cart.update { current ->
            current.filterNot { it.productId == productId }.toImmutableList()
        }
    }

    fun clearCart() {
        _cart.value = persistentListOf()
    }

    fun checkout(
        paymentMethod: String,
        cashReceived: Long?,
        discountPercent: Double,
        customerId: Long?,
        cashierId: Long,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val cartItems = _cart.value.map {
                CartItem(
                    productId = it.productId,
                    productName = it.productName,
                    price = it.price,
                    quantity = it.quantity,
                    subtotal = it.subtotal
                )
            }
            repo.checkout(
                cart = cartItems,
                paymentMethod = paymentMethod,
                cashReceived = cashReceived,
                customerId = customerId,
                cashierId = cashierId,
                discountPercent = discountPercent,
                taxPercent = 0.0
            ).onSuccess { txId ->
                clearCart()
                onSuccess(txId)
            }.onFailure { e ->
                onError(e.message ?: "Gagal checkout")
            }
        }
    }

    suspend fun getTransaction(txId: Long): TransactionWithItems? = repo.getTransaction(txId)
}

fun Product.toProductUi() = ProductUi(
    id = id,
    name = name,
    price = price,
    stock = stock,
    unit = unit,
    barcode = barcode,
    categoryId = categoryId
)