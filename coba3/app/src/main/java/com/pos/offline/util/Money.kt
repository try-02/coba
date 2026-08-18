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

fun Double.roundToQuantityPrecision(): Double = kotlin.math.round(this * 1000.0) / 1000.0

internal fun Double.formatQuantity(): String {
    val text = String.format(java.util.Locale.ROOT, "%.3f", this)
    return if (text.contains('.')) text.trimEnd('0').trimEnd('.') else text
}
