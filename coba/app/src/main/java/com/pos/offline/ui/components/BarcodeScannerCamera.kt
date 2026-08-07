package com.pos.offline.ui.components
import android.app.Activity
import androidx.core.view.WindowCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.ContextCompat
import android.view.WindowManager
import android.content.ContextWrapper
import android.content.Context
import android.util.Size
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.pos.offline.data.local.PosDatabase
import com.pos.offline.util.CameraPermissionState
import com.pos.offline.util.ImageFeatureExtractor
import com.pos.offline.util.VectorUtils.calculateCosineSimilarity
import com.pos.offline.util.VectorUtils.toVectorFloatArray
import com.pos.offline.util.ScanFeedbackManager
import com.pos.offline.util.ScanPreferencesRepository
import com.pos.offline.util.VibrationLevel
import com.pos.offline.util.openAppSettings
import com.pos.offline.util.rememberCameraPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import androidx.compose.ui.geometry.Size as GeometrySize

enum class ScannerMode {
    BARCODE, OBJECT
}

@Composable
fun BarcodeScannerCamera(
    onBarcodeScanned: suspend (String) -> Boolean,
    onObjectScanned: suspend (Long) -> Boolean, // Callback untuk mendeteksi produk via ID database berdasarkan AI
    isMultiScanMode: Boolean,
    scannerMode: ScannerMode,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val executor = remember { Executors.newSingleThreadExecutor() }
    
    // Inisialisasi TFLite Feature Extractor secara aman
    val featureExtractor = remember {
        try {
            ImageFeatureExtractor(context)
        } catch (e: Exception) {
            null
        }
    }
    
    val scanner = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_EAN_13,
                    Barcode.FORMAT_EAN_8,
                    Barcode.FORMAT_UPC_A,
                    Barcode.FORMAT_UPC_E,
                    Barcode.FORMAT_CODE_128,
                ).build(),
        )
    }
    
    var lastScannedCode by remember { mutableStateOf<String?>(null) }
    var lastScannedTime by remember { mutableLongStateOf(0L) }
    var pendingCode by remember { mutableStateOf<String?>(null) }
    var pendingCodeCount by remember { mutableIntStateOf(0) }
    var scanVisualState by remember { mutableStateOf(ScanVisualState.IDLE) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }
    var cameraError by remember { mutableStringState("Gagal membuka kamera.") }

    fun attemptBind() {
        val provider = cameraProvider ?: return
        val previewView = previewViewRef ?: return
        try {
            if (executor.isShutdown) return
            val resolutionStrategy =
                ResolutionStrategy(
                    Size(720, 1280),
                    ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER,
                )
            val resolutionSelector =
                ResolutionSelector.Builder()
                    .setResolutionStrategy(resolutionStrategy)
                    .build()
            val preview =
                Preview.Builder()
                    .setResolutionSelector(resolutionSelector)
                    .build()
                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }
            
            val analysis =
                ImageAnalysis.Builder()
                    .setResolutionSelector(resolutionSelector)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

            analysis.setAnalyzer(executor) { proxy ->
                if (scannerMode == ScannerMode.BARCODE) {
                    // ===== MODE BARCODE (100% Original ML Kit) =====
                    val mediaImage = proxy.image
                    if (mediaImage == null) {
                        proxy.close()
                        return@setAnalyzer
                    }
                    try {
                        val rotationDegrees = proxy.imageInfo.rotationDegrees
                        val input = InputImage.fromMediaImage(mediaImage, rotationDegrees)
                        scanner.process(input)
                            .addOnSuccessListener { barcodes ->
                                val targetBarcode = barcodes.firstOrNull { barcode ->
                                    val raw = barcode.rawValue
                                    if (raw.isNullOrBlank()) return@firstOrNull false
                                    val rect = barcode.boundingBox ?: return@firstOrNull false
                                    val viewW = previewView.width.toFloat()
                                    val viewH = previewView.height.toFloat()
                                    if (viewW == 0f || viewH == 0f) return@firstOrNull false
                                    val isRotated = rotationDegrees == 90 || rotationDegrees == 270
                                    val imgW = if (isRotated) proxy.height.toFloat() else proxy.width.toFloat()
                                    val imgH = if (isRotated) proxy.width.toFloat() else proxy.width.toFloat() // Fallback
                                    val scale = maxOf(viewW / imgW, viewH / imgH)
                                    val scaledImgW = imgW * scale
                                    val scaledImgH = imgH * scale
                                    val offsetX = (scaledImgW - viewW) / 2f
                                    val offsetY = (scaledImgH - viewH) / 2f
                                    val screenLeft = (rect.left * scale) - offsetX
                                    val screenRight = (rect.right * scale) - offsetX
                                    val screenTop = (rect.top * scale) - offsetY
                                    val screenBottom = (rect.bottom * scale) - offsetY
                                    val boxLeft = viewW * 0.15f
                                    val boxRight = viewW * 0.85f
                                    val boxTop = viewH * 0.35f
                                    val boxBottom = viewH * 0.65f
                                    screenLeft >= boxLeft && screenRight <= boxRight &&
                                        screenTop >= boxTop && screenBottom <= boxBottom
                                }
                                if (targetBarcode != null) {
                                    val code = targetBarcode.rawValue ?: return@addOnSuccessListener
                                    if (code == pendingCode) {
                                        pendingCodeCount++
                                        if (pendingCodeCount >= 2) {
                                            val currentTime = System.currentTimeMillis()
                                            val isSameCode = (code == lastScannedCode)
                                            val isTimeElapsed = (currentTime - lastScannedTime) > 2000L
                                            if (!isSameCode || isTimeElapsed) {
                                                lastScannedCode = code
                                                lastScannedTime = currentTime
                                                coroutineScope.launch {
                                                    val isSuccess = onBarcodeScanned(code)
                                                    scanVisualState = if (isSuccess) ScanVisualState.SUCCESS else ScanVisualState.ERROR
                                                    delay(400L)
                                                    scanVisualState = ScanVisualState.IDLE
                                                }
                                            }
                                        }
                                    } else {
                                        pendingCode = code
                                        pendingCodeCount = 1
                                    }
                                } else {
                                    pendingCode = null
                                    pendingCodeCount = 0
                                }
                            }.addOnCompleteListener { proxy.close() }
                    } catch (e: Exception) {
                        proxy.close()
                    }
                } else {
                    // ===== MODE SCAN OBJEK AI (TFLite MobileNet-V4) =====
                    val currentTime = System.currentTimeMillis()
                    // Beri jeda 1,5 detik antar pemindaian AI agar CPU tidak bekerja terlalu berat terus menerus
                    if (currentTime - lastScannedTime < 1500L || featureExtractor == null) {
                        proxy.close()
                        return@setAnalyzer
                    }

                    try {
                        val bitmap = proxy.toBitmap() // Didukung penuh oleh CameraX 1.7.0
                        val scannedVector = featureExtractor.extractFeatures(bitmap)
                        
                        coroutineScope.launch(Dispatchers.IO) {
                            // Ambil semua produk yang memiliki data vektor objek dari database Room
                            val productsWithVector = PosDatabase.getInstance(context).productDao().getProductsWithObjectVector()
                            
                            var bestMatchId: Long = -1
                            var highestSimilarity = 0f
                            
                            // Bandingkan dengan setiap produk menggunakan Cosine Similarity
                            for (product in productsWithVector) {
                                val dbVector = product.objectVector?.toVectorFloatArray() ?: continue
                                val similarity = calculateCosineSimilarity(scannedVector, dbVector)
                                if (similarity > highestSimilarity) {
                                    highestSimilarity = similarity
                                    bestMatchId = product.id
                                }
                            }
                            
                            // Ambang batas kemiripan (Threshold) 0.65 (65% mirip dianggap valid)
                            if (highestSimilarity >= 0.65f && bestMatchId != -1L) {
                                lastScannedTime = System.currentTimeMillis()
                                val isSuccess = onObjectScanned(bestMatchId)
                                withContext(Dispatchers.Main) {
                                    scanVisualState = if (isSuccess) ScanVisualState.SUCCESS else ScanVisualState.ERROR
                                    delay(400L)
                                    scanVisualState = ScanVisualState.IDLE
                                }
                            }
                        }
                        proxy.close()
                    } catch (e: Exception) {
                        proxy.close()
                    }
                }
            }
            provider.unbindAll()
            provider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                analysis,
            )
            cameraError = null
        } catch (e: Exception) {
            cameraError = "Gagal membuka kamera."
        }
    }

    DisposableEffect(Unit) {
        lastScannedCode = null
        lastScannedTime = 0L
        pendingCode = null
        pendingCodeCount = 0
        onDispose {
            cameraProvider?.unbindAll()
            executor.shutdown()
            scanner.close()
            featureExtractor?.close()
            scanVisualState = ScanVisualState.IDLE
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { c: Context ->
                val previewView = PreviewView(c).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
                previewViewRef = previewView
                val providerFuture = ProcessCameraProvider.getInstance(c)
                providerFuture.addListener({
                    try {
                        cameraProvider = providerFuture.get()
                        attemptBind()
                    } catch (e: Exception) {
                        cameraError = "Gagal membuka kamera."
                    }
                }, ContextCompat.getMainExecutor(c))
                previewView
            },
        )
        ScannerViewfinder(
            scanState = scanVisualState,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun rememberBarcodeScanner(
    onScanned: suspend (String) -> String?,
    onObjectScanned: suspend (Long) -> String? = { null } // Callback opsional untuk objek
): () -> Unit {
    val context = LocalContext.current
    val onScannedState = rememberUpdatedState(onScanned)
    val onObjectScannedState = rememberUpdatedState(onObjectScanned)
    val feedbackManager = remember { ScanFeedbackManager(context) }
    val prefsRepository = remember { ScanPreferencesRepository(context) }
    
    val isSoundEnabled by prefsRepository.isSoundEnabled.collectAsStateWithLifecycle()
    val soundVolume by prefsRepository.soundVolume.collectAsStateWithLifecycle()
    val soundDurationMs by prefsRepository.soundDurationMs.collectAsStateWithLifecycle()
    val isVibrationEnabled by prefsRepository.isVibrationEnabled.collectAsStateWithLifecycle()
    val vibrationIntensity by prefsRepository.vibrationIntensity.collectAsStateWithLifecycle()
    val vibrationDurationMs by prefsRepository.vibrationDurationMs.collectAsStateWithLifecycle()
    
    val vibrationLevel = remember(vibrationIntensity) {
        when {
            vibrationIntensity <= 35 -> VibrationLevel.HALUS
            vibrationIntensity <= 70 -> VibrationLevel.SEDANG
            else -> VibrationLevel.KUAT
        }
    }
    
    val (permState, requestPermission) = rememberCameraPermissionState()
    var showScanner by remember { mutableStateOf(false) }
    var pendingOpen by remember { mutableStateOf(false) }
    var showDeniedDialog by remember { mutableStateOf(false) }
    var isMultiScanMode by remember { mutableStateOf(false) }
    var scannerMode by remember { mutableStateOf(ScannerMode.BARCODE) } // State Mode Kamera
    var scannedCountBatch by remember { mutableIntStateOf(0) }
    var lastScannedCodeText by remember { mutableStateOf("") }
    var scanErrorMessage by remember { mutableStringState(null) }

    LaunchedEffect(scanErrorMessage) {
        if (scanErrorMessage != null) {
            delay(2500L)
            scanErrorMessage = null
        }
    }

    LaunchedEffect(showScanner) {
        if (showScanner) {
            isMultiScanMode = false
            scannedCountBatch = 0
            lastScannedCodeText = ""
            scanErrorMessage = null
        }
    }

    DisposableEffect(Unit) {
        onDispose { feedbackManager.release() }
    }

    LaunchedEffect(permState) {
        if (pendingOpen) {
            when (permState) {
                CameraPermissionState.GRANTED -> {
                    showScanner = true
                    pendingOpen = false
                }
                CameraPermissionState.PERMANENTLY_DENIED -> {
                    showDeniedDialog = true
                    pendingOpen = false
                }
                else -> {}
            }
        }
    }

    if (showDeniedDialog) {
        val permanentlyDenied = permState == CameraPermissionState.PERMANENTLY_DENIED
        AlertDialog(
            onDismissRequest = { showDeniedDialog = false },
            title = { Text(if (permanentlyDenied) "Izin Kamera Diblokir" else "Izin Kamera Diperlukan") },
            text = { Text("Akses kamera dibutuhkan untuk memindai barang.") },
            confirmButton = {
                Button(onClick = {
                    showDeniedDialog = false
                    pendingOpen = true
                    if (permanentlyDenied) openAppSettings(context) else requestPermission()
                }) {
                    Text(if (permanentlyDenied) "Buka Pengaturan" else "Izinkan")
                }
            },
            dismissButton = { TextButton(onClick = { showDeniedDialog = false }) { Text("Tutup") } },
        )
    }

    if (showScanner) {
        Dialog(
            onDismissRequest = { showScanner = false },
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
        ) {
            val view = LocalView.current
            LaunchedEffect(view) {
                (view.parent as? DialogWindowProvider)?.window?.let { window ->
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                }
            }
            BackHandler { showScanner = false }

            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                BarcodeScannerCamera(
                    isMultiScanMode = isMultiScanMode,
                    scannerMode = scannerMode,
                    onBarcodeScanned = { code ->
                        val scannedName = onScannedState.value(code)
                        val isSuccess = scannedName != null
                        if (isSuccess) {
                            scanErrorMessage = null
                            feedbackManager.triggerSuccessFeedback(isSoundEnabled, soundVolume, soundDurationMs, isVibrationEnabled, vibrationLevel, vibrationDurationMs)
                            scannedCountBatch++
                            lastScannedCodeText = scannedName
                            if (!isMultiScanMode) showScanner = false
                        } else {
                            scanErrorMessage = "Produk tidak ditemukan ($code)"
                            feedbackManager.triggerFailureFeedback(isSoundEnabled, soundVolume, isVibrationEnabled, vibrationLevel)
                        }
                        isSuccess
                    },
                    onObjectScanned = { productId ->
                        val scannedName = onObjectScannedState.value(productId)
                        val isSuccess = scannedName != null
                        if (isSuccess) {
                            scanErrorMessage = null
                            feedbackManager.triggerSuccessFeedback(isSoundEnabled, soundVolume, soundDurationMs, isVibrationEnabled, vibrationLevel, vibrationDurationMs)
                            scannedCountBatch++
                            lastScannedCodeText = scannedName
                            if (!isMultiScanMode) showScanner = false
                        } else {
                            scanErrorMessage = "Objek tidak dikenali di database"
                            feedbackManager.triggerFailureFeedback(isSoundEnabled, soundVolume, isVibrationEnabled, vibrationLevel)
                        }
                        isSuccess
                    },
                    modifier = Modifier.fillMaxSize(),
                )

                // Tombol Switch Mode di Atas (Barcode vs Objek AI)
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = { scannerMode = ScannerMode.BARCODE },
                        shape = RoundedCornerShape(20.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = if (scannerMode == ScannerMode.BARCODE) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                    ) {
                        Icon(Icons.Rounded.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Barcode", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { scannerMode = ScannerMode.OBJECT },
                        shape = RoundedCornerShape(20.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = if (scannerMode == ScannerMode.OBJECT) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                    ) {
                        Icon(Icons.Rounded.Camera, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Scan Objek AI", fontSize = 12.sp)
                    }
                }

                // Tombol Tutup Scanner (Kanan Atas)
                IconButton(
                    onClick = { showScanner = false },
                    modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(16.dp),
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Tutup", tint = Color.White)
                }

                // Panel Kontrol Bawah (Multi-scan & Status)
                Card(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.85f)),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = if (scannerMode == ScannerMode.BARCODE) "Arahkan ke Barcode Produk" else "Arahkan Kamera ke Bentuk Produk", color = Color.White, fontSize = 12.sp)
                            Switch(checked = isMultiScanMode, onCheckedChange = { isMultiScanMode = it })
                        }
                        Button(onClick = { showScanner = false }, modifier = Modifier.fillMaxWidth()) {
                            Text("Selesai ($scannedCountBatch Item)")
                        }
                    }
                }
            }
        }
    }

    return {
        when (permState) {
            CameraPermissionState.GRANTED -> showScanner = true
            else -> { pendingOpen = true; requestPermission() }
        }
    }
}

// Fungsi bantu state string ringkas
private fun mutableStringState(value: String?) = mutableStateOf(value)

@Composable
private fun ScannerViewfinder(scanState: ScanVisualState, modifier: Modifier = Modifier) {
    val animatedColor by animateColorAsState(
        targetValue = when (scanState) {
            ScanVisualState.IDLE -> Color.White
            ScanVisualState.SUCCESS -> Color(0xFF4CAF50)
            ScanVisualState.ERROR -> Color(0xFFF44336)
        },
        animationSpec = tween(200), label = ""
    )
    Box(modifier = modifier.drawWithCache {
        val boxWidth = size.width * 0.75f
        val boxHeight = size.height * 0.35f
        val left = (size.width - boxWidth) / 2f
        val top = (size.height - boxHeight) / 2f
        val right = left + boxWidth
        val bottom = top + boxHeight
        val cornerRadiusPx = 16.dp.toPx()
        onDrawWithContent {
            drawContent()
            drawRect(Color.Black.copy(alpha = 0.5f))
        }
    })
}

enum class ScanVisualState { IDLE, SUCCESS, ERROR }
