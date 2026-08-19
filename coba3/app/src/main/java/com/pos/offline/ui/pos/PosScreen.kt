package com.pos.offline.ui.pos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.pos.offline.data.repository.CheckoutResult
import com.pos.offline.ui.components.LocalGlobalMessage
import com.pos.offline.ui.components.rememberBarcodeScanner
import java.io.File
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pos.offline.data.di.ServiceLocator
import androidx.compose.runtime.rememberUpdatedState

/**
 * Stateful Composable (Route Level)
 * Bertugas mengelola integrasi ViewModel, Lifecycle, dan Event Collection.
 */
@Composable
fun PosScreen(
    onNavigateToSettings: () -> Unit,
    onSharePdfFile: (File) -> Unit,
    onExportPdf: (CheckoutResult) -> Unit,
    modifier: Modifier = Modifier,
    forceWideLayout: Boolean = false,
    isCartExpanded: Boolean = false,
    onCartExpandedChange: (Boolean) -> Unit = {},
    viewModel: PosViewModel = viewModel(factory = ServiceLocator.posViewModelFactory()),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val localState = rememberPosLocalState()
    val globalMessage = LocalGlobalMessage.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(isCartExpanded) {
        if (localState.isCartExpanded != isCartExpanded) {
            localState.updateCartExpanded(isCartExpanded)
        }
    }

    val currentOnCartExpandedChange by rememberUpdatedState(onCartExpandedChange)
    LaunchedEffect(localState.isCartExpanded) {
        if (localState.isCartExpanded != isCartExpanded) {
            currentOnCartExpandedChange(localState.isCartExpanded)
        }
    }

    LaunchedEffect(viewModel, lifecycleOwner, globalMessage) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEvents.collect { event ->
                when (event) {
                    is PosUiEvent.ShowMessage -> {
                        globalMessage.showMessage(message = event.message)
                    }
                }
            }
        }
    }

    val onAction: (PosAction) -> Unit = remember(viewModel) { { action -> viewModel.onAction(action) } }
    val onBarcodeScanned: suspend (String) -> String? = remember(viewModel) { { code -> viewModel.onBarcodeScanned(code) } }
    val launchScanner = rememberBarcodeScanner(onScanned = onBarcodeScanned)

    PosScreenContent(
        uiState = uiState,
        localState = localState,
        onAction = onAction,
        launchScanner = launchScanner,
        onNavigateToSettings = onNavigateToSettings,
        onSharePdfFile = onSharePdfFile,
        onExportPdf = onExportPdf,
        modifier = modifier,
        forceWideLayout = forceWideLayout,
    )
}

/**
 * Stateless Composable (UI Level)
 * Sepenuhnya bebas dari ViewModel, 100% STABLE, Skippable, dan mudah di-test/preview.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreenContent(
    uiState: PosUiState,
    localState: PosLocalStateHolder,
    onAction: (PosAction) -> Unit,
    launchScanner: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onSharePdfFile: (File) -> Unit,
    onExportPdf: (CheckoutResult) -> Unit,
    modifier: Modifier = Modifier,
    forceWideLayout: Boolean = false,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            PosTopBar(
                uiState = uiState,
                onAction = onAction,
                launchScanner = launchScanner,
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { inner ->
        BoxWithConstraints(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime)),
        ) {
            val isWide = forceWideLayout || maxWidth >= 840.dp
            val windowInfo = LocalWindowInfo.current
            val density = LocalDensity.current
            val maxH = with(density) { windowInfo.containerSize.height.toDp() }
            val imeVisible = WindowInsets.ime.getBottom(density) > 0

            if (isWide) {
                Row(Modifier.fillMaxSize()) {
                    ProductPane(
                        products = uiState.catalog.products,
                        cartQtyByProductId = uiState.catalog.cartQtyByProductId,
                        cartItems = uiState.cart.items,
                        onAction = onAction,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                    Spacer(Modifier.width(12.dp))
                    CartPaneContent(
                        cart = uiState.cart,
                        payment = uiState.payment,
                        catalog = uiState.catalog,
                        checkout = uiState.checkout,
                        localState = localState,
                        onAction = onAction,
                        collapsible = false,
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(min = 320.dp, max = 420.dp),
                    )
                }
            } else {
                Box(Modifier.fillMaxSize()) {
                    ProductPane(
                        products = uiState.catalog.products,
                        cartQtyByProductId = uiState.catalog.cartQtyByProductId,
                        cartItems = uiState.cart.items,
                        onAction = onAction,
                        modifier = Modifier.fillMaxSize(),
                    )
                    CartPaneContent(
                        cart = uiState.cart,
                        payment = uiState.payment,
                        catalog = uiState.catalog,
                        checkout = uiState.checkout,
                        localState = localState,
                        onAction = onAction,
                        collapsible = true,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .let { base ->
                                if (imeVisible) {
                                    base
                                } else {
                                    base.heightIn(max = maxH * 0.65f)
                                }
                            },
                    )
                }
            }
        }
    }

    PosDialogManager(
        uiState = uiState,
        localState = localState,
        onAction = onAction,
        onSharePdfFile = onSharePdfFile,
        onExportPdf = onExportPdf,
        onNavigateToSettings = onNavigateToSettings,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PosTopBar(
    uiState: PosUiState,
    onAction: (PosAction) -> Unit,
    launchScanner: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shift = uiState.shift
    val catalog = uiState.catalog

    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
        Column(
            modifier =
                modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp)
                    .padding(top = 4.dp, bottom = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(modifier = Modifier.zIndex(10f)) {
                ShiftIndicatorBar(
                    openShift = shift.activeShift,
                    isOpeningDrawer = shift.isOpeningDrawer,
                    onClick = {
                        val currentActiveShift = shift.activeShift
                        when {
                            currentActiveShift != null -> {
                                onAction(PosAction.OpenEndShiftDialog(currentActiveShift))
                            }
                            shift.openShifts.isEmpty() -> {
                                onAction(PosAction.OpenStartShiftDialog)
                            }
                            else -> {
                                onAction(PosAction.OpenShiftListDialog)
                            }
                        }
                    },
                    onManageClick = { onAction(PosAction.OpenShiftListDialog) },
                    onOpenDrawerClick = { onAction(PosAction.OpenCashDrawer) },
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                CompactSearchBar(
                    query = catalog.searchQuery,
                    onQueryChange = { onAction(PosAction.Search(it)) },
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(34.dp),
                )
                OutlinedButton(
                    onClick = launchScanner,
                    modifier = Modifier.height(34.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                ) {
                    Icon(
                        Icons.Rounded.QrCodeScanner,
                        contentDescription = "Scan Barcode",
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Scan", fontSize = 12.sp)
                }
            }

            if (catalog.categories.isNotEmpty()) {
                CategoryChipsRow(
                    categories = catalog.categories,
                    selected = catalog.selectedCategory,
                    onSelect = { onAction(PosAction.SelectCategory(it)) },
                )
            }
        }
    }
}