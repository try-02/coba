package com.pos.offline.util
import android.content.Context
import com.pos.offline.util.AppLogger
import android.net.Uri
import com.pos.offline.data.local.entity.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import org.dhatim.fastexcel.BorderSide
import org.dhatim.fastexcel.BorderStyle
import org.dhatim.fastexcel.Workbook
import org.dhatim.fastexcel.Worksheet
import org.dhatim.fastexcel.reader.ReadableWorkbook
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.coroutines.coroutineContext
import kotlin.math.round

sealed class ExcelOutcome {
    object Success : ExcelOutcome()

    data class Error(
        val throwable: Throwable,
    ) : ExcelOutcome()
}

data class ImportedProductRow(
    val sku: String,
    val barcode: String?,
    val name: String,
    val category: String?,
    val price: Long,
    val cost: Long,
    val stock: Double,
)

data class ExcelImportResult(
    val rows: List<ImportedProductRow>,
    val errors: List<String>,
)

object ExcelManager {
    private const val MAX_IMPORT_ROWS = 50_000
    private const val MAX_IMPORT_FILE_SIZE = 50L * 1024 * 1024
    private val HEADERS =
        listOf(
            "No",
            "SKU",
            "Barcode",
            "Nama Produk",
            "Kategori",
            "Harga Jual",
            "Modal",
            "Stok",
            "Margin",
            "Nilai Stok",
        )
    private val COLUMN_WIDTHS =
        listOf(
            6.0,
            15.0,
            18.0,
            32.0,
            18.0,
            18.0,
            18.0,
            12.0,
            14.0,
            20.0,
        )
    private const val REQUIRED_IMPORT_COLUMNS = 7
    private const val COLOR_HEADER_BG = "1F4E79"
    private const val COLOR_HEADER_TEXT = "FFFFFF"
    private const val COLOR_TITLE_BG = "2E75B6"
    private const val COLOR_TITLE_TEXT = "FFFFFF"
    private const val COLOR_ROW_EVEN = "F2F7FC"
    private const val COLOR_ROW_ODD = "FFFFFF"
    private const val COLOR_BORDER = "B4C6E7"
    private const val COLOR_SUMMARY_BG = "D6E4F0"
    private const val COLOR_NEGATIVE = "FF0000"
    private const val COLOR_POSITIVE = "006100"

    private val TOTAL_ROW_REGEX =
    Regex("""total\s*\(\s*\d+\s*produk\s*\)""", RegexOption.IGNORE_CASE)

    fun suggestedExportFileName(): String = "produk_${System.currentTimeMillis()}.xlsx"

suspend fun exportProducts(
    context: Context,
    products: List<ProductEntity>,
    destinationUri: Uri,
): ExcelOutcome =
    withContext(Dispatchers.IO) {
        var outputStream: OutputStream? = null

        try {
            AppLogger.measure(
                AppLogger.TAG_IO,
                "1. Export: Open Output Stream SAF",
            ) {
                outputStream =
                    context.contentResolver.openOutputStream(destinationUri)
            }

            if (outputStream == null) {
                return@withContext ExcelOutcome.Error(
                    IOException("Tidak bisa membuka output stream"),
                )
            }

            writeWorkbook(outputStream!!, products)

            ExcelOutcome.Success
        } catch (e: Exception) {
            ExcelOutcome.Error(e)
        } finally {
            try {
                outputStream?.close()
            } catch (_: Exception) {
            }
        }
    }

private fun writeWorkbook(
    outputStream: OutputStream,
    products: List<ProductEntity>,
) {
    val wb = Workbook(outputStream, "POS Offline", "1.0")

    try {
        AppLogger.measure(
            AppLogger.TAG_IO,
            "2a. Export: Build & Style Rows (${products.size} rows)",
        ) {
            val ws = wb.newWorksheet("Produk")

            COLUMN_WIDTHS.forEachIndexed { col, width ->
                ws.width(col, width)
            }

            ws.freezePane(0, 2)

            writeTitleRow(ws, products.size)
            writeHeaderRow(ws)

            var totalPrice = 0L
            var totalCost = 0L
            var totalStock = 0.0
            var totalStockValue = 0.0

            products.forEachIndexed { idx, p ->
                val margin =
                    if (p.price > 0) {
                        p.price - p.cost
                    } else {
                        0L
                    }

                val stockValue = p.stock * p.cost

                totalPrice += p.price
                totalCost += p.cost
                totalStock += p.stock
                totalStockValue += stockValue

                writeDataRow(
                    ws = ws,
                    rowIndex = idx + 2,
                    number = idx + 1,
                    p = p,
                    margin = margin,
                    stockValue = stockValue,
                )
            }

            // Styling data dilakukan setelah seluruh value ditulis.
            styleDataRows(
                ws = ws,
                firstRow = 2,
                lastRow = products.size + 1,
            )

            val summaryRow = products.size + 2

            writeSummaryRow(
                ws,
                summaryRow,
                products.size,
                totalPrice,
                totalCost,
                totalStock,
                totalStockValue,
            )
        }

        AppLogger.measure(
            AppLogger.TAG_IO,
            "2b. Export: FastExcel Finish & Zip Deflate",
        ) {
            wb.finish()
        }
    } finally {
        try {
            wb.close()
        } catch (_: Exception) {
        }
    }
}

    private fun writeTitleRow(
        ws: Worksheet,
        productCount: Int,
    ) {
        ws.rowHeight(0, 30.0)
        val title = "Data Produk — Total: $productCount produk"
        ws.value(0, 0, title)
        for (col in HEADERS.indices) {
            ws
                .style(0, col)
                .bold()
                .fontSize(if (col == 0) 14 else 11)
                .fontColor(COLOR_TITLE_TEXT)
                .fillColor(COLOR_TITLE_BG)
                .horizontalAlignment(if (col == 0) "left" else "center")
                .verticalAlignment("center")
                .set()
        }
    }

    private fun writeHeaderRow(ws: Worksheet) {
        ws.rowHeight(1, 24.0)
        HEADERS.forEachIndexed { col, title ->
            ws.value(1, col, title)
            ws
                .style(1, col)
                .bold()
                .fontSize(11)
                .fontColor(COLOR_HEADER_TEXT)
                .fillColor(COLOR_HEADER_BG)
                .horizontalAlignment("center")
                .verticalAlignment("center")
                .borderStyle(BorderSide.BOTTOM, BorderStyle.MEDIUM)
                .borderColor(BorderSide.BOTTOM, COLOR_BORDER)
                .set()
        }
    }

private fun writeDataRow(
    ws: Worksheet,
    rowIndex: Int,
    number: Int,
    p: ProductEntity,
    margin: Long,
    stockValue: Double,
) {
    ws.rowHeight(rowIndex, 20.0)

    ws.value(rowIndex, 0, number)
    ws.value(rowIndex, 1, p.sku)
    ws.value(rowIndex, 2, p.barcode)
    ws.value(rowIndex, 3, p.name)
    ws.value(rowIndex, 4, p.category)
    ws.value(rowIndex, 5, p.price.toDouble())
    ws.value(rowIndex, 6, p.cost.toDouble())
    ws.value(rowIndex, 7, p.stock)
    ws.value(rowIndex, 8, margin.toDouble())
    ws.value(rowIndex, 9, stockValue)
}

private fun styleDataRows(
    ws: Worksheet,
    firstRow: Int,
    lastRow: Int,
) {
    if (lastRow < firstRow) return

    // ---------------------------------------------------------
    // Base style seluruh data area A:J
    // ---------------------------------------------------------
    ws
        .range(firstRow, 0, lastRow, 9)
        .style()
        .fontSize(10)
        .verticalAlignment("center")
        .borderStyle(BorderSide.BOTTOM, BorderStyle.THIN)
        .borderColor(BorderSide.BOTTOM, COLOR_BORDER)
        .set()

    // ---------------------------------------------------------
    // Zebra rows
    // ---------------------------------------------------------
    ws
        .range(firstRow, 0, lastRow, 9)
        .style()
        .shadeAlternateRows(COLOR_ROW_EVEN)
        .set()

    // ---------------------------------------------------------
    // Column A - No
    // ---------------------------------------------------------
    ws
        .range(firstRow, 0, lastRow, 0)
        .style()
        .horizontalAlignment("center")
        .set()

    // ---------------------------------------------------------
    // Column D - Nama Produk
    // ---------------------------------------------------------
    ws
        .range(firstRow, 3, lastRow, 3)
        .style()
        .bold()
        .set()

    // ---------------------------------------------------------
    // Column F:G - Harga Jual & Modal
    // ---------------------------------------------------------
    ws
        .range(firstRow, 5, lastRow, 6)
        .style()
        .format("#,##0")
        .horizontalAlignment("right")
        .set()

    // ---------------------------------------------------------
    // Column H - Stok
    // ---------------------------------------------------------
    ws
        .range(firstRow, 7, lastRow, 7)
        .style()
        .format("#,##0.##")
        .horizontalAlignment("right")
        .set()

    // ---------------------------------------------------------
    // Column J - Nilai Stok
    // ---------------------------------------------------------
    ws
        .range(firstRow, 9, lastRow, 9)
        .style()
        .format("#,##0")
        .horizontalAlignment("right")
        .set()

    // ---------------------------------------------------------
    // Column I - Margin
    //
    // Base style terlebih dahulu.
    // Warna positif/negatif akan diterapkan per cell
    // karena nilainya berbeda-beda.
    // ---------------------------------------------------------
    ws
        .range(firstRow, 8, lastRow, 8)
        .style()
        .bold()
        .format("#,##0")
        .horizontalAlignment("right")
        .set()
}

    private fun writeSummaryRow(
        ws: Worksheet,
        rowIndex: Int,
        productCount: Int,
        totalPrice: Long,
        totalCost: Long,
        totalStock: Double,
        totalStockValue: Double,
    ) {
        val totalMargin = totalPrice - totalCost
        val marginColor = if (totalMargin >= 0) COLOR_POSITIVE else COLOR_NEGATIVE
        ws.rowHeight(rowIndex, 26.0)
        for (col in 0..2) {
            ws
                .style(rowIndex, col)
                .fillColor(COLOR_SUMMARY_BG)
                .borderStyle(BorderSide.TOP, BorderStyle.DOUBLE)
                .borderColor(BorderSide.TOP, COLOR_HEADER_BG)
                .set()
        }
        ws.value(rowIndex, 3, "TOTAL ($productCount produk)")
        ws
            .style(rowIndex, 3)
            .bold()
            .fontSize(11)
            .fillColor(COLOR_SUMMARY_BG)
            .horizontalAlignment("right")
            .verticalAlignment("center")
            .borderStyle(BorderSide.TOP, BorderStyle.DOUBLE)
            .borderColor(BorderSide.TOP, COLOR_HEADER_BG)
            .set()
        ws
            .style(rowIndex, 4)
            .fillColor(COLOR_SUMMARY_BG)
            .borderStyle(BorderSide.TOP, BorderStyle.DOUBLE)
            .borderColor(BorderSide.TOP, COLOR_HEADER_BG)
            .set()
        ws.value(rowIndex, 5, totalPrice.toDouble())
        applySummaryNumberStyle(ws, rowIndex, 5, "#,##0")
        ws.value(rowIndex, 6, totalCost.toDouble())
        applySummaryNumberStyle(ws, rowIndex, 6, "#,##0")
        ws.value(rowIndex, 7, totalStock)
        applySummaryNumberStyle(ws, rowIndex, 7, "#,##0.##")
        ws.value(rowIndex, 8, totalMargin.toDouble())
        ws
            .style(rowIndex, 8)
            .bold()
            .fontSize(11)
            .fillColor(COLOR_SUMMARY_BG)
            .fontColor(marginColor)
            .format("#,##0")
            .horizontalAlignment("right")
            .verticalAlignment("center")
            .borderStyle(BorderSide.TOP, BorderStyle.DOUBLE)
            .borderColor(BorderSide.TOP, COLOR_HEADER_BG)
            .set()
        ws.value(rowIndex, 9, totalStockValue)
        applySummaryNumberStyle(ws, rowIndex, 9, "#,##0")
    }

    private fun applySummaryNumberStyle(
        ws: Worksheet,
        row: Int,
        col: Int,
        numberFormat: String,
    ) {
        ws
            .style(row, col)
            .bold()
            .fontSize(11)
            .fillColor(COLOR_SUMMARY_BG)
            .format(numberFormat)
            .horizontalAlignment("right")
            .verticalAlignment("center")
            .borderStyle(BorderSide.TOP, BorderStyle.DOUBLE)
            .borderColor(BorderSide.TOP, COLOR_HEADER_BG)
            .set()
    }

suspend fun importProducts(
    context: Context,
    sourceUri: Uri,
): ExcelImportResult =
    withContext(Dispatchers.IO) {
        var inputStream: InputStream? = null

        try {
            val fileSize = getFileSize(context, sourceUri)

            if (fileSize > MAX_IMPORT_FILE_SIZE) {
                val maxMb = MAX_IMPORT_FILE_SIZE / (1024 * 1024)

                return@withContext ExcelImportResult(
                    emptyList(),
                    listOf("File terlalu besar (maks $maxMb MB)"),
                )
            }

            AppLogger.measure(
                AppLogger.TAG_IO,
                "1. Import: Open Input Stream SAF",
            ) {
                inputStream =
                    context.contentResolver.openInputStream(sourceUri)
            }

            if (inputStream == null) {
                return@withContext ExcelImportResult(
                    emptyList(),
                    listOf("File tidak bisa dibuka"),
                )
            }

            readWorkbook(inputStream!!)
        } catch (e: Exception) {
            ExcelImportResult(
                emptyList(),
                listOf("Error membaca file: ${e.message}"),
            )
        } finally {
            try {
                inputStream?.close()
            } catch (_: Exception) {
            }
        }
    }

private suspend fun readWorkbook(
    inputStream: InputStream,
): ExcelImportResult {
    val rows = ArrayList<ImportedProductRow>(256)
    val errors = ArrayList<String>(16)

    val wb =
        AppLogger.measure(
            AppLogger.TAG_IO,
            "2a. Import: Open Zip & Load Workbook Metadata",
        ) {
            ReadableWorkbook(inputStream)
        }

    wb.use {
        val sheet = wb.firstSheet

        AppLogger.measureSuspend(
            AppLogger.TAG_IO,
            "2b. Import: Stream XML & Parse Rows",
        ) {
            sheet.openStream().use { rowStream ->
                val iterator = rowStream.iterator()

                var rowIndex = 0
                var skipOffset = 0
                var headerFound = false

                while (iterator.hasNext()) {
                    coroutineContext.ensureActive()

                    val row = iterator.next()

                    if (!headerFound) {
                        val detection = detectHeader(row)

                        if (detection != null) {
                            headerFound = true
                            skipOffset = detection
                        }

                        rowIndex++
                        continue
                    }

                    if (rows.size >= MAX_IMPORT_ROWS) {
                        errors.add(
                            "Import dibatasi $MAX_IMPORT_ROWS baris. " +
                                "Sisa baris diabaikan.",
                        )
                        break
                    }

                    parseRow(
                        row = row,
                        rowIndex = rowIndex,
                        skipOffset = skipOffset,
                    )?.let { result ->
                        result.first?.let(rows::add)
                        result.second?.let(errors::add)
                    }

                    rowIndex++
                }

                if (!headerFound) {
                    return@measureSuspend ExcelImportResult(
                        emptyList(),
                        listOf(
                            "Header tidak ditemukan. " +
                                "File harus memiliki kolom: SKU, Barcode, Nama, " +
                                "Kategori, Harga Jual, Modal, Stok",
                        ),
                    )
                }
            }
        }
    }

    rows.trimToSize()

    return ExcelImportResult(
        rows = rows,
        errors = errors,
    )
}

    private fun detectHeader(row: org.dhatim.fastexcel.reader.Row): Int? {
        if (row.cellCount < REQUIRED_IMPORT_COLUMNS) return null

        fun cell(c: Int): String = getSafeCellString(row, c).lowercase()
        val cell0 = cell(0)
        val cell1 = cell(1)
        val cell2 = cell(2)
        if ((cell0 == "no" || cell0 == "no.") && cell1.contains("sku")) {
            return 1
        }
        if (cell0.contains("sku") &&
            (cell2.contains("nama") || cell2.contains("name"))
        ) {
            return 0
        }
        val allCells = (0 until minOf(row.cellCount, 10)).map { cell(it) }
        val headerKeywords = listOf("sku", "nama", "harga", "modal", "stok")
        val matchCount =
            headerKeywords.count { keyword ->
                allCells.any { it.contains(keyword) }
            }
        if (matchCount >= 3) {
            return if (cell0 == "no" || cell0 == "no." ||
                cell0.contains("nomor") || cell0 == "#"
            ) {
                1
            } else {
                0
            }
        }
        return null
    }

private fun parseRow(
    row: org.dhatim.fastexcel.reader.Row,
    rowIndex: Int,
    skipOffset: Int,
): Pair<ImportedProductRow?, String?>? {

    val base = skipOffset

    // Ambil 7 cell yang diperlukan SEKALI saja.
    val sku = getSafeCellString(row, base)
    val barcode = getSafeCellString(row, base + 1)
    val name = getSafeCellString(row, base + 2)
    val category = getSafeCellString(row, base + 3)
    val price = getSafeCellString(row, base + 4)
    val cost = getSafeCellString(row, base + 5)
    val stock = getSafeCellString(row, base + 6)

    // Abaikan baris kosong.
    if (
        sku.isBlank() &&
        barcode.isBlank() &&
        name.isBlank() &&
        category.isBlank() &&
        price.isBlank() &&
        cost.isBlank() &&
        stock.isBlank()
    ) {
        return null
    }

    // Abaikan summary row.
    val skuLower = sku.lowercase()
    val nameLower = name.lowercase()

    if (
        (skuLower.isBlank() && TOTAL_ROW_REGEX.containsMatchIn(nameLower)) ||
        skuLower == "total" ||
        skuLower == "jumlah"
    ) {
        return null
    }

    return try {
        if (sku.isBlank()) {
            throw IllegalArgumentException("SKU kosong")
        }

        if (name.isBlank()) {
            throw IllegalArgumentException("Nama kosong")
        }

        val imported =
            ImportedProductRow(
                sku = sku,
                barcode = barcode.ifBlank { null },
                name = name,
                category = category.ifBlank { null },
                price = parseCurrency(price, "Harga"),
                cost = parseCurrency(cost, "Modal"),
                stock = parseQty(stock, "Stok"),
            )

        Pair(imported, null)
    } catch (e: Exception) {
        Pair(
            null,
            "Baris ${rowIndex + 1}: ${e.message}",
        )
    }
}

    internal fun parseQtyForTest(s: String, field: String = "Qty"): Double {
        return parseQty(s, field)
    }

    internal fun parseCurrencyForTest(s: String, field: String = "Harga"): Long {
        return parseCurrency(s, field)
    }

    private fun parseCurrency(
        s: String,
        field: String,
    ): Long {
        val value =
            parseFlexibleNumber(s, treatSingleSeparatorAsThousands = true)
                ?: error("$field tidak valid: \"$s\"")
        require(value >= 0) { "$field bernilai negatif" }
        return round(value).toLong()
    }

    private fun parseQty(
        s: String,
        field: String,
    ): Double {
        val value =
            parseFlexibleNumber(s, treatSingleSeparatorAsThousands = false)
                ?: error("$field tidak valid: \"$s\"")
        require(value >= 0) { "$field bernilai negatif" }
        return value
    }

private fun parseFlexibleNumber(
    raw: String,
    treatSingleSeparatorAsThousands: Boolean,
): Double? {
    val trimmed = raw.trim()

    if (trimmed.isEmpty()) return null

    var start = 0
    var negative = false

    if (trimmed[0] == '-') {
        negative = true
        start = 1
    }

    if (start >= trimmed.length) return null

    val body = trimmed.substring(start)

    var hasDot = false
    var hasComma = false
    var dotCount = 0
    var commaCount = 0
    var lastDot = -1
    var lastComma = -1

    for (i in body.indices) {
        when (body[i]) {
            '.' -> {
                hasDot = true
                dotCount++
                lastDot = i
            }

            ',' -> {
                hasComma = true
                commaCount++
                lastComma = i
            }

            in '0'..'9' -> Unit

            else -> return null
        }
    }

    if (!hasDot && !hasComma) {
        val value = body.toDoubleOrNull() ?: return null
        return if (negative) -value else value
    }

    val normalized =
        when {
            hasDot && hasComma -> {
                if (lastComma > lastDot) {
                    body.replace(".", "").replace(',', '.')
                } else {
                    body.replace(",", "")
                }
            }

            hasDot -> {
                val digitsAfter = body.length - lastDot - 1

                if (
                    dotCount > 1 ||
                    (treatSingleSeparatorAsThousands && digitsAfter == 3)
                ) {
                    body.replace(".", "")
                } else {
                    body
                }
            }

            else -> {
                val digitsAfter = body.length - lastComma - 1

                if (
                    commaCount > 1 ||
                    (treatSingleSeparatorAsThousands && digitsAfter == 3)
                ) {
                    body.replace(",", "")
                } else {
                    body.replace(',', '.')
                }
            }
        }

    val value = normalized.toDoubleOrNull() ?: return null

    return if (negative) -value else value
}

    private fun getFileSize(
        context: Context,
        uri: Uri,
    ): Long =
        try {
            context.contentResolver
                .openAssetFileDescriptor(uri, "r")
                ?.use { it.length } ?: -1L
        } catch (_: Exception) {
            -1L
        }

    private fun getSafeCellString(
        row: org.dhatim.fastexcel.reader.Row,
        col: Int,
    ): String {
        if (col < 0 || col >= row.cellCount) return ""
        val cell = row.getCell(col) ?: return ""
        val textValue = cell.text ?: cell.value?.toString() ?: ""
        return if (textValue.endsWith(".0")) {
            textValue.dropLast(2).trim()
        } else {
            textValue.trim()
        }
    }
}
