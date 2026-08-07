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
        // Menghemat 1 pembuatan bitmap (Bitmap.createScaledBitmap)
        val resizedBitmap = if (bitmap.width != IMAGE_SIZE || bitmap.height != IMAGE_SIZE) {
            Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true)
        } else {
            bitmap
        }
        
        convertBitmapToByteBuffer(resizedBitmap)
        interpreter?.run(byteBuffer, outputArray)
        
        // Kembalikan clone agar array asli tidak termutasi oleh kelas lain
        return outputArray[0].clone() 
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap) {
        byteBuffer.rewind() // Wajib! Kembalikan kursor buffer ke index 0
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        
        var pixel = 0
        for (i in 0 until IMAGE_SIZE) {
            for (j in 0 until IMAGE_SIZE) {
                val valInt = intValues[pixel++]
                byteBuffer.putFloat(((valInt shr 16) and 0xFF) / 255.0f)
                byteBuffer.putFloat(((valInt shr 8) and 0xFF) / 255.0f)
                byteBuffer.putFloat((valInt and 0xFF) / 255.0f)
            }
        }
    }
    
    fun close() {
        interpreter?.close()
        interpreter = null
    }
}