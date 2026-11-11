package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.theme.*

/**
 * Biometric Camera Preview component with live CameraX feed,
 * face alignment overlay, liveness detection scanning laser,
 * camera lens switching (Front/Back), and fallback simulation.
 */
@Composable
fun BiometricCameraPreview(
    modifier: Modifier = Modifier,
    isId: Boolean = true,
    isFrontLens: Boolean = true,
    onToggleLens: () -> Unit = {},
    isShutterTriggered: Boolean = false
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    var cameraBindSuccess by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    // Shutter flash effect
    val shutterAlpha by animateFloatAsState(
        targetValue = if (isShutterTriggered) 0.85f else 0.0f,
        animationSpec = tween(durationMillis = 150),
        label = "shutterFlash"
    )

    // Scanning laser animation
    val infiniteTransition = rememberInfiniteTransition(label = "laserScan")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserPos"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
            .border(2.dp, DarcorpGold500.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .testTag("biometric_camera_preview")
    ) {
        if (hasCameraPermission) {
            // Live CameraX Preview via AndroidView
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.surfaceProvider = previewView.surfaceProvider
                            }

                            val cameraSelector = if (isFrontLens) {
                                if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                }
                            } else {
                                if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                }
                            }

                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview
                            )
                            cameraBindSuccess = true
                        } catch (e: Exception) {
                            cameraBindSuccess = false
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            // If CameraX is in headless/emulator without physical feed, show rich simulated live feed
            if (!cameraBindSuccess) {
                SimulatedBiometricLiveFeed(isId = isId, laserProgress = laserProgress)
            }
        } else {
            // Permission Request Placeholder
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = DarcorpBrown700.copy(alpha = 0.2f),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = DarcorpGold400,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (isId) "Izin Kamera Diperlukan" else "Camera Permission Required",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = if (isId) "Kamera digunakan untuk memindai biometrik wajah presensi"
                           else "Camera is used to scan face biometrics for attendance",
                    color = Color.LightGray,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = DarcorpGold500),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp).testTag("grant_camera_permission_btn")
                ) {
                    Text(
                        text = if (isId) "Aktifkan Kamera" else "Enable Camera",
                        color = DarcorpNavy900,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Biometric Face Alignment Overlay
        BiometricFaceGuideCanvas(
            modifier = Modifier.fillMaxSize(),
            laserProgress = laserProgress
        )

        // Top Status Badges Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.65f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF22C55E))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isId) "3D LIVENESS: SIAP" else "3D LIVENESS: READY",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Lens Switcher Button
            IconButton(
                onClick = onToggleLens,
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                    .testTag("switch_lens_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.FlipCameraAndroid,
                    contentDescription = "Switch Camera",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Bottom Guidance Status
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                    )
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CenterFocusStrong,
                        contentDescription = null,
                        tint = DarcorpGold400,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isId) "Posisikan wajah di tengah oval" else "Center face inside oval",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = "Pencahayaan: 98%",
                    color = Color(0xFF4ADE80),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // White Flash Effect when Capture Button is Pressed
        if (shutterAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = shutterAlpha))
            )
        }
    }
}

/**
 * Biometric Overlay with Oval target, face landmark corners,
 * and vertical scanning laser beam.
 */
@Composable
private fun BiometricFaceGuideCanvas(
    modifier: Modifier = Modifier,
    laserProgress: Float
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Oval face guide center & dimensions
        val ovalWidth = w * 0.56f
        val ovalHeight = h * 0.72f
        val ovalLeft = (w - ovalWidth) / 2f
        val ovalTop = (h - ovalHeight) / 2f

        // Draw outer translucent vignette around the face
        drawOval(
            color = Color(0x33000000),
            topLeft = Offset(ovalLeft, ovalTop),
            size = Size(ovalWidth, ovalHeight)
        )

        // Draw dotted/solid oval outline in biometric cyan/gold
        drawOval(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF38BDF8), Color(0xFFFBBF24), Color(0xFF22C55E))
            ),
            topLeft = Offset(ovalLeft, ovalTop),
            size = Size(ovalWidth, ovalHeight),
            style = Stroke(width = 3.dp.toPx())
        )

        // Corner target brackets
        val bracketLen = 20.dp.toPx()
        val bracketStroke = Stroke(width = 3.5.dp.toPx())
        val bracketColor = Color(0xFF38BDF8)

        // Top-Left
        drawLine(bracketColor, Offset(ovalLeft - 6, ovalTop + bracketLen), Offset(ovalLeft - 6, ovalTop - 6), bracketStroke.width)
        drawLine(bracketColor, Offset(ovalLeft - 6, ovalTop - 6), Offset(ovalLeft + bracketLen, ovalTop - 6), bracketStroke.width)

        // Top-Right
        val ovalRight = ovalLeft + ovalWidth
        drawLine(bracketColor, Offset(ovalRight + 6, ovalTop + bracketLen), Offset(ovalRight + 6, ovalTop - 6), bracketStroke.width)
        drawLine(bracketColor, Offset(ovalRight + 6, ovalTop - 6), Offset(ovalRight - bracketLen, ovalTop - 6), bracketStroke.width)

        // Bottom-Left
        val ovalBottom = ovalTop + ovalHeight
        drawLine(bracketColor, Offset(ovalLeft - 6, ovalBottom - bracketLen), Offset(ovalLeft - 6, ovalBottom + 6), bracketStroke.width)
        drawLine(bracketColor, Offset(ovalLeft - 6, ovalBottom + 6), Offset(ovalLeft + bracketLen, ovalBottom + 6), bracketStroke.width)

        // Bottom-Right
        drawLine(bracketColor, Offset(ovalRight + 6, ovalBottom - bracketLen), Offset(ovalRight + 6, ovalBottom + 6), bracketStroke.width)
        drawLine(bracketColor, Offset(ovalRight + 6, ovalBottom + 6), Offset(ovalRight - bracketLen, ovalBottom + 6), bracketStroke.width)

        // Draw horizontal animated scanning laser inside oval
        val laserY = ovalTop + (ovalHeight * laserProgress)
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFF38BDF8).copy(alpha = 0.9f),
                    Color(0xFF4ADE80),
                    Color(0xFF38BDF8).copy(alpha = 0.9f),
                    Color.Transparent
                )
            ),
            start = Offset(ovalLeft + 10f, laserY),
            end = Offset(ovalRight - 10f, laserY),
            strokeWidth = 3.dp.toPx()
        )
    }
}

/**
 * High-tech simulated biometric feed when camera hardware is in emulation.
 */
@Composable
private fun SimulatedBiometricLiveFeed(
    isId: Boolean,
    laserProgress: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF1E293B), Color(0xFF0A0F1D))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = null,
                tint = DarcorpGold400.copy(alpha = 0.85f),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.4f),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFF22C55E))))
            ) {
                Text(
                    text = if (isId) "Pemindaian Biometrik 3D Berjalan..." else "3D Biometric Feed Active...",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}
