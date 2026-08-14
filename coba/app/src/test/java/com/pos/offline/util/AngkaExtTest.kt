package com.pos.offline.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import com.pos.offline.util.formatQuantity
import com.pos.offline.util.roundToQuantityPrecision
import com.pos.offline.ui.components.formatPercentTrim

class NumberExtTest {

    @Test
    fun testFormatPercentTrim() {
        // Menguji pembulatan 4 angka di belakang koma (7.000000000000001 di-format "%.4f" jadi "7.0000" -> di-trim jadi "7")
        assertEquals("7", formatPercentTrim(7.000000000000001))
    }

    @Test
    fun testFormatQuantity() {
        // Menguji bilangan bulat
        assertEquals("6", 6.0.formatQuantity())

        // Menguji bilangan desimal
        assertEquals("17.5", 17.5.formatQuantity())
    }

@Test
fun testRoundToQuantityPrecision_hilangkanDriftStepperBerulang() {
    var qty = 1.0
    repeat(50) { qty = (qty + 0.1).roundToQuantityPrecision() }
    assertEquals(6.0, qty, 0.0) // 1.0 + 50x0.1 -- persis, tanpa toleransi delta
}

@Test
fun testRoundToQuantityPrecision_kombinasiTambahKurang() {
    var qty = 1.0
    val taps = listOf(0.1, 0.1, 0.1, -0.1, 0.1, 0.1, -0.1, 0.1, 0.1, 0.1) // net +0.6
    taps.forEach { delta -> qty = (qty + delta).roundToQuantityPrecision() }
    assertEquals(1.6, qty, 0.0)
}
}