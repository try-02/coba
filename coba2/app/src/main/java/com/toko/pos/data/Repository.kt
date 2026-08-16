package com.toko.pos.data

import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class Repository(private val db: AppDatabase) {

    // Produk
    fun getAllProducts(): Flow<List<Product>> = db.productDao().getAllProducts()
    fun searchProducts(query: String): Flow<List<Product>> = db.productDao().searchProducts(query)
    suspend fun getProduct(id: Long): Product? = db.productDao().getProductById(id)
    suspend fun addProduct(product: Product): Long = db.productDao().insertProduct(product)
    suspend fun updateProduct(product: Product) = db.productDao().updateProduct(product)
    suspend fun deleteProduct(product: Product) = db.productDao().deleteProduct(product)
    fun getLowStockProducts(): Flow<List<Product>> = db.productDao().getLowStockProducts()

    // Kategori
    fun getAllCategories(): Flow<List<Category>> = db.categoryDao().getAllCategories()
    suspend fun addCategory(name: String): Long = db.categoryDao().insertCategory(Category(name = name))

    // Transaksi
    fun getAllTransactions(): Flow<List<Transaction>> = db.transactionDao().getAllTransactions()
    suspend fun getTransaction(id: Long): TransactionWithItems? = db.transactionDao().getTransactionWithItems(id)
    fun getSalesSummary(start: Long, end: Long): Flow<SaleSummary> = db.transactionDao().getSalesSummary(start, end)
    fun getTransactionsBetween(start: Long, end: Long): Flow<List<Transaction>> = db.transactionDao().getTransactionsBetween(start, end)
    fun getTopProducts(start: Long, end: Long): Flow<List<TopProduct>> = db.transactionDao().getTopProducts(start, end)

    suspend fun checkout(
        cart: List<CartItem>,
        paymentMethod: String,
        cashReceived: Long?,
        customerId: Long?,
        cashierId: Long,
        discountPercent: Double,
        taxPercent: Double
    ): Result<Long> = withContext(Dispatchers.IO) {
        try {
            db.withTransaction {
                // Validasi stok
                cart.forEach { item ->
                    val product = db.productDao().getProductById(item.productId)
                        ?: throw Exception("Produk ${item.productName} tidak ditemukan")
                    if (product.stock < item.quantity) {
                        throw Exception("Stok ${product.name} tidak mencukupi (sisa: ${product.stock.toInt()})")
                    }
                }

                // Hitung total
                val subtotal = cart.sumOf { it.subtotal }
                val discountAmount = (subtotal * discountPercent / 100).toLong()
                val taxAmount = ((subtotal - discountAmount) * taxPercent / 100).toLong()
                val total = subtotal - discountAmount + taxAmount

                // Insert transaksi
                val tx = Transaction(
                    transactionNumber = generateTransactionNumber(),
                    date = System.currentTimeMillis(),
                    subtotal = subtotal,
                    discountPercent = discountPercent,
                    discountAmount = discountAmount,
                    taxPercent = taxPercent,
                    taxAmount = taxAmount,
                    total = total,
                    paymentMethod = paymentMethod,
                    cashReceived = cashReceived,
                    change = if (paymentMethod == "CASH") (cashReceived ?: 0) - total else 0,
                    customerId = customerId,
                    cashierId = cashierId
                )
                val txId = db.transactionDao().insertTransaction(tx)

                // Insert items
                db.transactionDao().insertTransactionItems(
                    cart.map {
                        TransactionItem(
                            transactionId = txId,
                            productId = it.productId,
                            productName = it.productName,
                            quantity = it.quantity,
                            unitPrice = it.price,
                            costPrice = 0, // isi dari product.costPrice jika tersedia
                            discountPercent = it.discountPercent,
                            subtotal = it.subtotal
                        )
                    }
                )

                // Kurangi stok
                cart.forEach { item ->
                    val rowsUpdated = db.productDao().decreaseStock(item.productId, item.quantity)
                    if (rowsUpdated == 0) throw Exception("Gagal mengurangi stok ${item.productName}")
                }

                // Update hutang jika kredit
                if (customerId != null && paymentMethod == "CREDIT") {
                    db.customerDao().updateDebt(customerId, total)
                }

                Result.success(txId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateTransactionNumber(): String {
        val df = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        return "POS${df.format(Date())}${(1000..9999).random()}"
    }

    // Auth
    suspend fun login(username: String, password: String): Cashier? {
        val cashier = db.cashierDao().getCashierByUsername(username) ?: return null
        if (cashier.passwordHash == hashPassword(password)) return cashier
        return null
    }

    private fun hashPassword(password: String): String {
        return sha256("pos_salt:$password")
    }
}

data class CartItem(
    val productId: Long,
    val productName: String,
    val price: Long,
    val quantity: Double,
    val discountPercent: Double = 0.0,
    val subtotal: Long
)