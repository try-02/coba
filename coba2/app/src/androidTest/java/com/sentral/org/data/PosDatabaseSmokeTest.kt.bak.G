package com.sentral.org.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sentral.org.data.entity.PersediaanEntity
import com.sentral.org.data.entity.ProdukEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PosDatabaseSmokeTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val db = PosDatabaseFactory.create(context)

    @After
    fun close() = db.close()

    @Test
    fun databaseCreatesAndPersistsBasicInventory() = runBlocking {
        val now = 1_700_000_000_000L
        val productId = db.produkDao().insert(
            ProdukEntity(
                nama = "Uji",
                sku = "TEST-001",
                barcode = null,
                harga = 30_000,
                hargaModal = 20_000,
                kategori = "",
                aktif = true,
                dibuatPada = now,
                diperbaruiPada = now,
            ),
        )
        db.persediaanDao().insert(PersediaanEntity(productId, 1_200, 0, now))
        val stock = db.persediaanDao().getByProdukId(productId)
        assertNotNull(stock)
        assertEquals(1_200L, stock!!.jumlah)
    }

    @Test
    fun normalStockMayBecomeNegative() = runBlocking {
        val now = 1_700_000_000_000L
        val productId = db.produkDao().insert(
            ProdukEntity("Neg", "NEG-001", null, 10_000, 5_000, "", true, now, now),
        )
        db.persediaanDao().insert(PersediaanEntity(productId, 0, 0, now))
        assertEquals(1, db.persediaanDao().addNormal(productId, -1_500, now))
        assertEquals(-1_500L, db.persediaanDao().getByProdukId(productId)!!.jumlah)
    }
}
