package com.sentral.org.domain.usecase

import com.sentral.org.data.entity.ProdukEntity
import com.sentral.org.data.repository.ProdukRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchProductUseCase(
    private val produkRepository: ProdukRepository,
) {
    /**
     * Returns a filtered Flow of active products matching the query.
     * Query matches against name, sku, or barcode (case-insensitive).
     * Empty query returns all active products.
     */
    fun observeFiltered(query: String, category: String?): Flow<List<ProdukEntity>> {
        return produkRepository.observeAktif().map { products ->
            products.filter { product ->
                val matchesQuery = query.isBlank() ||
                    product.nama.contains(query, ignoreCase = true) ||
                    product.sku.contains(query, ignoreCase = true) ||
                    (product.barcode?.contains(query, ignoreCase = true) == true)

                val matchesCategory = category == null || product.kategori == category

                matchesQuery && matchesCategory
            }
        }
    }

    fun observeCategories(): Flow<List<String>> {
        return produkRepository.observeAktif().map { products ->
            products.map { it.kategori }.distinct().sorted()
        }
    }
}
