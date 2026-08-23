package com.sentral.org.domain.model

data class Product(
    val id: Long,
    val name: String,
    val sku: String,
    val barcode: String?,
    val price: Long,
    val cost: Long,
    val category: String,
    val active: Boolean,
)
