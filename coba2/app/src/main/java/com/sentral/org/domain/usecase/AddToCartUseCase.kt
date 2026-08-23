package com.sentral.org.domain.usecase

import com.sentral.org.data.service.CartService

class AddToCartUseCase(
    private val cartService: CartService,
) {
    suspend operator fun invoke(cartId: Long, productId: Long, quantity: Long, now: Long): Result<Unit> {
        return cartService.addProduct(cartId, productId, quantity, now)
    }
}
