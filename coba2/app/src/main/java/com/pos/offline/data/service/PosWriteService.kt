package com.pos.offline.data.service

import androidx.room3.withWriteTransaction
import com.pos.offline.data.PosDatabase

interface TransactionRunner {
    suspend fun <T> run(block: suspend () -> T): T
}

// RoomTransactionRunner.kt (Implementasi di Data Layer)
class RoomTransactionRunner(private val database: PosDatabase) : TransactionRunner {
    override suspend fun <T> run(block: suspend () -> T): T = 
        withWriteTransaction(database) { block() }
}