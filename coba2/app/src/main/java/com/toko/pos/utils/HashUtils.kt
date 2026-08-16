package com.toko.pos.utils

import java.security.MessageDigest

fun sha256(input: String): String {
    return MessageDigest.getInstance("SHA-256")
        .digest(input.toByteArray())
        .joinToString("") { "%02x".format(it) }
}