package com.pos.offline

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pos.offline.data.di.ServiceLocator
import com.pos.offline.ui.components.GlobalMessageController
import com.pos.offline.ui.components.TopAlignedMessagePill
import com.pos.offline.ui.inventory.InventoryScreen
import com.pos.offline.ui.inventory.InventoryViewModel
import com.pos.offline.ui.pos.PosScreen
import com.pos.offline.ui.pos.PosViewModel
import com.pos.offline.ui.receipt.ReceiptManager
import com.pos.offline.ui.report.ReportScreen
import com.pos.offline.ui.report.ReportViewModel
import com.pos.offline.ui.settings.PrinterViewModel
import com.pos.offline.ui.settings.SettingsScreen
import com.pos.offline.ui.settings.SettingsViewModel
import com.pos.offline.ui.settings.StoreProfileViewModel
import com.pos.offline.ui.theme.PosTheme
import com.pos.offline.util.HardwareScannerInterceptor
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.geometry.Rect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.MutableState

private enum class Dest(
    val label: String,
) {
    POS("Kasir"),
    INVENTORY("Inventaris"),
    REPORT("Laporan"),
    SETTINGS("Pengaturan"),
}
val LocalActiveFocusBounds = staticCompositionLocalOf<MutableState<Rect?>> {
    error("LocalActiveFocusBounds belum disediakan")
}
class MainActivity : ComponentActivity() {

    private val posViewModel: PosViewModel by viewModels {
        ServiceLocator.posViewModelFactory()
    }

    private val scannerInterceptor =
        HardwareScannerInterceptor { barcode ->
            lifecycleScope.launch {
                posViewModel.onBarcodeScanned(barcode)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            PosTheme {
                AppRoot()
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (scannerInterceptor.onKeyEvent(event)) return true
        return super.dispatchKeyEvent(event)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AppRoot(
    modifier: Modifier = Modifier,
    storeProfileViewModel: StoreProfileViewModel = viewModel(factory = ServiceLocator.storeProfileViewModelFactory()),
    settingsViewModel: SettingsViewModel = viewModel(factory = ServiceLocator.settingsViewModelFactory()),
    posViewModel: PosViewModel = viewModel(factory = ServiceLocator.posViewModelFactory())
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val messageController =
        remember {
            GlobalMessageController(scope)
        }

    val focusManager = LocalFocusManager.current

    val pagerState =
        rememberPagerState(
            initialPage = 0,
        ) {
            Dest.entries.size
        }

    val currentDest = Dest.entries[pagerState.currentPage]

val storeProfile by
    storeProfileViewModel.profile.collectAsStateWithLifecycle()

val settingsUiState by
    settingsViewModel.uiState.collectAsStateWithLifecycle()

    val isRestoringDatabase = settingsUiState.isImporting

    val pageAlpha = remember {
        Animatable(1f)
    }

    var isJumping by remember {
        mutableStateOf(false)
    }

    fun goTo(dest: Dest) {
        val target = dest.ordinal

        if (pagerState.currentPage == target) return

        scope.launch {
            isJumping = true

            pageAlpha.animateTo(
                0f,
                animationSpec = tween(90),
            )

            pagerState.scrollToPage(target)

            pageAlpha.animateTo(
                1f,
                animationSpec = tween(140),
            )

            isJumping = false
        }
    }

val openShift by
    posViewModel.openShift.collectAsStateWithLifecycle()

    var showExitDialog by rememberSaveable {
        mutableStateOf(false)
    }

    BackHandler(
        enabled = isRestoringDatabase,
    ) {
        // Block back while database restore is running.
    }

    BackHandler(
        enabled = showExitDialog,
    ) {
        showExitDialog = false
    }

    BackHandler(
        enabled =
            currentDest != Dest.POS &&
                !isRestoringDatabase &&
                !showExitDialog,
    ) {
        goTo(Dest.POS)
    }

    BackHandler(
        enabled =
            currentDest == Dest.POS &&
                !showExitDialog &&
                !isRestoringDatabase,
    ) {
        showExitDialog = true
    }

    if (showExitDialog) {
        val shift = openShift

        if (shift != null) {
            AlertDialog(
                onDismissRequest = {
                    showExitDialog = false
                },
                containerColor = MaterialTheme.colorScheme.surface,
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(28.dp),
                    )
                },
                title = {
                    Text(
                        text = "Ada Shift Kasir Aktif!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text =
                                "Shift kasir atas nama ${shift.cashierName} masih berjalan. " +
                                    "Untuk keakuratan laporan keuangan dan laci kas " +
                                    "(rekonsiliasi uang fisik), sangat disarankan untuk " +
                                    "menutup shift terlebih dahulu di tab Kasir.",
                            fontSize = 13.sp,
                        )

                        Text(
                            text =
                                "Catatan: Jika Anda memilih 'Tetap Keluar', sesi shift " +
                                    "akan tetap aktif menggantung dan harus ditutup secara " +
                                    "normal saat aplikasi dibuka kembali.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                },
                confirmButton = {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    ) {
                        TextButton(
                            onClick = {
                                showExitDialog = false
                                (context as? android.app.Activity)?.finishAndRemoveTask()
                            },
                            colors =
                                ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error,
                                ),
                        ) {
                            Text(text = "Tetap Keluar", fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { showExitDialog = false },
                        ) {
                            Text(text = "Batal", fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                showExitDialog = false
                                goTo(Dest.POS)
                            },
                        ) {
                            Text(text = "Tutup Shift Dulu", fontSize = 13.sp)
                        }
                    }
                },
                dismissButton = {},
            )
        } else {
            AlertDialog(
                onDismissRequest = {
                    showExitDialog = false
                },
                containerColor = MaterialTheme.colorScheme.surface,
                title = {
                    Text(
                        text = "Keluar Aplikasi?",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                text = {
                    Text(
                        text =
                            "Semua data transaksi dan laci kas Anda telah tersimpan " +
                                "dengan aman di database lokal. Sesi kasir Anda saat ini " +
                                "bersih (tidak ada shift berjalan). Keluar sekarang?",
                        fontSize = 13.sp,
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showExitDialog = false

                            (context as? android.app.Activity)
                                ?.finishAndRemoveTask()
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    MaterialTheme.colorScheme.error,
                            ),
                    ) {
                        Text(
                            text = "Keluar",
                            fontSize = 13.sp,
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showExitDialog = false
                        },
                    ) {
                        Text("Batal", fontSize = 13.sp)
                    }
                },
            )
        }
    }

    val density = LocalDensity.current

    val imeVisible =
        WindowInsets.ime.getBottom(density) > 0

    val isLandscape =
        LocalConfiguration.current.orientation ==
            Configuration.ORIENTATION_LANDSCAPE

    var isCartExpanded by remember {
        mutableStateOf(false)
    }

    var menuExpanded by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(pagerState.currentPage) {
        if (currentDest != Dest.POS) {
            isCartExpanded = false
        }

        menuExpanded = false
    }

    val hideFab =
        isRestoringDatabase ||
            imeVisible ||
            (
                !isLandscape &&
                    currentDest == Dest.POS &&
                    isCartExpanded
            )

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(Unit) {
                    detectTapGestures {
                        focusManager.clearFocus()
                    }
                },
    ) {
        @OptIn(ExperimentalFoundationApi::class)
        CompositionLocalProvider(
            LocalOverscrollFactory provides null,
        ) {
            HorizontalPager(
                state = pagerState,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = pageAlpha.value
                        },
                userScrollEnabled =
                    !menuExpanded &&
                        !imeVisible &&
                        !isJumping &&
                        !isRestoringDatabase,
                flingBehavior =
                    PagerDefaults.flingBehavior(
                        state = pagerState,
                    ),
            ) { page ->

                val dest = Dest.entries[page]

                when (dest) {
                    Dest.POS -> {
                        PosScreen(
                            forceWideLayout = isLandscape,
                            onNavigateToSettings = {
                                goTo(Dest.SETTINGS)
                            },
                            onSharePdfFile = { file ->
                                context.startActivity(
                                    ReceiptManager.buildPdfShareIntent(
                                        context,
                                        file,
                                    ),
                                )
                            },
                            onExportPdf = { result ->
                                scope.launch {
                                    val file =
                                        ReceiptManager.exportToPdf(
                                            context,
                                            result,
                                            storeProfile,
                                        )

                                    Toast.makeText(
                                        context,
                                        "Struk tersimpan: ${file.name}",
                                        Toast.LENGTH_LONG,
                                    ).show()
                                }
                            },
                            isCartExpanded =
                                if (isLandscape) {
                                    false
                                } else {
                                    isCartExpanded
                                },
                            onCartExpandedChange =
                                if (isLandscape) {
                                    {}
                                } else {
                                    { value: Boolean ->
                                        isCartExpanded = value
                                    }
                                },
                        )
                    }

                    Dest.INVENTORY -> {
                        InventoryScreen()
                    }

                    Dest.REPORT -> {
                        ReportScreen(
                            onNavigateToSettings = {
                                goTo(Dest.SETTINGS)
                            },
                            onSharePdfFile = { file ->
                                context.startActivity(
                                    ReceiptManager.buildPdfShareIntent(
                                        context,
                                        file,
                                    ),
                                )
                            },
                            onExportPdf = { result ->
                                scope.launch {
                                    val file =
                                        ReceiptManager.exportToPdf(
                                            context,
                                            result,
                                            storeProfile,
                                        )

                                    Toast.makeText(
                                        context,
                                        "Struk tersimpan: ${file.name}",
                                        Toast.LENGTH_LONG,
                                    ).show()
                                }
                            },
                            onShare = { result ->
                                context.startActivity(
                                    ReceiptManager.buildShareIntent(
                                        context,
                                        result,
                                    ),
                                )
                            },
                        )
                    }

                    Dest.SETTINGS -> {
                        SettingsScreen(
                            onExitClick = {
                                showExitDialog = true
                            },
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = menuExpanded,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(alpha = 0.35f),
                        )
                        .clickable(
                            interactionSource =
                                remember {
                                    MutableInteractionSource()
                                },
                            indication = null,
                        ) {
                            menuExpanded = false
                        },
            )
        }

        val fabBottomPadding = 6.dp

        AnimatedVisibility(
            visible = !hideFab,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        bottom = fabBottomPadding,
                    )
                    .navigationBarsPadding(),
        ) {
            ExpandableMenuFab(
                expanded = menuExpanded,
                onToggle = {
                    menuExpanded = !menuExpanded
                },
                selected = currentDest,
                onSelect = { dest ->
                    goTo(dest)
                    menuExpanded = false
                },
            )
        }

        AnimatedVisibility(
            visible = isRestoringDatabase,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(alpha = 0.75f),
                        )
                        .clickable(
                            interactionSource =
                                remember {
                                    MutableInteractionSource()
                                },
                            indication = null,
                        ) {
                            // Block click while restoring.
                        },
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp),
                    )

                    Text(
                        text = "Memulihkan cadangan…",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp),
                    )

                    Text(
                        text =
                            "Jangan tutup aplikasi. " +
                                "Aplikasi akan otomatis restart.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                    )
                }
            }
        }

        TopAlignedMessagePill(
            message = messageController.currentMessage,
            onDismiss = {
                messageController.dismiss()
            },
        )
    }
}

@Composable
private fun ExpandableMenuFab(
    expanded: Boolean,
    onToggle: () -> Unit,
    selected: Dest,
    onSelect: (Dest) -> Unit,
    modifier: Modifier = Modifier,
) {
    val expandFraction by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        label = "fab_expand",
    )

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier.padding(bottom = 1.dp),
    ) {
        Dest.entries.forEachIndexed { index, dest ->

            val startAngle = 160.0
            val endAngle = 20.0

            val angle =
                startAngle -
                    (
                        index *
                            (startAngle - endAngle) /
                            (Dest.entries.size - 1)
                    )

            val angleRad = Math.toRadians(angle)

            val radius = 80.dp

            val xOffset =
                (cos(angleRad) * radius.value).dp *
                    expandFraction

            val yOffset =
                (-sin(angleRad) * radius.value).dp *
                    expandFraction

            if (expandFraction > 0f) {
                Column(
                    modifier =
                        Modifier
                            .offset(
                                x = xOffset,
                                y = yOffset,
                            )
                            .graphicsLayer {
                                scaleX =
                                    0.3f +
                                        (0.7f * expandFraction)

                                scaleY =
                                    0.3f +
                                        (0.7f * expandFraction)

                                alpha = expandFraction
                            },
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.spacedBy(6.dp),
                ) {
                    MiniMenuItem(
                        dest = dest,
                        isSelected = dest == selected,
                        onClick = {
                            onSelect(dest)
                        },
                    )

                    Text(
                        text = dest.label,
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                            ),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        Surface(
            modifier =
                Modifier
                    .size(40.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                    )
                    .graphicsLayer {
                        rotationZ = expandFraction * 135f
                    },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            onClick = onToggle,
        ) {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                AnimatedContent(
                    targetState = expanded,
                    label = "fab_icon_morph",
                ) { isExpanded ->

                    Icon(
                        imageVector =
                            if (isExpanded) {
                                Icons.Rounded.Close
                            } else {
                                Icons.Rounded.Menu
                            },
                        contentDescription =
                            if (isExpanded) {
                                "Tutup menu"
                            } else {
                                "Buka menu"
                            },
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniMenuItem(
    dest: Dest,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .size(36.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = CircleShape,
                ),
        shape = CircleShape,
        color =
            if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
        onClick = onClick,
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = dest.icon(),
                contentDescription = dest.label,
                tint =
                    if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

private fun Dest.icon() =
    when (this) {
        Dest.POS -> Icons.Rounded.ShoppingCart
        Dest.INVENTORY -> Icons.Rounded.Inventory2
        Dest.REPORT -> Icons.Rounded.Assessment
        Dest.SETTINGS -> Icons.Rounded.Settings
    }