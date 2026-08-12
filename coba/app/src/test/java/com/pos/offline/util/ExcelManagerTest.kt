package com.pos.offline.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import com.pos.offline.util.ExcelManager

class ExcelManagerTest {

    @Test
    fun testParseQtyAndCurrencyFormat() {
        // Test 1: Quantiti "12.500" dibaca sebagai 12.5 (Desimal)
        assertEquals(12.5, ExcelManager.parseQtyForTest("12.500"), 0.0001)

        // Test 2: Harga/Currency "12.500" dibaca sebagai 12500 (Ribuan/Long)
        assertEquals(12500L, ExcelManager.parseCurrencyForTest("12.500"))
    }
}