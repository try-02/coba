package com.pos.offline.data

import com.pos.offline.data.model.MoneyMath
import org.junit.Assert.assertEquals
import org.junit.Test

class MoneyMathTest {
    @Test fun decimalQuantityProducesExactLineTotal() {
        assertEquals(36_000L, MoneyMath.lineTotal(30_000L, 1_200L))
        assertEquals(67_500L, MoneyMath.lineTotal(15_000L, 4_500L))
    }

    @Test fun percentageUsesIntegerMath() {
        // 12.5% = 12500 with denominator 100000.
        assertEquals(12_500L, MoneyMath.percentage(100_000L, 12_500L))
    }

    @Test fun proportionalReturnUsesHistoricalNetLineValue() {
        // Net line Rp90.000, sold quantity 10, return 2.5 => Rp22.500.
        assertEquals(22_500L, MoneyMath.proportional(2_500L, 10_000L, 90_000L))
    }
}
