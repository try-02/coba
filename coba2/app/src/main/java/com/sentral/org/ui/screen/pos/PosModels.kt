package com.sentral.org.ui.screen.pos

import com.sentral.org.data.entity.ProdukEntity
import com.sentral.org.data.entity.ItemKeranjangEntity
import com.sentral.org.data.service.CheckoutUiState
import java.text.NumberFormat
import java.util.Locale

private val idrFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

data class ProductUi(
    val id: Long,
    val name: String,
    val sku: String,
    val category: String,
    val price: Long,
    val priceFormatted: String,
    val stockQuantity: Long = 0,
    val colorSeed: Long = id % 10, // for placeholder avatar color
)

data class CartItemUi(
    val id: Long,
    val productId: Long,
    val productName: String,
    val unitPrice: Long,
    val quantity: Long,
    val lineTotal: Long,
    val unitPriceFormatted: String,
    val lineTotalFormatted: String,
)

data class CategoryUi(
    val name: String,
    val isSelected: Boolean,
)

data class ShiftInfoUi(
    val isOpen: Boolean,
    val cashierName: String,
    val shiftId: Long,
)

data class PosUiState(
    val isLoading: Boolean = true,
    val products: List<ProductUi> = emptyList(),
    val categories: List<CategoryUi> = emptyList(),
    val searchQuery: String = "",
    val cartItems: List<CartItemUi> = emptyList(),
    val cartTotal: Long = 0,
    val cartTotalFormatted: String = "",
    val cartItemCount: Int = 0,
    val activeCartId: Long? = null,
    val shiftInfo: ShiftInfoUi? = null,
    val checkoutState: CheckoutUiState = CheckoutUiState.Idle,
    val isCartExpanded: Boolean = false,
)

// ── Mappers ──

fun ProdukEntity.toProductUi(): ProductUi = ProductUi(
    id = id,
    name = nama,
    sku = sku,
    category = kategori,
    price = harga,
    priceFormatted = formatRupiah(harga),
)

fun ItemKeranjangEntity.toCartItemUi(): CartItemUi {
    val lineTotal = lineTotal(unitPrice = hargaSatuan, quantity = jumlah)
    return CartItemUi(
        id = id,
        productId = produkId,
        productName = namaProduk,
        unitPrice = hargaSatuan,
        quantity = jumlah,
        lineTotal = lineTotal,
        unitPriceFormatted = formatRupiah(hargaSatuan),
        lineTotalFormatted = formatRupiah(lineTotal),
    )
}

fun lineTotal(unitPrice: Long, quantity: Long): Long {
    // Scale: quantity is in original units (not scaled 1000x)
    return unitPrice * quantity
}

fun formatRupiah(amount: Long): String {
    return idrFormat.format(amount).replace(",00", "")
        .replace("Rp", "Rp")
}

// Placeholder color palette for product avatars
val avatarColors = listOf(
    0xFF1DB954, // Spotify green
    0xFFE1332D, // red
    0xFF2E77D0, // blue
    0xFFF59E0B, // amber
    0xFF8B5CF6, // violet
    0xFFEC4899, // pink
    0xFF14B8A6, // teal
    0xFFF97316, // orange
    0xFF6366F1, // indigo
    0xFF84CC16, // lime
)
