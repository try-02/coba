package com.toko.pos.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY name")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isActive = 1 AND (name LIKE '%' || :query || '%' OR barcode = :query) ORDER BY name")
    fun searchProducts(query: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): Product?

    @Query("SELECT * FROM products WHERE stock <= minStock AND isActive = 1")
    fun getLowStockProducts(): Flow<List<Product>>

    @Insert
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("UPDATE products SET stock = stock + :qty WHERE id = :id")
    suspend fun increaseStock(id: Long, qty: Double)

    @Query("UPDATE products SET stock = stock - :qty WHERE id = :id AND stock >= :qty")
    suspend fun decreaseStock(id: Long, qty: Double): Int
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name")
    fun getAllCategories(): Flow<List<Category>>

    @Insert
    suspend fun insertCategory(category: Category): Long

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): Category?
}

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: Transaction): Long

    @Insert
    suspend fun insertTransactionItems(items: List<TransactionItem>)

    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionWithItems(id: Long): TransactionWithItems?

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransaction(id: Long): Transaction?

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end")
    fun getTransactionsBetween(start: Long, end: Long): Flow<List<Transaction>>

    @Query("""
        SELECT COALESCE(SUM(CASE WHEN status = 'SUCCESS' THEN total END), 0) as revenue,
               COALESCE(SUM(CASE WHEN status = 'SUCCESS' THEN total - subtotal END), 0) as profit
        FROM transactions WHERE date BETWEEN :start AND :end
    """)
    fun getSalesSummary(start: Long, end: Long): Flow<SaleSummary>

    @Query("""
        SELECT ti.productName as name, SUM(ti.quantity) as totalSold
        FROM transaction_items ti
        INNER JOIN transactions t ON ti.transactionId = t.id
        WHERE t.date BETWEEN :start AND :end AND t.status = 'SUCCESS'
        GROUP BY ti.productId, ti.productName
        ORDER BY totalSold DESC
        LIMIT 5
    """)
    fun getTopProducts(start: Long, end: Long): Flow<List<TopProduct>>
}

data class TopProduct(
    val name: String,
    val totalSold: Double
)

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: Long): Customer?

    @Insert
    suspend fun insertCustomer(customer: Customer): Long

    @Query("UPDATE customers SET debt = debt + :amount WHERE id = :id")
    suspend fun updateDebt(id: Long, amount: Long)
}

@Dao
interface CashierDao {
    @Query("SELECT * FROM cashiers WHERE username = :username AND isActive = 1")
    suspend fun getCashierByUsername(username: String): Cashier?

    @Query("SELECT * FROM cashiers ORDER BY name")
    fun getAllCashiers(): Flow<List<Cashier>>

    @Insert
    suspend fun insertCashier(cashier: Cashier)
}