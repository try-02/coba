package com.sentral.org.domain.model

data class CartItem(
    val id: Long,
    val cartId: Long,
    val productId: Long,
    val productName: String,
    val unitPrice: Long,
    val quantity: Long,
)
