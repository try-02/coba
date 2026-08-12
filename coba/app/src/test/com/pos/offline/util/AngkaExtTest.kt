package com.pos.offline.utils

import org.junit.Assert.assertEquals
import org.junit.Test

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
}