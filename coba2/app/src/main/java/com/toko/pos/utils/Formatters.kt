package com.toko.pos.utils

import java.text.NumberFormat
import java.util.*

fun formatRupiah(amount: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount)
}

fun formatDateTime(timestamp: Long): String {
    val formatter = java.text.SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}