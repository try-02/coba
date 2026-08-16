package com.toko.pos.data

import androidx.room.*

@Entity(tableName = "cashiers")
data class Cashier(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val name: String,
    val isActive: Boolean = true
)

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val color: Long = 0xFF4CAF50
)

@Entity(tableName = "products", indices = [Index("barcode")])
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val barcode: String? = null,
    val name: String,
    val price: Long,
    val costPrice: Long = 0,
    val stock: Double = 0.0,
    val unit: String = "pcs",
    val categoryId: Long? = null,
    val imagePath: String? = null,
    val minStock: Int = 0,
    val isActive: Boolean = true
)

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String? = null,
    val address: String? = null,
    val debt: Long = 0
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionNumber: String,
    val date: Long,
    val subtotal: Long,
    val discountPercent: Double = 0.0,
    val discountAmount: Long = 0,
    val taxPercent: Double = 0.0,
    val taxAmount: Long = 0,
    val total: Long,
    val paymentMethod: String,
    val cashReceived: Long? = null,
    val change: Long? = null,
    val customerId: Long? = null,
    val cashierId: Long,
    val status: String = "SUCCESS",
    val note: String? = null
)

@Entity(tableName = "transaction_items", foreignKeys = [
    ForeignKey(entity = Transaction::class, parentColumns = ["id"], childColumns = ["transactionId"], onDelete = ForeignKey.CASCADE)
], indices = [Index("transactionId")])
data class TransactionItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionId: Long,
    val productId: Long?,
    val productName: String,
    val quantity: Double,
    val unitPrice: Long,
    val costPrice: Long,
    val discountPercent: Double = 0.0,
    val subtotal: Long
)

data class TransactionWithItems(
    @Embedded val transaction: Transaction,
    @Relation(parentColumn = "id", entityColumn = "transactionId")
    val items: List<TransactionItem>
)

data class SaleSummary(
    val revenue: Long = 0,
    val profit: Long = 0
)