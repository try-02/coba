package com.toko.pos.printer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import java.io.OutputStream
import java.util.UUID

class PrinterManager {
    fun print(transactionText: String, deviceAddress: String) {
        val adapter = BluetoothAdapter.getDefaultAdapter() ?: return
        val device = adapter.getRemoteDevice(deviceAddress)
        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        val socket: BluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
        socket.connect()
        val outputStream: OutputStream = socket.outputStream
        outputStream.write(transactionText.toByteArray(Charsets.UTF_8))
        outputStream.flush()
        socket.close()
    }
}