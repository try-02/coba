package com.pos.offline.data.model

object MoneyMath {
    fun lineTotal(unitPrice: Long, quantityScaled: Long): Long {
        require(unitPrice >= 0)
        require(quantityScaled > 0)
        return Math.floorDiv(Math.multiplyExact(unitPrice, quantityScaled), QUANTITY_SCALE)
    }

fun percentage(value: Long, scaledPercent: Long): Long {
    require(value >= 0)
    require(scaledPercent in 0..100_000)
    val dividend = Math.multiplyExact(value, scaledPercent)
    val divisor = 100_000L
    // Pembulatan Half-Up standar finansial tanpa float
    return (dividend + (divisor / 2)) / divisor 
}
fun proportional(part: Long, total: Long, amount: Long): Long {
    require(part >= 0 && total > 0 && amount >= 0)
    val dividend = Math.multiplyExact(part, amount)
    // Pembulatan Half-Up proporsional
    return (dividend + (total / 2)) / total 
}

    fun sumExact(values: Iterable<Long>): Long = values.fold(0L, Math::addExact)
}
