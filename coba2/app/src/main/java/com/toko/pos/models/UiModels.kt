package com.toko.pos.ui.models

import androidx.compose.runtime.Immutable

@Immutable
data class CartItemUi(
    val productId: Long,
    val productName: String,
    val price: Long,
    val quantity: Double,
    val subtotal: Long,
    val discountPercent: Double = 0.0
)

@Immutable
data class ProductUi(
    val id: Long,
    val name: String,
    val price: Long,
    val stock: Double,
    val unit: String,
    val barcode: String?,
    val categoryId: Long?
)