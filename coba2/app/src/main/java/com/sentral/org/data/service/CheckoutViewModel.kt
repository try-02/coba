package com.sentral.org.data.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentral.org.data.dao.ItemKeranjangDao
import com.sentral.org.data.dao.KeranjangDao
import com.sentral.org.data.entity.ItemKeranjangEntity
import com.sentral.org.data.entity.KeranjangEntity
import com.sentral.org.data.entity.ProdukEntity
import com.sentral.org.data.model.*
import com.sentral.org.domain.usecase.AddToCartUseCase
import com.sentral.org.domain.usecase.RemoveFromCartUseCase
import com.sentral.org.domain.usecase.SearchProductUseCase
import com.sentral.org.domain.usecase.UpdateCartQuantityUseCase
import com.sentral.org.ui.screen.pos.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.sentral.org.data.local.entity.ItemKeranjangEntity
import com.sentral.org.data.local.entity.KeranjangEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map


sealed interface CheckoutUiState {
    data object Idle : CheckoutUiState
    data object Processing : CheckoutUiState
    data class Success(val result: CheckoutResult) : CheckoutUiState
    data class Error(val message: String) : CheckoutUiState
}

class CheckoutViewModel(
    private val checkoutServiceLazy: Lazy<CheckoutService>,
    private val cartService: CartService,
    private val searchProductUseCase: SearchProductUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
    private val keranjangDao: KeranjangDao,
    private val itemKeranjangDao: ItemKeranjangDao,
) : ViewModel() {

    private val checkoutService by lazy { checkoutServiceLazy.value }

    // ── Internal mutable state flows ──

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _isCartExpanded = MutableStateFlow(false)
    private val _checkoutState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Idle)
    private var cartId: Long? = null

    private var searchJob: Job? = null

    // ── Public UI State ──

val state: StateFlow<PosUiState> = combine(
    _searchQuery,
    _selectedCategory,
    _isCartExpanded,
    _checkoutState,
    searchProductUseCase.observeCategories(),
    searchProductUseCase.observeFiltered("", null),
    observeCartItems(),          // <- ini harus Flow<List<CartItemUi>>
    observeActiveCart(),
) { values ->
    // Cast eksplisit
    val query: String = values[0] as String
    val category: String? = values[1] as String?
    val expanded: Boolean = values[2] as Boolean
    val checkoutState: CheckoutUiState = values[3] as CheckoutUiState
    val categories: List<String> = values[4] as List<String>
    val products: List<ProdukEntity> = values[5] as List<ProdukEntity>
    val cartItems: List<CartItemUi> = values[6] as List<CartItemUi>
    val cart: KeranjangEntity? = values[7] as KeranjangEntity?

    val filtered = if (query.isBlank() && category == null) {
        products.map { it.toProductUi() }
    } else {
        products.filter { p ->
            val matchQuery = query.isBlank() ||
                p.nama.contains(query, ignoreCase = true) ||
                p.sku.contains(query, ignoreCase = true) ||
                (p.barcode?.contains(query, ignoreCase = true) == true)
            val matchCategory = category == null || p.kategori == category
            matchQuery && matchCategory
        }.map { it.toProductUi() }
    }

    PosUiState(
        isLoading = false,
        products = filtered,
        categories = categories.map { CategoryUi(name = it, selected = it == category) },
        searchQuery = query,
        cartItems = cartItems,
        cartTotal = cartItems.sumOf { it.lineTotal },
        cartTotalFormatted = formatRupiah(cartItems.sumOf { it.lineTotal }),
        cartItemCount = cartItems.sumOf { it.quantity.toInt() },
        activeCartId = cart?.id,
        shiftInfo = null,
        checkoutState = checkoutState,
        isCartExpanded = expanded,
    )
}.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = PosUiState(),
)

    // ── Observe cart ──

private fun observeCartItems(): Flow<List<CartItemUi>> =
    keranjangDao.observeOpen()
        .flatMapLatest { carts ->
            val activeCart = carts.firstOrNull()
            if (activeCart == null) {
                flowOf(emptyList())
            } else {
                itemKeranjangDao.getByCart(activeCart.id)
                    .map { items: List<ItemKeranjangEntity> ->
                        items.map { item -> item.toCartItemUi() }
                    }
            }
        }



    private fun observeActiveCart(): Flow<KeranjangEntity?> = keranjangDao.observeOpen()
        .map { it.firstOrNull() }

    // ── Actions ──

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun onBarcodeScanned(barcode: String) {
        _searchQuery.value = barcode
        // Auto-add if exact match found
        viewModelScope.launch {
            val product = searchProductUseCase.observeFiltered(barcode, null)
                .first()
                .firstOrNull { it.barcode == barcode }

            if (product != null) {
                val cId = cartId
                if (cId != null) {
                    addToCartUseCase(cId, product.id, 1, System.currentTimeMillis())
                }
            }
        }
    }

    fun onAddToCart(productId: Long) {
        val cId = cartId ?: return
        viewModelScope.launch {
            addToCartUseCase(cId, productId, 1, System.currentTimeMillis())
        }
    }

    fun onIncrementQuantity(productId: Long) {
        val cId = cartId ?: return
        viewModelScope.launch {
            updateCartQuantityUseCase(cId, productId, 1, System.currentTimeMillis())
        }
    }

    fun onDecrementQuantity(productId: Long) {
        val cId = cartId ?: return
        viewModelScope.launch {
            val result = updateCartQuantityUseCase(cId, productId, -1, System.currentTimeMillis())
            // if fails (quantity ≤ 0), the item will be removed by dao changeQuantity logic
        }
    }

    fun onRemoveCartItem(itemId: Long) {
        viewModelScope.launch {
            removeFromCartUseCase(itemId)
        }
    }

    fun onToggleCartExpanded() {
        _isCartExpanded.value = !_isCartExpanded.value
    }

    fun onCartDragProgress(fraction: Float) {
        // fraction 0..1 → collapsed..expanded
        _isCartExpanded.value = fraction > 0.3f
    }

    fun onCartExpandedChanged(expanded: Boolean) {
        _isCartExpanded.value = expanded
    }

    // ── Checkout ──

    fun checkout(request: CheckoutRequest) {
        if (_checkoutState.value is CheckoutUiState.Processing) return

        viewModelScope.launch(Dispatchers.IO) {
            _checkoutState.value = CheckoutUiState.Processing
            val result = checkoutService.checkout(request)
            _checkoutState.value = result.fold(
                onSuccess = { CheckoutUiState.Success(it) },
                onFailure = { CheckoutUiState.Error(it.message ?: "Checkout gagal") },
            )
        }
    }

    fun resetCheckoutState() {
        _checkoutState.value = CheckoutUiState.Idle
    }
}
