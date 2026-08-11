package com.pos.offline.util
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.roundToLong

private val rupiahFormatter =
    object : ThreadLocal<DecimalFormat>() {
        override fun initialValue(): DecimalFormat =
            DecimalFormat(
                "#,###",
                DecimalFormatSymbols(Locale.forLanguageTag("id-ID")),
            ).apply {
                maximumFractionDigits = 0
                minimumFractionDigits = 0
            }
    }

fun Long.toRupiah(): String = "Rp " + rupiahFormatter.get()!!.format(this)

fun Double.roundToRupiah(): Long = this.roundToLong()

fun Double.formatQuantity(): String {
    val isWholeNumber = this % 1.0 == 0.0
    return if (isWholeNumber) this.toLong().toString() else this.toString()
}
