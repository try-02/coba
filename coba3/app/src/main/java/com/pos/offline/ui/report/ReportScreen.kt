package com.pos.offline.ui.report

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pos.offline.data.local.entity.PaymentMethod
import com.pos.offline.data.local.entity.ReturnEntity
import com.pos.offline.data.local.entity.ReturnItemEntity
import com.pos.offline.data.local.entity.ShiftEntity
import com.pos.offline.data.local.entity.TransactionEntity
import com.pos.offline.data.local.entity.hasReturn
import com.pos.offline.data.local.entity.isVoid
import com.pos.offline.data.repository.CheckoutResult
import com.pos.offline.data.repository.ReturnDetail
import com.pos.offline.data.repository.ReturnItemInput
import com.pos.offline.ui.components.GlassCard
import com.pos.offline.ui.components.ThousandsSeparatorTransformation
import com.pos.offline.ui.components.discountRowLabel
import com.pos.offline.ui.components.paymentMethodLabel
import com.pos.offline.ui.components.rememberBarcodeScanner
import com.pos.offline.ui.components.LocalGlobalMessage
import com.pos.offline.ui.inventory.InventoryViewModel
import com.pos.offline.ui.receipt.PrintUiState
import com.pos.offline.ui.receipt.ReceiptManager
import com.pos.offline.ui.receipt.forTransaction
import com.pos.offline.util.ReceiptPrintOutcome
import com.pos.offline.util.bouncyOverscroll
import com.pos.offline.util.formatQuantity
import com.pos.offline.util.iosGlideFlingBehavior
import com.pos.offline.util.toRupiah
import com.pos.offline.util.roundToQuantityPrecision
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt
import androidx.compose.material3.Switch
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.composed
import com.pos.offline.LocalActiveFocusBounds
import androidx.compose.ui.focus.FocusEventModifierNode
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: ReportViewModel,
    inventoryViewModel: InventoryViewModel,
    onNavigateToSettings: () -> Unit,
    onSharePdfFile: (File) -> Unit,
    onExportPdf: (CheckoutResult) -> Unit,
    onShare: (CheckoutResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    val report by viewModel.report.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val isToday by viewModel.isToday.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val selectedTransaction by viewModel.selectedTransaction.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val closedShifts by viewModel.closedShifts.collectAsStateWithLifecycle()
    val selectedShiftDetail by viewModel.selectedShiftDetail.collectAsStateWithLifecycle()
    val showReturnDialog by viewModel.showReturnDialog.collectAsStateWithLifecycle()
    val printUiState by viewModel.printUiState.collectAsStateWithLifecycle()
    val pendingPrintTarget by viewModel.pendingPrintTarget.collectAsStateWithLifecycle()
    val returnMessage by viewModel.returnMessage.collectAsStateWithLifecycle()
    val returnSubmitting by viewModel.returnSubmitting.collectAsStateWithLifecycle()
    val returnSummary by viewModel.returnSummary.collectAsStateWithLifecycle()
    val selectedReturnDetail by viewModel.selectedReturnDetail.collectAsStateWithLifecycle()
    var pendingVoidConfirm by remember { mutableStateOf(false) }
    var voidBanner by remember { mutableStateOf<ReportMessage?>(null) }
    val globalMessage = LocalGlobalMessage.current
    val currentSelectedTransaction by rememberUpdatedState(selectedTransaction)
    val productHistoryQuery by viewModel.productHistoryQuery.collectAsStateWithLifecycle()
    val productHistoryHierarchy by viewModel.productHistoryHierarchy.collectAsStateWithLifecycle()
    val searchUiState by viewModel.searchUiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    var showDirectWarrantyScreen by remember { mutableStateOf(false) }
    var warrantySearchQuery by remember { mutableStateOf("") }
    val warrantyScanner =
        rememberBarcodeScanner { scannedCode ->
            warrantySearchQuery = scannedCode
            scannedCode
        }
    val openScanner =
        rememberBarcodeScanner { scannedCode ->
            viewModel.searchProductHistory(scannedCode)
            viewModel.searchInvoice(scannedCode)
            scannedCode
        }
// Tetapi sekarang selectedTransaction yang digunakan selalu merupakan nilai terbaru.
LaunchedEffect(viewModel, globalMessage) {
    viewModel.messages.collect { msg ->
        if (currentSelectedTransaction != null) {
            voidBanner = msg
        } else {
            globalMessage.showMessage(msg.text)
        }
    }
}
    LaunchedEffect(voidBanner) {
        if (voidBanner != null) {
            delay(3000)
            voidBanner = null
        }
    }
    if (showDatePicker) {
        val datePickerState =
            rememberDatePickerState(
                initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                            viewModel.selectExactDate(localDate)
                        }
                        showDatePicker = false
                    },
                ) { Text("Pilih") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.AutoMirrored.Rounded.ReceiptLong,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Laporan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            },
            contentWindowInsets = WindowInsets.statusBars.union(WindowInsets.navigationBars),
        ) { inner ->
            @OptIn(ExperimentalFoundationApi::class)
            CompositionLocalProvider(
                LocalOverscrollFactory provides null,
            ) {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(inner)
                            .consumeWindowInsets(inner)
                            .bouncyOverscroll()
                            .imePadding(),
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    flingBehavior = iosGlideFlingBehavior(),
                ) {
                    item(key = "unified_search_actions") {
                        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                CompactReportSearchBar(
                                    query = productHistoryQuery,
                                    onQueryChange = { query ->
                                        viewModel.searchProductHistory(query)
                                        viewModel.searchInvoice(query)
                                    },
                                    modifier = Modifier.weight(1f).height(48.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                CompactSquareIconButton(
                                    icon = Icons.Rounded.QrCodeScanner,
                                    contentDescription = "Scan Barcode",
                                    onClick = { openScanner() },
                                )
                                Spacer(Modifier.width(8.dp))
                                CompactSquareIconButton(
                                    icon = Icons.Rounded.Build,
                                    contentDescription = "Klaim Garansi Direct",
                                    onClick = { showDirectWarrantyScreen = true },
                                    isError = true,
                                )
                            }
                        }
                    }
                    when (val state = searchUiState) {
                        is SearchUiState.ProductHistoryResults -> {
                            item(key = "product_history_header") {
                                Text(
                                    "Hasil Pencarian Produk · 1 Tahun",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                            items(
                                items = state.hierarchy,
                                key = { "month_${it.yearMonth}" },
                            ) { monthGroup ->
                                MonthGroupCard(
                                    monthGroup = monthGroup,
                                    onTransactionClick = { txId -> viewModel.openTransactionDetail(txId) },
                                )
                            }
                        }

                        is SearchUiState.InvoiceResults -> {
                            item(key = "invoice_search_header") {
                                Text(
                                    "Hasil Pencarian Struk (${state.transactions.size})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                            items(
                                items = state.transactions,
                                key = { "invoice_${it.id}" },
                            ) { tx ->
                                TransactionRow(
                                    tx = tx,
                                    onClick = { viewModel.openTransactionDetail(tx.id) },
                                )
                            }
                        }

                        is SearchUiState.Empty -> {
                            item(key = "search_empty_state") {
                                Text(
                                    "Data \"${state.query}\" tidak ditemukan dalam 365 hari terakhir.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(vertical = 12.dp),
                                )
                            }
                        }

                        SearchUiState.Loading -> {
                            item(key = "search_loading") {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                }
                            }
                        }

                        SearchUiState.Idle -> {
                            item(key = "date_navigator") {
                                DateNavigator(
                                    label = selectedDate.format(ReportViewModel.dateFmt),
                                    isToday = isToday,
                                    onPrevious = viewModel::previousDay,
                                    onNext = viewModel::nextDay,
                                    onToday = viewModel::goToday,
                                    onCalendarClick = { showDatePicker = true },
                                )
                            }
                            item(key = "summary") { SummarySection(report = report) }
                            if (report.transactionCount > 0) {
                                item(key = "revenue_trend_chart") {
                                    RevenueTrendChart(
                                        date = report.date,
                                        transactions = report.transactions.filterNot { it.isVoid },
                                        hourly = report.hourlyRevenue,
                                    )
                                }
                            }
                            item(key = "sales_report_generator") {
                                val periodType by viewModel.selectedPeriodType.collectAsStateWithLifecycle()
                                val includeSalesSummary by viewModel.includeSalesSummary.collectAsStateWithLifecycle()
                                val includeProductsSold by viewModel.includeProductsSold.collectAsStateWithLifecycle()
                                val includeDeadStock by viewModel.includeDeadStock.collectAsStateWithLifecycle()
                                val salesReportState by viewModel.salesReportUiState.collectAsStateWithLifecycle()
                                val scope = rememberCoroutineScope()
                                val context = LocalContext.current
                                
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateContentSize(spring(stiffness = Spring.StiffnessLow)),
                                    shape = RoundedCornerShape(18.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.12f)),
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        Text(
                                            "Generator Laporan",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        
                                        ReportPeriodToggleRow(
                                            selected = periodType,
                                            enabled = true,
                                            onSelect = viewModel::toggleReportPeriod,
                                        )
                                        
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            ReportOptionRow(
                                                label = "Ringkasan Penjualan & Keuangan",
                                                checked = includeSalesSummary,
                                                onCheckedChange = viewModel::toggleIncludeSalesSummary
                                            )
                                            ReportOptionRow(
                                                label = "Daftar Produk Terjual",
                                                checked = includeProductsSold,
                                                onCheckedChange = viewModel::toggleIncludeProductsSold
                                            )
                                            ReportOptionRow(
                                                label = "Produk Tidak Laku (Dead Stock)",
                                                checked = includeDeadStock,
                                                onCheckedChange = viewModel::toggleIncludeDeadStock
                                            )
                                        }
                                        
                                        if (periodType != null) {
                                            SalesReportResultCard(
                                                uiState = salesReportState,
                                                onPrint = viewModel::printSalesReport,
                                                onExportPdf = {
                                                    viewModel.buildCurrentReportLinesForExportAsync { lines ->
                                                        if (lines != null) {
                                                            scope.launch {
                                                                val file = ReceiptManager.exportPdfFromLines(
                                                                        context, lines, "Laporan_${periodType?.name}_$selectedDate"
                                                                    )
                                                                viewModel.notifyPdfExported()
                                                                onSharePdfFile(file)
                                                            }
                                                        }
                                                    }
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                            item(key = "tab_switcher") {
                                ReportTabSwitcher(selected = selectedTab, onSelect = viewModel::selectTab)
                            }
                            when (selectedTab) {
                                ReportTab.TRANSACTIONS -> {
                                    item(key = "list_header") {
                                        Text(
                                            "Daftar Transaksi (${report.transactions.size})",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(top = 4.dp),
                                        )
                                    }
                                    if (report.transactions.isEmpty()) {
                                        item(key = "empty") { EmptyReport() }
                                    } else {
                                        items(
                                            items = report.transactions,
                                            key = { it.id },
                                            contentType = { "transaction" },
                                        ) { tx ->
                                            TransactionRow(
                                                tx = tx,
                                                onClick = { viewModel.openTransactionDetail(tx.id) },
                                            )
                                        }
                                    }
                                }

                                ReportTab.SHIFTS -> {
                                    item(key = "payment_breakdown") {
                                        PaymentBreakdownSection(
                                            cashRevenue = report.cashRevenue,
                                            qrisRevenue = report.qrisRevenue,
                                        )
                                    }
                                    item(key = "return_summary") {
                                        ReturnSummarySection(
                                            cashRefundTotal = returnSummary.cashRefundTotal,
                                            qrisRefundTotal = returnSummary.qrisRefundTotal,
                                        )
                                    }
                                    item(key = "returns_header") {
                                        Text(
                                            "Daftar Retur (${returnSummary.returns.size})",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(top = 4.dp),
                                        )
                                    }
                                    if (returnSummary.returns.isEmpty()) {
                                        item(key = "empty_returns") { EmptyReturns() }
                                    } else {
                                        items(
                                            items = returnSummary.returns,
                                            key = { "return_${it.id}" },
                                            contentType = { "return" },
                                        ) { ret ->
                                            ReturnRow(
                                                ret = ret,
                                                onClick = { viewModel.openReturnDetail(ret.id) },
                                            )
                                        }
                                    }
                                    item(key = "closed_shifts_header") {
                                        Text(
                                            "Riwayat Tutup Shift (${closedShifts.size})",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(top = 4.dp),
                                        )
                                    }
                                    if (closedShifts.isEmpty()) {
                                        item(key = "empty_shifts") { EmptyClosedShifts() }
                                    } else {
                                        items(
                                            items = closedShifts,
                                            key = { it.id },
                                            contentType = { "closed_shift" },
                                        ) { shift ->
                                            ClosedShiftRow(
                                                shift = shift,
                                                onClick = { viewModel.openShiftDetail(shift) },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = showDirectWarrantyScreen,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
        ) {
val inventoryProducts by inventoryViewModel.products.collectAsStateWithLifecycle()

DirectWarrantyScreen(
    products = inventoryProducts,
    searchQuery = warrantySearchQuery,
    onQueryChange = { warrantySearchQuery = it },
    onScanClick = { warrantyScanner() },
    onNavigateBack = {
        showDirectWarrantyScreen = false
        warrantySearchQuery = ""
    },
    onSubmitWarranty = { product, qty, note ->
        viewModel.processDirectWarranty(
            product = product,
            qty = qty,
            note = note,
        )
    },
    onSubmitExchange = { broken, brokenQty, replace, replaceQty, note ->
        viewModel.prosesTukarGulingGaransi(
            barangRusak = broken,
            qtyRusak = brokenQty,
            barangPengganti = replace,
            qtyPengganti = replaceQty,
            catatan = note,
        )
    },
)
        }
    }
    if (selectedTransaction != null && !pendingVoidConfirm && !showReturnDialog && pendingPrintTarget == null) {
        val current = selectedTransaction!!
        TransactionDetailDialog(
            result = current,
            banner = voidBanner,
            printUiState = printUiState.forTransaction(current.transaction.id),
            onVoidClick = { pendingVoidConfirm = true },
            onReturnClick = { viewModel.openReturnDialog() },
            onPrint = { viewModel.printReceipt(current) },
            onExport = { onExportPdf(current) },
            onShare = { onShare(current) },
            onSharePdfFile = onSharePdfFile,
            onNavigateToSettings = onNavigateToSettings,
            onDismiss = {
                pendingVoidConfirm = false
                voidBanner = null
                viewModel.closeTransactionDetail()
            },
        )
    }
    pendingPrintTarget?.let { target ->
        PrinterPickerDialog(
            printers = target.availablePrinters,
            onSelect = { printer -> viewModel.onPrinterPicked(printer) },
            onDismiss = { viewModel.cancelPrinterPicker() },
        )
    }
    if (pendingVoidConfirm) {
        VoidConfirmDialog(
            invoiceId = selectedTransaction?.transaction?.id.orEmpty(),
            onConfirm = {
                viewModel.voidSelectedTransaction()
                pendingVoidConfirm = false
            },
            onDismiss = { pendingVoidConfirm = false },
        )
    }
    if (showReturnDialog && selectedTransaction != null) {
        ReturnItemDialog(
            result = selectedTransaction!!,
            submitting = returnSubmitting,
            message = returnMessage,
            onDismiss = viewModel::closeReturnDialog,
            onSubmit = { items, refundAmount, refundMethod, note ->
                viewModel.submitReturn(items, refundAmount, refundMethod, note)
            },
        )
    }
    selectedShiftDetail?.let { detail ->
        ClosedShiftDetailDialog(
            detail = detail,
            onDismiss = viewModel::closeShiftDetail,
        )
    }
    selectedReturnDetail?.let { detail ->
        ReturnDetailDialog(
            detail = detail,
            onViewOriginalTransaction = { invoiceId ->
                viewModel.closeReturnDetail()
                viewModel.openTransactionDetail(invoiceId)
            },
            onDismiss = viewModel::closeReturnDetail,
        )
    }
}

@Composable
private fun ReportPeriodToggleRow(
    selected: ReportPeriodType?,
    enabled: Boolean,
    onSelect: (ReportPeriodType) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ReportPeriodChip(
            label = "Harian",
            isSelected = selected == ReportPeriodType.DAILY,
            enabled = enabled,
            onClick = { onSelect(ReportPeriodType.DAILY) },
            modifier = Modifier.weight(1f),
        )
        ReportPeriodChip(
            label = "Bulanan",
            isSelected = selected == ReportPeriodType.MONTHLY,
            enabled = enabled,
            onClick = { onSelect(ReportPeriodType.MONTHLY) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ReportPeriodChip(
    label: String,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                .clickable(enabled = enabled, onClick = onClick)
                .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color =
                when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
        )
    }
}

@Composable
private fun SalesReportResultCard(
    uiState: SalesReportUiState,
    onPrint: () -> Unit,
    onExportPdf: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 2.dp)
            .animateContentSize(
                spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow,
                ),
            ),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
    ) {
        when (uiState) {
            is SalesReportUiState.Loading -> {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp))
                    Text("Memuat laporan...", style = MaterialTheme.typography.bodyMedium)
                }
            }

            is SalesReportUiState.Loaded -> {
                val data = uiState.data
                val periodLabel = if (uiState.periodType == ReportPeriodType.MONTHLY) "Bulanan" else "Harian"
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text(
                        "Hasil $periodLabel",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(2.dp))
                    SummaryLine("Jumlah Transaksi", "${data.summary.transactionCount}x")
                    SummaryLine("Penjualan Kotor", data.summary.subtotalSum.toRupiah())
                    SummaryLine("Pendapatan Bersih", data.pendapatanBersih.toRupiah(), emphasize = true)
                    if (data.biayaGaransi > 0) {
                        SummaryLine(
                            "Biaya Klaim Garansi",
                            "- ${data.biayaGaransi.toRupiah()}",
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                    SummaryLine("Laba Bersih", data.labaBersih.toRupiah(), color = MaterialTheme.colorScheme.primary)
                    if (data.diskon > 0) SummaryLine("Diskon", "- ${data.diskon.toRupiah()}")
                    if (data.summary.taxSum > 0) SummaryLine("Pajak", data.summary.taxSum.toRupiah())
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onPrint,
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(11.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp),
                        ) {
                            Icon(Icons.Rounded.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Cetak")
                        }
                        OutlinedButton(
                            onClick = onExportPdf,
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(11.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp),
                        ) {
                            Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("PDF")
                        }
                    }
                }
            }

            SalesReportUiState.Hidden -> Unit
        }
    }
}

@Composable
private fun DateNavigator(
    label: String,
    isToday: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit,
    onCalendarClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.10f)),
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CompactNavIcon(
                    icon = Icons.Rounded.ChevronLeft,
                    contentDescription = "Hari sebelumnya",
                    onClick = onPrevious,
                )
                Column(
                    modifier = Modifier.weight(1f).clickable(onClick = onCalendarClick),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(19.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        if (isToday) "Hari ini · ketuk untuk kalender" else "Ketuk untuk pilih kalender",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                CompactNavIcon(
                    icon = Icons.Rounded.ChevronRight,
                    contentDescription = "Hari berikutnya",
                    enabled = !isToday,
                    onClick = onNext,
                )
            }
            if (!isToday) {
                Row(
                    Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TodayPillButton(onClick = onToday)
                }
            }
        }
    }
}

@Composable
private fun MonthExpandableCard(
    monthGroup: MonthSalesGroup,
    onTransactionClick: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val monthLabel = monthGroup.yearMonth.format(
        DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("id-ID")),
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.34f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.10f)),
    ) {
        Column {
            Surface(
                onClick = { expanded = !expanded },
                color = Color.Transparent,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(9.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            monthLabel,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "${monthGroup.totalTransactions} transaksi",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Icon(
                        if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = if (expanded) "Ciutkan" else "Tampilkan transaksi",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    monthGroup.days.forEach { dayGroup ->
                        DayExpandableSection(
                            dayGroup = dayGroup,
                            onTransactionClick = onTransactionClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayExpandableSection(
    dayGroup: DaySalesGroup,
    onTransactionClick: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val dateLabel = dayGroup.date.format(
        DateTimeFormatter.ofPattern("EEEE, dd MMMM", Locale.forLanguageTag("id-ID")),
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            onClick = { expanded = !expanded },
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    dateLabel,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "${dayGroup.transactions.size} transaksi",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(3.dp))
                Icon(
                    if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier.padding(top = 5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                dayGroup.transactions.forEach { tx ->
                    TransactionRow(
                        tx = tx,
                        onClick = { onTransactionClick(tx.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactNavIcon(
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(40.dp) // Minimum 40dp-48dp Target Size
                .clip(CircleShape)
                .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(22.dp),
            tint =
                if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                },
        )
    }
}

@Composable
private fun TodayPillButton(onClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Rounded.Today,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.width(6.dp))
        Text(
            "Ke Hari Ini",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SummarySection(report: DailyReport) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.34f),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.10f),
            ),
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Text(
                    "Total Pendapatan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    report.totalRevenue.toRupiah(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    buildString {
                        append("${report.transactionCount} transaksi")
                        if (report.voidedCount > 0) {
                            append(" · ${report.voidedCount} dibatalkan")
                        }
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Jumlah Transaksi",
                value = "${report.transactionCount}",
                icon = Icons.AutoMirrored.Rounded.ReceiptLong,
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Rata-rata / Transaksi",
                value = report.averagePerTransaction.toRupiah(),
                icon = Icons.AutoMirrored.Rounded.ShowChart,
            )
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier, // BENAR: Diletakkan setelah parameter wajib
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(15.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.08f),
        ),
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun Long.toCompactRupiah(): String {
    fun trim(d: Double): String {
        val rounded = Math.round(d * 10) / 10.0
        return if (rounded == rounded.toLong().toDouble()) {
            rounded.toLong().toString()
        } else {
            rounded.toString().replace('.', ',')
        }
    }
    return when {
        this >= 1_000_000_000L -> "Rp${trim(this / 1_000_000_000.0)}M"
        this >= 1_000_000L -> "Rp${trim(this / 1_000_000.0)}jt"
        this >= 1_000L -> "Rp${trim(this / 1_000.0)}rb"
        else -> "Rp$this"
    }
}

@Composable
private fun RevenueTrendChart(
    date: LocalDate,
    transactions: List<TransactionEntity>,
    hourly: List<Long>,
) {
    val primary = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val gridColor = onSurface.copy(alpha = 0.08f)
    val axisTextColor = onSurface.copy(alpha = 0.58f)
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }
    val textMeasurer = rememberTextMeasurer()
    val zone = remember { ZoneId.systemDefault() }
    val dayStartMillis = remember(date) {
        date.atStartOfDay(zone).toInstant().toEpochMilli()
    }
    val dayEndMillis = remember(date) {
        date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
    }

    // Hanya transaksi aktif yang benar-benar digambar. Void tidak masuk grafik.
    val points = remember(transactions, dayStartMillis, dayEndMillis) {
        transactions
            .asSequence()
            .filter { it.createdAt in dayStartMillis until dayEndMillis }
            .sortedBy { it.createdAt }
            .map { tx ->
                RevenuePoint(
                    transaction = tx,
                    timeMillis = tx.createdAt,
                    value = tx.total.coerceAtLeast(0L),
                )
            }
            .toList()
    }

    val peakHour = remember(points) {
        points
            .groupBy { tx ->
                ((tx.timeMillis - dayStartMillis) / 3_600_000L).toInt().coerceIn(0, 23)
            }
            .maxWithOrNull(
                compareBy<Map.Entry<Int, List<RevenuePoint>>> { it.value.size }
                    .thenBy { it.key },
            )
            ?.key ?: 0
    }
    val peakTransactionCount = remember(points, peakHour) {
        points.count {
            ((it.timeMillis - dayStartMillis) / 3_600_000L).toInt().coerceIn(0, 23) == peakHour
        }
    }
    val peakRevenueHour = remember(hourly) {
        hourly.indices.maxByOrNull { hourly[it] } ?: 0
    }
    val peakRevenue = remember(hourly, peakRevenueHour) {
        hourly.getOrElse(peakRevenueHour) { 0L }
    }

    val maxPointValue = points.maxOfOrNull { it.value } ?: 0L
    val maxRevenue = maxOf(maxPointValue, 1L)
    val ySteps = 4
    val hourSpacing = 64.dp
    val yAxisWidth = 54.dp
    val chartWidth = hourSpacing * 24f + 16.dp
    val chartHeight = 190.dp
    val plotBottom = 154.dp

    val labelStyle = remember(axisTextColor) {
        TextStyle(
            color = axisTextColor,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
        )
    }
    val yLabels = remember(maxRevenue, labelStyle) {
        (0..ySteps).map { step ->
            val value = maxRevenue * step / ySteps.toLong()
            textMeasurer.measure(value.toCompactRupiah(), labelStyle)
        }
    }
    // Capture theme colors in the composable scope; Canvas/DrawScope cannot
    // invoke MaterialTheme directly.
    val tooltipContainerColor = MaterialTheme.colorScheme.primaryContainer
    val tooltipTextColor = MaterialTheme.colorScheme.onPrimaryContainer


    // Reset selection when the underlying dataset changes.
    LaunchedEffect(points) {
        selectedPointIndex = null
    }

    // Saat dibuka, arahkan viewport ke sekitar transaksi terakhir. Ini tidak
    // mengubah HorizontalPager karena scrollState hanya dimiliki chart ini.
    LaunchedEffect(points, chartWidth) {
        if (points.isNotEmpty()) {
            val latestHour = ((points.last().timeMillis - dayStartMillis) / 3_600_000L)
                .toInt()
                .coerceIn(0, 23)
            val target = with(density) {
                (latestHour - 2).coerceAtLeast(0) * hourSpacing.toPx()
            }
            // Menunggu satu frame memberi kesempatan LazyColumn/Canvas mengukur maxValue.
            kotlinx.coroutines.yield()
            scrollState.scrollTo(target.roundToInt().coerceIn(0, scrollState.maxValue))
        } else {
            scrollState.scrollTo(0)
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.10f),
        ),
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.AutoMirrored.Rounded.ShowChart,
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Tren Pendapatan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (points.isNotEmpty()) {
                Spacer(Modifier.height(5.dp))
                Text(
                    "Jam teramai · ${"%02d:00–%02d:00".format(peakHour, peakHour + 1)} · " +
                        "$peakTransactionCount transaksi",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (peakRevenue > 0L) {
                    Text(
                        "Pendapatan tertinggi · ${"%02d:00–%02d:00".format(peakRevenueHour, peakRevenueHour + 1)} · ${peakRevenue.toRupiah()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                Spacer(Modifier.height(5.dp))
                Text(
                    "Belum ada transaksi pada hari ini",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth().height(chartHeight),
                verticalAlignment = Alignment.Top,
            ) {
                // Sumbu Y tetap terlihat ketika grafik digeser horizontal.
                Canvas(
                    modifier = Modifier.width(yAxisWidth).height(chartHeight),
                ) {
                    val plotBottomPx = plotBottom.toPx()
                    val plotHeight = plotBottomPx
                    for (step in 0..ySteps) {
                        val ratio = step / ySteps.toFloat()
                        val y = plotBottomPx - ratio * plotHeight
                        val measured = yLabels.getOrElse(step) {
                            textMeasurer.measure("Rp0", labelStyle)
                        }
                        drawText(
                            textLayoutResult = measured,
                            topLeft = Offset(
                                0f,
                                (y - measured.size.height / 2f)
                                    .coerceIn(0f, plotBottomPx - measured.size.height),
                            ),
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(chartHeight)
                        .horizontalScroll(scrollState),
                ) {
                    Canvas(
                        modifier = Modifier
                            .width(chartWidth)
                            .height(chartHeight)
                            .pointerInput(points, maxRevenue, dayStartMillis) {
                                detectTapGestures { position ->
                                    if (points.isEmpty()) {
                                        selectedPointIndex = null
                                    } else {
                                        val plotLeft = 8.dp.toPx()
                                        val plotWidth = 24f * hourSpacing.toPx()
                                        val plotHeightPx = plotBottom.toPx()
                                        val x = position.x
                                        val y = position.y

                                        fun xFor(time: Long): Float {
                                            val ratio = (time - dayStartMillis).toFloat() /
                                                (dayEndMillis - dayStartMillis).toFloat()
                                            return plotLeft + ratio.coerceIn(0f, 1f) * plotWidth
                                        }

                                        fun yFor(value: Long): Float {
                                            val ratio = value.toFloat() / maxRevenue.toFloat()
                                            return plotHeightPx - ratio.coerceIn(0f, 1f) * plotHeightPx
                                        }

                                        val nearest = points
                                            .mapIndexed { index, point ->
                                                val dx = x - xFor(point.timeMillis)
                                                val dy = y - yFor(point.value)
                                                index to (dx * dx + dy * dy)
                                            }
                                            .minByOrNull { it.second }

                                        selectedPointIndex = nearest
                                            ?.takeIf { it.second <= 28.dp.toPx() * 28.dp.toPx() }
                                            ?.first
                                    }
                                }
                            },
                    ) {
                        val plotLeft = 8.dp.toPx()
                        val plotRight = size.width
                        val plotTop = 0f
                        val plotBottomPx = plotBottom.toPx()
                        val plotWidth = 24f * hourSpacing.toPx()
                        val plotHeight = plotBottomPx

                        fun xFor(time: Long): Float {
                            val ratio = (time - dayStartMillis).toFloat() /
                                (dayEndMillis - dayStartMillis).toFloat()
                            return plotLeft + ratio.coerceIn(0f, 1f) * plotWidth
                        }

                        fun yFor(value: Long): Float {
                            val ratio = value.toFloat() / maxRevenue.toFloat()
                            return plotBottomPx - ratio.coerceIn(0f, 1f) * plotHeight
                        }

                        // Grid horizontal: membantu membaca nominal tanpa membuat
                        // grafik terasa berat.
                        for (step in 0..ySteps) {
                            val ratio = step / ySteps.toFloat()
                            val y = plotBottomPx - ratio * plotHeight
                            drawLine(
                                color = gridColor,
                                start = Offset(plotLeft, y),
                                end = Offset(plotRight, y),
                                strokeWidth = 1.dp.toPx(),
                            )
                        }

                        // Grid vertikal dan label setiap jam: 00 sampai 24.
                        for (hour in 0..24) {
                            val x = plotLeft + hour * hourSpacing.toPx()
                            drawLine(
                                color = gridColor,
                                start = Offset(x, plotTop),
                                end = Offset(x, plotBottomPx),
                                strokeWidth = 1.dp.toPx(),
                            )
                            val label = "%02d".format(hour)
                            val measured = textMeasurer.measure(label, labelStyle)
                            val labelX = (x - measured.size.width / 2f)
                                .coerceIn(0f, size.width - measured.size.width)
                            drawText(
                                textLayoutResult = measured,
                                topLeft = Offset(labelX, plotBottomPx + 6.dp.toPx()),
                            )
                        }

                        // Satu transaksi = satu titik. Tidak ada garis, area fill,
                        // interpolasi, atau akumulasi visual.
                        points.forEachIndexed { index, point ->
                            val center = Offset(
                                xFor(point.timeMillis),
                                yFor(point.value),
                            )
                            val selected = selectedPointIndex == index
                            if (selected) {
                                drawCircle(
                                    color = primary.copy(alpha = 0.18f),
                                    radius = 9.dp.toPx(),
                                    center = center,
                                )
                            }
                            drawCircle(
                                color = primary,
                                radius = if (selected) 4.5.dp.toPx() else 3.5.dp.toPx(),
                                center = center,
                            )
                        }

                        selectedPointIndex?.let { index ->
                            val point = points.getOrNull(index) ?: return@let
                            val pointX = xFor(point.timeMillis)
                            val pointY = yFor(point.value)
                            val timeText = ReportViewModel.timeFmt.format(
                                Instant.ofEpochMilli(point.timeMillis),
                            )
                            val amountText = point.value.toRupiah()
                            val idText = point.transaction.id
                            val tooltipStyle = TextStyle(
                                color = tooltipTextColor,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                            )
                            val timeLayout = textMeasurer.measure(timeText, tooltipStyle)
                            val amountLayout = textMeasurer.measure(amountText, tooltipStyle.copy(fontWeight = FontWeight.Bold))
                            val idLayout = textMeasurer.measure(
                                idText.take(22).let { if (idText.length > 22) "$it…" else it },
                                tooltipStyle,
                            )
                            val tooltipWidth = maxOf(
                                timeLayout.size.width,
                                amountLayout.size.width,
                                idLayout.size.width,
                            ) + 20.dp.toPx()
                            val tooltipHeight = 58.dp.toPx()
                            val tooltipX = (pointX - tooltipWidth / 2f)
                                .coerceIn(4.dp.toPx(), size.width - tooltipWidth - 4.dp.toPx())
                            val tooltipY = (pointY - tooltipHeight - 10.dp.toPx())
                                .coerceAtLeast(4.dp.toPx())

                            drawRoundRect(
                                color = tooltipContainerColor,
                                topLeft = Offset(tooltipX, tooltipY),
                                size = androidx.compose.ui.geometry.Size(tooltipWidth, tooltipHeight),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx()),
                            )
                            drawText(
                                textLayoutResult = timeLayout,
                                topLeft = Offset(tooltipX + 10.dp.toPx(), tooltipY + 7.dp.toPx()),
                            )
                            drawText(
                                textLayoutResult = amountLayout,
                                topLeft = Offset(tooltipX + 10.dp.toPx(), tooltipY + 22.dp.toPx()),
                            )
                            drawText(
                                textLayoutResult = idLayout,
                                topLeft = Offset(tooltipX + 10.dp.toPx(), tooltipY + 39.dp.toPx()),
                            )
                        }
                    }
                }
            }

            if (points.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Geser grafik untuk melihat 00–24 · Ketuk titik untuk detail transaksi",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
                    modifier = Modifier.padding(start = yAxisWidth),
                )
            }
        }
    }
}

private data class RevenuePoint(
    val transaction: TransactionEntity,
    val timeMillis: Long,
    val value: Long,
)


@Composable
private fun ReportTabSwitcher(
    selected: ReportTab,
    onSelect: (ReportTab) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ReportTabChip(
            label = "Transaksi",
            selected = selected == ReportTab.TRANSACTIONS,
            onClick = { onSelect(ReportTab.TRANSACTIONS) },
            modifier = Modifier.weight(1f),
        )
        ReportTabChip(
            label = "Shift",
            selected = selected == ReportTab.SHIFTS,
            onClick = { onSelect(ReportTab.SHIFTS) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ReportTabChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                .clickable(onClick = onClick)
                .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color =
                if (selected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
        )
    }
}

@Composable
private fun PaymentBreakdownSection(
    cashRevenue: Long,
    qrisRevenue: Long,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Breakdown Metode Bayar",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Tunai",
                value = cashRevenue.toRupiah(),
                icon = Icons.Rounded.AttachMoney,
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "QRIS",
                value = qrisRevenue.toRupiah(),
                icon = Icons.Rounded.QrCode,
            )
        }
    }
}

@Composable
private fun ReturnSummarySection(
    cashRefundTotal: Long,
    qrisRefundTotal: Long,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Retur Hari Ini",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Refund Tunai",
                value = cashRefundTotal.toRupiah(),
                icon = Icons.Rounded.AttachMoney,
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Refund QRIS",
                value = qrisRefundTotal.toRupiah(),
                icon = Icons.Rounded.QrCode,
            )
        }
    }
}

@Composable
private fun ReturnRow(
    ret: ReturnEntity,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                ReportViewModel.timeFmt.format(Instant.ofEpochMilli(ret.returnedAt)),
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    ret.transactionId,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${ret.cashierName.ifBlank { "Tanpa kasir" }} · ${paymentMethodLabel(ret.refundMethod)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (ret.note.isNotBlank()) {
                    Text(
                        ret.note,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Text(
                "- ${ret.refundAmount.toRupiah()}",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(Modifier.width(6.dp))
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = "Lihat detail retur",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun EmptyReturns() {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Rounded.History,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Belum ada retur pada hari ini",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ReturnDetailDialog(
    detail: ReturnDetail,
    onViewOriginalTransaction: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val header = detail.header
    val items = detail.items
    val totalQty = items.sumOf { it.quantityReturned }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detail Retur", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(header.transactionId, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    ReportViewModel.dateTimeFmt.format(Instant.ofEpochMilli(header.returnedAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                val isSynthetic = header.transactionId.startsWith("EXC-") || header.transactionId.startsWith("RET-DIR-")
                if (!isSynthetic) {
                    TextButton(
                        onClick = { onViewOriginalTransaction(header.transactionId) },
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 4.dp),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ReceiptLong,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Lihat Transaksi Asal", style = MaterialTheme.typography.labelMedium)
                    }
                    Spacer(Modifier.height(6.dp))
                }
                HorizontalDivider(Modifier.padding(vertical = 2.dp))
                SummaryLine("Kasir", header.cashierName.ifBlank { "Tanpa kasir" })
                SummaryLine("Shift", header.shiftId?.let { "#$it" } ?: "Tanpa shift")
                Spacer(Modifier.height(10.dp))
                Text(
                    "Item Diretur (${totalQty.formatQuantity()})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
                HorizontalDivider(Modifier.padding(vertical = 2.dp))
                items.forEach { item ->
                    ReturnDetailItemRow(item)
                }
                Spacer(Modifier.height(6.dp))
                HorizontalDivider(Modifier.padding(vertical = 2.dp))
                SummaryLine("Metode Pengembalian", paymentMethodLabel(header.refundMethod))
                SummaryLine(
                    "Total Refund",
                    header.refundAmount.toRupiah(),
                    emphasize = true,
                    color = MaterialTheme.colorScheme.error,
                )
                if (header.note.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text("Catatan", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Text(
                        header.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Tutup") }
        },
    )
}

@Composable
private fun ReturnDetailItemRow(item: ReturnItemEntity) {
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(item.productName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    "${item.quantityReturned.formatQuantity()} x ${item.unitPrice.toRupiah()}",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                kotlin.math
                    .round(item.unitPrice * item.quantityReturned)
                    .toLong()
                    .toRupiah(),
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
            )
        }
        RestockBadge(restocked = item.restocked, hasProduct = item.productId != null)
    }
}

@Composable
private fun RestockBadge(
    restocked: Boolean,
    hasProduct: Boolean,
) {
    val (label, color) =
        when {
            !hasProduct -> "Produk sudah dihapus · stok dilewati" to MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            restocked -> "✓ Dikembalikan ke stok" to MaterialTheme.colorScheme.primary
            else -> "✗ Tidak dikembalikan ke stok" to MaterialTheme.colorScheme.error
        }
    Text(
        label,
        style = MaterialTheme.typography.labelSmall,
        color = color,
    )
}

@Composable
private fun TransactionRow(
    tx: TransactionEntity,
    onClick: () -> Unit,
) {
    val isVoid = tx.isVoid
    val isReturned = tx.hasReturn
    val muted = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.42f)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isVoid) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = if (isVoid) 0.06f else 0.10f),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.width(52.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    ReportViewModel.timeFmt.format(Instant.ofEpochMilli(tx.createdAt)),
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isVoid) muted else MaterialTheme.colorScheme.primary,
                )
                Text(
                    tx.createdAt.let {
                        Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .dayOfMonth
                            .toString()
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isVoid) muted else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    tx.id,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isVoid) muted else MaterialTheme.colorScheme.onSurface,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text(
                        paymentMethodLabel(tx.paymentMethod),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isVoid) muted else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        "·",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    )
                    Text(
                        "Bayar ${tx.paidAmount.toRupiah()}",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = if (isVoid) muted else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (isVoid) VoidBadge()
                    if (isReturned) ReturnedBadge()
                }
            }

            Spacer(Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    tx.total.toRupiah(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isVoid) muted else MaterialTheme.colorScheme.onSurface,
                )
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = "Lihat detail transaksi",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }
        }
    }
}

@Composable
private fun VoidBadge() {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
    ) {
        Text(
            "DIBATALKAN",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun ReturnedBadge() {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
    ) {
        Text(
            "SUDAH DIRETUR",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun ReceiptActionsRow(
    onPrint: () -> Unit,
    onExport: () -> Unit,
    onShare: () -> Unit,
    printEnabled: Boolean = true,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ReceiptActionButton(
            icon = Icons.Rounded.Print,
            label = if (printEnabled) "Cetak" else "Mencetak...",
            onClick = onPrint,
            enabled = printEnabled,
            modifier = Modifier.weight(1f),
        )
        ReceiptActionButton(
            icon = Icons.Rounded.PictureAsPdf,
            label = "PDF",
            onClick = onExport,
            modifier = Modifier.weight(1f),
        )
        ReceiptActionButton(
            icon = Icons.Rounded.Share,
            label = "Bagikan",
            onClick = onShare,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ReceiptActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (enabled) 0.4f else 0.2f),
                ).clickable(enabled = enabled, onClick = onClick)
                .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (enabled) 1f else 0.5f),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (enabled) 1f else 0.5f),
        )
    }
}

@Composable
private fun TransactionDetailDialog(
    result: CheckoutResult,
    banner: ReportMessage?,
    printUiState: PrintUiState,
    onVoidClick: () -> Unit,
    onReturnClick: () -> Unit,
    onPrint: () -> Unit,
    onExport: () -> Unit,
    onShare: () -> Unit,
    onSharePdfFile: (File) -> Unit,
    onNavigateToSettings: () -> Unit,
    onDismiss: () -> Unit,
) {
    val tx = result.transaction
    val isVoid = tx.isVoid
    val hasReturn = tx.hasReturn

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    "Detail Transaksi",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    ReportViewModel.dateTimeFmt.format(Instant.ofEpochMilli(tx.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                banner?.let { msg ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = if (msg.isError) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        },
                    ) {
                        Text(
                            msg.text,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (msg.isError) {
                                MaterialTheme.colorScheme.onErrorContainer
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            },
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                tx.id,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                tx.cashierName.ifBlank { "Tanpa kasir" },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            if (isVoid) VoidBadge()
                            else if (hasReturn) ReturnedBadge()
                        }
                    }
                }

                ReceiptActionsRow(
                    onPrint = onPrint,
                    onExport = onExport,
                    onShare = onShare,
                    printEnabled = printUiState !is PrintUiState.Printing,
                )

                ReprintResultBanner(
                    printUiState = printUiState,
                    onSharePdfFile = onSharePdfFile,
                    onNavigateToSettings = onNavigateToSettings,
                )

                Text(
                    "Item Pembelian",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.20f),
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                        result.items.forEachIndexed { index, item ->
                            if (index > 0) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.16f),
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 9.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        item.productName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    Text(
                                        "${item.quantity.formatQuantity()} × ${item.unitPrice.toRupiah()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Text(
                                    item.lineTotal.toRupiah(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }

                Text(
                    "Ringkasan Pembayaran",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.20f),
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        SummaryLine("Subtotal", tx.subtotal.toRupiah())

                        tx.discountRowLabel()?.let { label ->
                            SummaryLine(label, "- ${tx.discount.toRupiah()}")
                        }

                        if (tx.tax > 0) {
                            SummaryLine("Pajak", tx.tax.toRupiah())
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.22f),
                        )

                        SummaryLine(
                            "Total",
                            tx.total.toRupiah(),
                            emphasize = true,
                        )
                        SummaryLine("Bayar", tx.paidAmount.toRupiah())

                        when {
                            tx.change < 0L -> {
                                SummaryLine(
                                    "Kurang Bayar",
                                    kotlin.math.abs(tx.change).toRupiah(),
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }

                            tx.change == 0L -> {
                                SummaryLine(
                                    "Kembali",
                                    tx.change.toRupiah(),
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }

                            else -> {
                                val isQrisCashOut =
                                    tx.paymentMethod == PaymentMethod.QRIS.name && tx.changeGivenInCash

                                SummaryLine(
                                    if (isQrisCashOut) "Kembali (Tunai Laci)" else "Kembali",
                                    tx.changeGiven.toRupiah(),
                                    color = if (isQrisCashOut) {
                                        MaterialTheme.colorScheme.tertiary
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    },
                                )

                                val tip = (tx.change - tx.changeGiven).coerceAtLeast(0L)
                                if (tip > 0L) {
                                    SummaryLine(
                                        "Tip/Donasi",
                                        tip.toRupiah(),
                                        color = MaterialTheme.colorScheme.tertiary,
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.22f),
                        )

                        SummaryLine("Metode Bayar", paymentMethodLabel(tx.paymentMethod))
                        SummaryLine("Kasir", tx.cashierName.ifBlank { "Tanpa kasir" })
                        tx.shiftId?.let { id ->
                            SummaryLine("Shift", "#$id")
                        }

                        if (isVoid && tx.voidedAt != null) {
                            SummaryLine(
                                "Dibatalkan",
                                ReportViewModel.dateTimeFmt.format(
                                    Instant.ofEpochMilli(tx.voidedAt),
                                ),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }

                if (!isVoid) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f),
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            if (!hasReturn) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    TextButton(
                                        onClick = onReturnClick,
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Text(
                                            "Retur Item",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }

                                    TextButton(
                                        onClick = onVoidClick,
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Text(
                                            "Batalkan",
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    "Transaksi ini sudah memiliki riwayat retur, sehingga tidak dapat dibatalkan.",
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        },
    )
}

@Composable
private fun ReprintResultBanner(
    printUiState: PrintUiState,
    onSharePdfFile: (File) -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    val state = printUiState as? PrintUiState.Result ?: return
    val outcome = state.outcome
    val (message, isError) =
        when (outcome) {
            is ReceiptPrintOutcome.Success -> {
                "Struk terkirim ke \"${outcome.printer.label}\"." to false
            }

            is ReceiptPrintOutcome.SuccessWithNotice -> {
                "Struk terkirim ke \"${outcome.printer.label}\".\n⚠ ${outcome.notice}" to false
            }

            is ReceiptPrintOutcome.Failed -> {
                val printerCount = outcome.attempts.size
                val reason = outcome.attempts.firstOrNull()?.message ?: ""
                if (reason.contains("terhubung", ignoreCase = true)) {
                    if (printerCount > 1) {
                        "Gagal mencetak ke semua printer. Periksa koneksi perangkat." to true
                    } else {
                        "Gagal mencetak ke printer. Periksa koneksi perangkat." to true
                    }
                } else {
                    val title = if (printerCount > 1) "Gagal mencetak ke semua printer." else "Gagal mencetak ke printer."
                    "$title\nAlasan: $reason" to true
                }
            }

            ReceiptPrintOutcome.NoPrinterConfigured -> {
                "Printer belum diatur." to true
            }

            ReceiptPrintOutcome.AlreadyInProgress -> {
                "Sedang mencetak, mohon tunggu..." to false
            }
        }

    // SOLUSI: Semua emisi UI di top-level dibungkus dalam satu Column
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(Modifier.height(6.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = if (isError) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            },
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall,
                color = if (isError) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                },
            )
        }
        
        if (outcome is ReceiptPrintOutcome.Failed && outcome.fallbackPdf != null) {
            Spacer(Modifier.height(4.dp)) // Opsional: Beri sedikit jarak sebelum tombol
            TextButton(
                onClick = { onSharePdfFile(outcome.fallbackPdf) },
                modifier = Modifier.align(Alignment.End) // Opsional: Meratakan tombol ke kanan
            ) {
                Text("Bagikan PDF Cadangan")
            }
        }
        
        if (outcome is ReceiptPrintOutcome.NoPrinterConfigured) {
            Spacer(Modifier.height(4.dp)) // Opsional: Beri sedikit jarak sebelum tombol
            TextButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.align(Alignment.End) // Opsional: Meratakan tombol ke kanan
            ) {
                Text("Buka Pengaturan Printer")
            }
        }
    }
}

@Composable
private fun VoidConfirmDialog(
    invoiceId: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Batalkan Transaksi?", fontWeight = FontWeight.Bold) },
        text = {
            Text(
                "Transaksi $invoiceId akan dibatalkan dan stok item akan dikembalikan. " +
                    "Tindakan ini tidak dapat diurungkan.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Ya, Batalkan", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Tidak") }
        },
    )
}

private data class ReturnRowState(
    val transactionItemId: Long,
    val productId: Long?,
    val productName: String,
    val unitPrice: Long,
    val maxQuantity: Double,
    val included: Boolean = false,
    val quantity: Double = maxQuantity,
    val restocked: Boolean = productId != null,
    val restockToDamaged: Boolean = false,
)

@Composable
private fun ReturnItemDialog(
    result: CheckoutResult,
    submitting: Boolean,
    message: ReportMessage?,
    onDismiss: () -> Unit,
    onSubmit: (items: List<ReturnItemInput>, refundAmount: Long, refundMethod: PaymentMethod, note: String) -> Unit,
) {
    val tx = result.transaction
    var rows by remember(tx.id) {
        mutableStateOf(
            result.items.map { item ->
                ReturnRowState(
                    transactionItemId = item.id,
                    productId = item.productId,
                    productName = item.productName,
                    unitPrice = item.unitPrice,
                    maxQuantity = item.quantity,
                )
            },
        )
    }
    val suggestedRefund = rows.filter { it.included }.sumOf { kotlin.math.round(it.unitPrice * it.quantity).toLong() }
    var refundAmountEdited by remember(tx.id) { mutableStateOf(false) }
    var refundAmountText by remember(tx.id) { mutableStateOf("") }
    LaunchedEffect(suggestedRefund) {
        if (!refundAmountEdited) {
            refundAmountText = if (suggestedRefund <= 0L) "" else suggestedRefund.toString()
        }
    }
    var refundMethod by remember(tx.id) {
        mutableStateOf(
            if (tx.paymentMethod == PaymentMethod.QRIS.name) PaymentMethod.QRIS else PaymentMethod.CASH,
        )
    }
    var note by remember(tx.id) { mutableStateOf("") }
    val includedCount = rows.count { it.included }
    val maxRefundable = tx.total
    val refundAmountValue = refundAmountText.toLongOrNull() ?: 0L
    val isRefundOverLimit = refundAmountValue > maxRefundable
    AlertDialog(
        onDismissRequest = { if (!submitting) onDismiss() },
        title = { Text("Retur Item", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                message?.let { msg ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color =
                            if (msg.isError) {
                                MaterialTheme.colorScheme.errorContainer
                            } else {
                                MaterialTheme.colorScheme.primaryContainer
                            },
                    ) {
                        Text(
                            msg.text,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color =
                                if (msg.isError) {
                                    MaterialTheme.colorScheme.onErrorContainer
                                } else {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                },
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                }
                Text(
                    "Transaksi ${tx.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Pilih item yang dikembalikan pelanggan:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                rows.forEachIndexed { index, row ->
                    ReturnItemRow(
                        row = row,
                        onToggleIncluded = { checked ->
                            rows =
                                rows.toMutableList().also {
                                    it[index] = row.copy(included = checked)
                                }
                        },
                        onQuantityChange = { qty ->
                            rows =
                                rows.toMutableList().also {
                                    it[index] = row.copy(quantity = qty.coerceIn(0.1, row.maxQuantity).roundToQuantityPrecision())
                                }
                        },
                        onRestockedChange = { checked ->
                            rows =
                                rows.toMutableList().also {
                                    it[index] = row.copy(restocked = checked)
                                }
                        },
                        onRestockToDamagedChange = { checked ->
                            rows =
                                rows.toMutableList().also {
                                    it[index] = row.copy(restockToDamaged = checked)
                                }
                        },
                    )
                    HorizontalDivider(Modifier.padding(vertical = 2.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text("Metode Pengembalian", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                RefundMethodToggle(selected = refundMethod, onSelect = { refundMethod = it })
                Spacer(Modifier.height(10.dp))
                Text("Nominal Pengembalian", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                RefundAmountField(
                    value = refundAmountText,
                    onValueChange = { digits ->
                        refundAmountEdited = true
                        refundAmountText = digits
                    },
                    isError = isRefundOverLimit,
                )
                if (isRefundOverLimit) {
                    Text(
                        "Melebihi total transaksi (maks ${maxRefundable.toRupiah()})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                } else if (includedCount > 0) {
                    Text(
                        "Sugesti: ${suggestedRefund.toRupiah()} (tanpa prorata diskon/pajak)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text("Catatan (opsional)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Mis. barang cacat produksi") },
                    minLines = 2,
                    textStyle = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = !submitting && includedCount > 0 && !isRefundOverLimit,
                onClick = {
                    val items =
                        rows.filter { it.included }.map { row ->
                            ReturnItemInput(
                                transactionItemId = row.transactionItemId,
                                productId = row.productId,
                                productName = row.productName,
                                unitPrice = row.unitPrice,
                                quantityReturned = row.quantity,
                                restocked = row.restocked && row.productId != null,
                                restockToDamaged = row.restockToDamaged && row.restocked && row.productId != null,
                            )
                        }
                    onSubmit(items, refundAmountText.toLongOrNull() ?: 0L, refundMethod, note.trim())
                },
            ) {
                if (submitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Proses Retur", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(enabled = !submitting, onClick = onDismiss) { Text("Batal") }
        },
    )
}

@Composable
private fun ReturnItemRow(
    row: ReturnRowState,
    onToggleIncluded: (Boolean) -> Unit,
    onQuantityChange: (Double) -> Unit,
    onRestockedChange: (Boolean) -> Unit,
    onRestockToDamagedChange: (Boolean) -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = row.included, onCheckedChange = onToggleIncluded)
            Column(Modifier.weight(1f)) {
                Text(
                    row.productName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${row.unitPrice.toRupiah()} · maks ${row.maxQuantity.formatQuantity()}",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (row.included) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 40.dp, top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val step = if (row.maxQuantity % 1.0 == 0.0) 1.0 else 0.1
                    MiniStepper(
                        qty = row.quantity,
                        canDecrease = row.quantity > step,
                        canIncrease = row.quantity < row.maxQuantity,
                        onDecrease = { onQuantityChange((row.quantity - step).coerceAtLeast(step)) },
                        onIncrease = { onQuantityChange((row.quantity + step).coerceAtMost(row.maxQuantity)) },
                    )
                    Spacer(Modifier.width(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                            if (row.productId != null) {
                                Modifier.clickable { onRestockedChange(!row.restocked) }
                            } else {
                                Modifier
                            },
                    ) {
                        Checkbox(
                            checked = row.restocked && row.productId != null,
                            onCheckedChange = if (row.productId != null) onRestockedChange else null,
                            enabled = row.productId != null,
                        )
                        Text(
                            "Kembalikan ke stok?",
                            style = MaterialTheme.typography.labelSmall,
                            color =
                                MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = if (row.productId != null) 0.8f else 0.4f,
                                ),
                        )
                    }
                }
                if (row.restocked && row.productId != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onRestockToDamagedChange(!row.restockToDamaged) },
                    ) {
                        Checkbox(
                            checked = row.restockToDamaged,
                            onCheckedChange = onRestockToDamagedChange,
                        )
                        Text(
                            "Tandai sebagai Stok Rusak / Garansi",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniStepper(
    qty: Double,
    canDecrease: Boolean,
    canIncrease: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier =
                Modifier
                    .size(36.dp) // Minimum Touch Target perbaikan UI/UX
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (canDecrease) 1f else 0.4f))
                    .then(if (canDecrease) Modifier.clickable(onClick = onDecrease) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.Remove, contentDescription = "Kurangi jumlah", modifier = Modifier.size(18.dp))
        }
        Text(
            qty.formatQuantity(),
            modifier = Modifier.width(44.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
        )
        Box(
            modifier =
                Modifier
                    .size(36.dp) // Minimum Touch Target perbaikan UI/UX
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (canIncrease) 1f else 0.4f))
                    .then(if (canIncrease) Modifier.clickable(onClick = onIncrease) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Tambah jumlah", modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun RefundMethodToggle(
    selected: PaymentMethod,
    onSelect: (PaymentMethod) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        listOf(PaymentMethod.CASH to "Tunai", PaymentMethod.QRIS to "QRIS").forEach { (method, label) ->
            val isSelected = selected == method
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { onSelect(method) }
                        .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color =
                        if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }
        }
    }
}

@Composable
private fun RefundAmountField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
) {
    BasicTextField(
        value = value,
        onValueChange = { input -> onValueChange(input.filter { it.isDigit() }) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        visualTransformation = ThousandsSeparatorTransformation,
        textStyle =
            MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            ),
        modifier = Modifier.fillMaxWidth().height(48.dp),
        decorationBox = { innerTextField ->
            Row(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                        .border(
                            width = 1.dp,
                            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp),
                        ).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Rp ",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            "0",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                        )
                    }
                    innerTextField()
                }
            }
        },
    )
}

@Composable
private fun ClosedShiftRow(
    shift: ShiftEntity,
    onClick: () -> Unit,
) {
    val diff = shift.cashDifference
    val diffColor =
        when {
            diff == null -> MaterialTheme.colorScheme.onSurfaceVariant
            diff < 0L -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.primary
        }
    val diffLabel =
        when {
            diff == null -> "-"
            diff == 0L -> "Pas"
            diff < 0L -> "-${(-diff).toRupiah()}"
            else -> "+${diff.toRupiah()}"
        }
        
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                shift.endedAt?.let { ReportViewModel.timeFmt.format(Instant.ofEpochMilli(it)) } ?: "-",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    shift.cashierName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "Kas awal ${shift.startingCash.toRupiah()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    diffLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = diffColor,
                )
                Text(
                    "Selisih",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(6.dp))
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = "Lihat detail shift",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ClosedShiftDetailDialog(
    detail: ClosedShiftDetail,
    onDismiss: () -> Unit,
) {
    val shift = detail.shift
    val summary = detail.summary
    val expected = shift.endingCashExpected ?: summary.expectedCashInDrawer
    val actual = shift.endingCashActual ?: 0L
    val difference = shift.cashDifference ?: (actual - expected)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detail Tutup Shift", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(shift.cashierName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    "Mulai: ${ReportViewModel.dateTimeFmt.format(Instant.ofEpochMilli(shift.startedAt))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                shift.endedAt?.let {
                    Text(
                        "Ditutup: ${ReportViewModel.dateTimeFmt.format(Instant.ofEpochMilli(it))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text("📋 Ringkasan Shift", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                HorizontalDivider(Modifier.padding(vertical = 2.dp))
                SummaryLine("Penjualan Tunai", summary.cashRevenue.toRupiah())
                SummaryLine("Penjualan QRIS", summary.qrisRevenue.toRupiah())
                HorizontalDivider(Modifier.padding(vertical = 2.dp))
                SummaryLine("Total Pendapatan", summary.totalRevenue.toRupiah(), emphasize = true)
                if (summary.qrisRefunds > 0L) {
                    SummaryLine(
                        "Refund via QRIS",
                        "- ${summary.qrisRefunds.toRupiah()}",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                SummaryLine("Laba Kotor", summary.grossProfit.toRupiah(), color = MaterialTheme.colorScheme.primary)
                if (summary.warrantyExchangeCost > 0L) {
                    SummaryLine(
                        "Biaya Klaim Garansi",
                        "- ${summary.warrantyExchangeCost.toRupiah()}",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Spacer(Modifier.height(14.dp))
                Text("💵 Rekonsiliasi Laci", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                HorizontalDivider(Modifier.padding(vertical = 2.dp))
                SummaryLine("Kas Awal (Modal)", summary.startingCash.toRupiah())
                SummaryLine("Penjualan Tunai", summary.cashRevenue.toRupiah())
                if (summary.cashRefunds > 0L) {
                    SummaryLine(
                        "Refund Tunai",
                        "- ${summary.cashRefunds.toRupiah()}",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                if (summary.qrisCashChangeOut > 0L) {
                    SummaryLine(
                        "Kembalian Tunai (dari QRIS)",
                        "- ${summary.qrisCashChangeOut.toRupiah()}",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                HorizontalDivider(Modifier.padding(vertical = 2.dp))
                SummaryLine("Estimasi di Laci", expected.toRupiah(), emphasize = true)
                SummaryLine("Kas Fisik (Aktual)", actual.toRupiah())
                Spacer(Modifier.height(6.dp))
                val diffAbs = kotlin.math.abs(difference)
                val diffColor =
                    if (difference < 0L) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                val diffLabel =
                    when {
                        difference == 0L -> "Pas ✓"
                        difference < 0L -> "-${diffAbs.toRupiah()} (Uang Kurang)"
                        else -> "+${diffAbs.toRupiah()} (Uang Lebih)"
                    }
                SummaryLine("Selisih", diffLabel, emphasize = true, color = diffColor)
                if (shift.note.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text("Catatan", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Text(
                        shift.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Tutup") }
        },
    )
}

@Composable
private fun EmptyClosedShifts() {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Rounded.History,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Belum ada shift yang ditutup pada hari ini",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SummaryLine(
    label: String,
    value: String,
    emphasize: Boolean = false,
    color: Color = Color.Unspecified,
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = if (emphasize) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal,
            color = color,
        )
        Text(
            value,
            style = if (emphasize) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace, // Monospace Finansial Wajib
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Medium,
            color = color,
        )
    }
}

@Composable
private fun EmptyReport() {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.AutoMirrored.Rounded.ReceiptLong,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Belum ada transaksi pada hari ini",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun MonthGroupCard(
    monthGroup: MonthSalesGroup,
    onTransactionClick: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.10f),
        ),
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Surface(
                onClick = { expanded = !expanded },
                color = Color.Transparent,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                        modifier = Modifier.size(36.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Rounded.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = monthGroup.yearMonth.toString(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "${monthGroup.totalTransactions} transaksi",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Icon(
                        imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = if (expanded) "Ciutkan" else "Buka",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    monthGroup.days.forEach { dayGroup ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = dayGroup.date.toString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(Modifier.width(8.dp))
                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.18f),
                                )
                            }

                            dayGroup.transactions.forEach { tx ->
                                TransactionRow(
                                    tx = tx,
                                    onClick = { onTransactionClick(tx.id) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactReportSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { focusManager.clearFocus() },
            onDone = { focusManager.clearFocus() }
        ),
        modifier = modifier.trackFocusBounds(),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = "Cari",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Cari struk, produk, SKU, kategori...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    innerTextField()
                }
                if (query.isNotEmpty()) {
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        onClick = {
                            onQueryChange("")
                            focusManager.clearFocus()
                        },
                        shape = CircleShape,
                        color = Color.Transparent,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = "Hapus pencarian",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun CompactSquareIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    isError: Boolean = false,
) {
    val color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val borderColor = if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(13.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(20.dp),
                tint = color,
            )
        }
    }
}

@Composable
private fun ReportOptionRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Surface(
        onClick = { onCheckedChange(!checked) },
        shape = RoundedCornerShape(11.dp),
        color = if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = checked,
                onCheckedChange = null,
                modifier = Modifier.scale(0.72f),
            )
        }
    }
}

fun Modifier.trackFocusBounds(): Modifier = this then TrackFocusBoundsElement()

private class TrackFocusBoundsElement : ModifierNodeElement<TrackFocusBoundsNode>() {
    override fun create(): TrackFocusBoundsNode = TrackFocusBoundsNode()

    override fun update(node: TrackFocusBoundsNode) {}

    override fun hashCode(): Int = javaClass.hashCode()

    override fun equals(other: Any?): Boolean = other is TrackFocusBoundsElement

    override fun InspectorInfo.inspectableProperties() {
        name = "trackFocusBounds"
    }
}

private class TrackFocusBoundsNode :
    Modifier.Node(),
    CompositionLocalConsumerModifierNode,
    FocusEventModifierNode,
    GlobalPositionAwareModifierNode {

    private var isFocused = false
    private var lastCoordinates: LayoutCoordinates? = null
    private var activeBoundsState: MutableState<Rect?>? = null

    override fun onFocusEvent(focusState: FocusState) {
        val wasFocused = isFocused
        isFocused = focusState.isFocused

        val activeBounds = currentValueOf(LocalActiveFocusBounds)
        activeBoundsState = activeBounds

        if (isFocused) {
            lastCoordinates?.let { coordinates ->
                if (coordinates.isAttached) {
                    activeBounds.value = coordinates.boundsInRoot()
                }
            }
        } else if (wasFocused) {
            activeBounds.value = null
        }
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        lastCoordinates = coordinates

        if (isFocused && coordinates.isAttached) {
            val activeBounds = currentValueOf(LocalActiveFocusBounds)
            activeBoundsState = activeBounds
            activeBounds.value = coordinates.boundsInRoot()
        }
    }

    override fun onReset() {
        activeBoundsState?.value = null

        isFocused = false
        lastCoordinates = null
        activeBoundsState = null

        super.onReset()
    }

    override fun onDetach() {
        activeBoundsState?.value = null

        isFocused = false
        lastCoordinates = null
        activeBoundsState = null

        super.onDetach()
    }
}