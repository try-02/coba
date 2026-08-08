package com.pos.offline.util

import kotlin.math.sqrt

object VectorUtils {
    /**
     * Mengubah FloatArray (hasil dari AI) menjadi String (untuk disimpan di Room DB).
     * Contoh hasil: "0.123,-0.456,0.789..."
     */
    fun FloatArray.toVectorString(): String {
        return this.joinToString(",")
    }

    /**
     * Mengubah String dari Room DB kembali menjadi FloatArray untuk dikomparasi.
     */
    fun String.toVectorFloatArray(): FloatArray {
        if (this.isBlank()) return FloatArray(0)
        return this.split(",").mapNotNull { it.toFloatOrNull() }.toFloatArray()
    }

    /**
     * Menghitung tingkat kemiripan (Cosine Similarity) antara 2 objek visual.
     * Hasil: 1.0 (Identik/Sama Persis) hingga -1.0 (Sangat Berbeda).
     */
    fun calculateCosineSimilarity(vectorA: FloatArray, vectorB: FloatArray): Float {
        if (vectorA.isEmpty() || vectorB.isEmpty() || vectorA.size != vectorB.size) return 0f

        var dotProduct = 0.0
        var normA = 0.0
        var normB = 0.0

        for (i in vectorA.indices) {
            dotProduct += vectorA[i] * vectorB[i]
            normA += vectorA[i] * vectorA[i]
            normB += vectorB[i] * vectorB[i]
        }

        return if (normA == 0.0 || normB == 0.0) 0f 
        else (dotProduct / (sqrt(normA) * sqrt(normB))).toFloat()
    }
}
