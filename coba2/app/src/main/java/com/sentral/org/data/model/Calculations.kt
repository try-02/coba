package com.sentral.org.data.model

const val QUANTITY_SCALE = 1000L

/**
 * Kebijakan pembulatan uang: HALF_UP konsisten untuk SEMUA pembagi
 * (skala quantity, persentase, dan alokasi proporsional).
 * Overflow dijaga oleh multiplyExact/addExact, bukan silent-wrap.
 */
object MoneyMath {

    fun lineTotal(unitPrice: Long, quantityScaled: Long): Long {
        require(unitPrice >= 0)
        require(quantityScaled > 0)
        return divideHalfUp(Math.multiplyExact(unitPrice, quantityScaled), QUANTITY_SCALE)
    }

    fun percentage(value: Long, scaledPercent: Long): Long {
        require(value >= 0)
        require(scaledPercent in 0..100_000)
        return divideHalfUp(Math.multiplyExact(value, scaledPercent), 100_000L)
    }

    fun proportional(part: Long, total: Long, amount: Long): Long {
        require(part >= 0 && total > 0 && amount >= 0)
        return divideHalfUp(Math.multiplyExact(part, amount), total)
    }

    fun sumExact(values: Iterable<Long>): Long = values.fold(0L, Math::addExact)

    // Caller menjamin dividend >= 0, sehingga formula ini aman.
    private fun divideHalfUp(dividend: Long, divisor: Long): Long =
        (dividend + divisor / 2) / divisor
}