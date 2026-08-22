package com.pos.offline.data.repository.impl

import com.pos.offline.data.dao.PrinterDao
import com.pos.offline.data.repository.PrinterRepository

class OfflinePrinterRepository(private val dao: PrinterDao) : PrinterRepository {
    override fun observeAll() = dao.observeAll()
    override suspend fun getDefault() = dao.getDefault()
}
