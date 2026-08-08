package com.pos.offline.util

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class ImageFeatureExtractor(context: Context) {
    private var interpreter: Interpreter? = null
    private val IMAGE_SIZE = 224
    private var OUTPUT_SIZE = 1000

    // PRE-ALLOCATE MEMORY: Dibuat 1x, dipakai berkali-kali untuk mencegah Memory Churn
    private val byteBuffer = ByteBuffer.allocateDirect(4 * IMAGE_SIZE * IMAGE_SIZE * 3).apply {
        order(ByteOrder.nativeOrder())
    }
    private val intValues = IntArray(IMAGE_SIZE * IMAGE_SIZE)
    private var outputArray = Array(1) { FloatArray(OUTPUT_SIZE) }

    init {
        // MENCEGAH RESOURCE LEAK: Gunakan blok 'use' agar otomatis ter-close setelah dibaca
        context.assets.openFd("mobilenetv4_conv_small.tflite").use { assetFileDescriptor ->
            FileInputStream(assetFileDescriptor.fileDescriptor).use { fileInputStream ->
                val fileChannel = fileInputStream.channel
                val startOffset = assetFileDescriptor.startOffset
                val declaredLength = assetFileDescriptor.declaredLength
                val mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
                
                val options = Interpreter.Options().apply { numThreads = 2 }
                interpreter = Interpreter(mappedByteBuffer, options)
                
                val outputShape = interpreter?.getOutputTensor(0)?.shape()
                if (outputShape != null && outputShape.size >= 2) {
                    OUTPUT_SIZE = outputShape[1]
                    outputArray = Array(1) { FloatArray(OUTPUT_SIZE) } // Update array pre-allocated
                }
            }
        }
    }

fun extractFeatures(bitmap: Bitmap): FloatArray {
    var softwareBitmap: Bitmap? = null
    var resizedBitmap: Bitmap? = null

    try {
        // 1. Konversi paksa ke ARGB_8888 jika bitmap berupa Hardware
        softwareBitmap = if (bitmap.config == Bitmap.Config.HARDWARE) {
            bitmap.copy(Bitmap.Config.ARGB_8888, true)
        } else {
            bitmap
        }

        // 2. Resize ke 224x224
        resizedBitmap = if (softwareBitmap.width != IMAGE_SIZE || softwareBitmap.height != IMAGE_SIZE) {
            Bitmap.createScaledBitmap(softwareBitmap, IMAGE_SIZE, IMAGE_SIZE, true)
        } else {
            softwareBitmap
        }
        
        convertBitmapToByteBuffer(resizedBitmap)
        interpreter?.run(byteBuffer, outputArray)
        
        val rawVector = outputArray[0]
        
        // 3. L2 NORMALIZATION
        var sum = 0.0f
        for (v in rawVector) {
            sum += v * v
        }
        val magnitude = kotlin.math.sqrt(sum.toDouble()).toFloat()
        
        if (magnitude > 0) {
            for (i in rawVector.indices) {
                rawVector[i] = rawVector[i] / magnitude
            }
        }
        
        return rawVector.clone()
    } finally {
        // --- MENCEGAH MEMORY LEAK ---
        // Hapus bitmap sementara dari memori jika dibuat secara baru
        if (softwareBitmap != null && softwareBitmap != bitmap) {
            softwareBitmap.recycle()
        }
        if (resizedBitmap != null && resizedBitmap != softwareBitmap && resizedBitmap != bitmap) {
            resizedBitmap.recycle()
        }
    }
}

    private fun convertBitmapToByteBuffer(bitmap: Bitmap) {
        byteBuffer.rewind() // Wajib! Kembalikan kursor buffer ke index 0
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        
        var pixel = 0
        for (i in 0 until IMAGE_SIZE) {
            for (j in 0 until IMAGE_SIZE) {
                val valInt = intValues[pixel++]
                
                val r = ((valInt shr 16) and 0xFF)
                val g = ((valInt shr 8) and 0xFF)
                val b = (valInt and 0xFF)
                
                byteBuffer.putFloat((r / 127.5f) - 1.0f)
                byteBuffer.putFloat((g / 127.5f) - 1.0f)
                byteBuffer.putFloat((b / 127.5f) - 1.0f)
            }
        }
        
        // --- TAMBAHKAN BARIS INI ---
        // Putar kembali kursor ke awal agar TFLite membaca dari pixel pertama
        byteBuffer.rewind() 
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }
}