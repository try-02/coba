package com.pos.offline.data.local

import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PosDatabaseMigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        PosDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory(),
    )

    private fun migrateAndValidate(
        fromVersion: Int,
        toVersion: Int,
        vararg migrations: Migration,
    ) {
        helper.createDatabase(TEST_DB, fromVersion).close()
        helper.runMigrationsAndValidate(TEST_DB, toVersion, true, *migrations).close()
    }

    // Guard murah: menangkap kasus umum "versi dinaikkan tapi lupa daftarkan Migration baru
    // ke Migrations.ALL" -- kalau tidak, ini baru ketahuan sebagai crash di device pengguna asli.
    @Test
    fun jumlahMigrasiSesuaiVersiDatabase() {
        assertEquals(18, Migrations.ALL.size) // versi 19 -> 18 hop
    }

    // Rantai penuh -- TIDAK terpengaruh berkas skema yang hilang (misal 4.json).
    @Test
    fun migrateAll_v1_ke_v19() {
        helper.createDatabase(TEST_DB, 1).close()
        helper.runMigrationsAndValidate(TEST_DB, 19, true, *Migrations.ALL).close()
    }

    // Hop terisolasi -- hanya yang skema awal & targetnya tersedia.
    @Test
    fun migrate1To2_tambahKolomCost() = migrateAndValidate(1, 2, Migrations.MIGRATION_1_2)

    @Test
    fun migrate2To3_tambahKasirDanShift() = migrateAndValidate(2, 3, Migrations.MIGRATION_2_3)

    @Test
    fun migrate5To6_tambahStatusVoid() = migrateAndValidate(5, 6, Migrations.MIGRATION_5_6)

    @Test
    fun migrate6To7_tambahTabelReturns() = migrateAndValidate(6, 7, Migrations.MIGRATION_6_7)

    @Test
    fun migrate7To8_tambahTabelPrinterDanStoreProfile() = migrateAndValidate(7, 8, Migrations.MIGRATION_7_8)

    @Test
    fun migrate8To9_tambahBarcode() = migrateAndValidate(8, 9, Migrations.MIGRATION_8_9)

    @Test
    fun migrate9To10_tambahKategori() = migrateAndValidate(9, 10, Migrations.MIGRATION_9_10)

    @Test
    fun migrate10To11_tambahStatusQueryTracking() = migrateAndValidate(10, 11, Migrations.MIGRATION_10_11)

    @Test
    fun migrate11To12_tambahIndexPerforma() = migrateAndValidate(11, 12, Migrations.MIGRATION_11_12)

    @Test
    fun migrate12To13_tambahChangeGiven() = migrateAndValidate(12, 13, Migrations.MIGRATION_12_13)

    @Test
    fun migrate13To14_tambahChangeGivenInCash() = migrateAndValidate(13, 14, Migrations.MIGRATION_13_14)

    // Hop paling berisiko: rebuild total 4 tabel (cart_items, products, transaction_items,
    // return_items).
    @Test
    fun migrate14To15_rebuildEmpatTabel() = migrateAndValidate(14, 15, Migrations.MIGRATION_14_15)

    @Test
    fun migrate15To16_tambahDamagedStock() = migrateAndValidate(15, 16, Migrations.MIGRATION_15_16)

    @Test
    fun migrate16To17_tambahIsWarrantyExchangeTransaksi() = migrateAndValidate(16, 17, Migrations.MIGRATION_16_17)

    @Test
    fun migrate17To18_tambahIsWarrantyExchangeRetur() = migrateAndValidate(17, 18, Migrations.MIGRATION_17_18)

    @Test
    fun migrate18To19_tambahrestockedToDamagedReturItem() = migrateAndValidate(18, 19, Migrations.MIGRATION_18_19)

    // Integritas DATA lintas migrasi 14->15 (bukan cuma bentuk skema) -- memverifikasi
    // stock (INTEGER->REAL) dan quantity (INTEGER->REAL) terkonversi tanpa kehilangan nilai.
    @Test
    fun migrate14To15_dataStokDanKuantitasTerkonversiBenar() {
        helper.createDatabase(TEST_DB, 14).apply {
            execSQL(
                "INSERT INTO products (id, name, sku, barcode, price, cost, stock, active, createdAt, updatedAt, category) " +
                    "VALUES (1, 'Produk Lama', 'SKU-OLD', NULL, 10000, 4000, 7, 1, 1000, 1000, '')",
            )
            execSQL(
                "INSERT INTO cart_items (productId, name, unitPrice, quantity) VALUES (1, 'Produk Lama', 10000, 3)",
            )
        }.close()

        val migrated = helper.runMigrationsAndValidate(TEST_DB, 15, true, Migrations.MIGRATION_14_15)

        migrated.query("SELECT stock FROM products WHERE sku = 'SKU-OLD'").use { cursor ->
            assertTrue("Baris produk lama harus tetap ada", cursor.moveToFirst())
            assertEquals(7.0, cursor.getDouble(0), 0.0001)
        }
        migrated.query("SELECT quantity FROM cart_items WHERE productId = 1").use { cursor ->
            assertTrue("Baris cart_items lama harus tetap ada", cursor.moveToFirst())
            assertEquals(3.0, cursor.getDouble(0), 0.0001)
        }
        migrated.close()
    }
}