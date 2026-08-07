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
    
    // Input untuk MobileNet-V4 sesuai spesifikasi Anda
    private val IMAGE_SIZE = 224 
    
    // Kita buat dinamis agar tidak hardcoded
    private var OUTPUT_SIZE = 1000 

    init {
        // Ganti nama file sesuai dengan file .tflite Anda di folder assets
        val assetFileDescriptor = context.assets.openFd("mobilenetv4_conv_small.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        val mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        
        val options = Interpreter.Options().apply {
            numThreads = 2 
        }
        interpreter = Interpreter(mappedByteBuffer, options)
        
        // Membaca ukuran output otomatis dari model (biasanya shape [1, 1000] atau [1, 1024])
        val outputShape = interpreter?.getOutputTensor(0)?.shape()
        if (outputShape != null && outputShape.size >= 2) {
            OUTPUT_SIZE = outputShape[1]
        }
    }

    fun extractFeatures(bitmap: Bitmap): FloatArray {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true)
        val byteBuffer = convertBitmapToByteBuffer(resizedBitmap)
        
        // Menyiapkan wadah output dengan ukuran dinamis
        val output = Array(1) { FloatArray(OUTPUT_SIZE) }
        
        interpreter?.run(byteBuffer, output)
        
        return output[0]
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        // Model timm ImageNet biasanya menggunakan normalisasi RGB standar (mean/std)
        // Namun untuk feature matching sederhana, normalisasi 0-1 seringkali sudah cukup.
        val byteBuffer = ByteBuffer.allocateDirect(4 * IMAGE_SIZE * IMAGE_SIZE * 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        
        val intValues = IntArray(IMAGE_SIZE * IMAGE_SIZE)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        
        var pixel = 0
        for (i in 0 until IMAGE_SIZE) {
            for (j in 0 until IMAGE_SIZE) {
                val valInt = intValues[pixel++]
                // Normalisasi (0.0 - 1.0)
                byteBuffer.putFloat(((valInt shr 16) and 0xFF) / 255.0f)
                byteBuffer.putFloat(((valInt shr 8) and 0xFF) / 255.0f)
                byteBuffer.putFloat((valInt and 0xFF) / 255.0f)
            }
        }
        return byteBuffer
    }
    
    fun close() {
        interpreter?.close()
        interpreter = null
    }
}
