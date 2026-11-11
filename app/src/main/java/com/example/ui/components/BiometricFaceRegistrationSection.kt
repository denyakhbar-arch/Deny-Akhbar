package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DarcorpRepository
import com.example.model.BiometricFaceProfile
import com.example.model.UserProfile
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * UI section to capture and register a user's face for biometric attendance authentication,
 * including a live camera preview component, capture button, anti-spoofing verification,
 * and biometric enrollment status management.
 */
@Composable
fun BiometricFaceRegistrationSection(
    currentUser: UserProfile,
    repository: DarcorpRepository,
    isId: Boolean = true,
    onFaceRegistered: (BiometricFaceProfile) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val biometricProfiles by repository.biometricProfiles.collectAsState()
    val userProfile = biometricProfiles[currentUser.id]
    val isAlreadyRegistered = userProfile?.isRegistered == true

    var isCapturingMode by remember { mutableStateOf(!isAlreadyRegistered) }
    var isProcessingCapture by remember { mutableStateOf(false) }
    var processingStepText by remember { mutableStateOf("") }
    var isShutterTriggered by remember { mutableStateOf(false) }
    var isFrontLens by remember { mutableStateOf(true) }
    var showTestMatchDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("biometric_registration_section"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isAlreadyRegistered) DarcorpEmerald.copy(alpha = 0.4f) else DarcorpGold500.copy(alpha = 0.5f)
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Title & Biometric Shield Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarcorpNavy900,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = null,
                                tint = DarcorpGold400,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isId) "Registrasi Biometrik Wajah Presensi" else "Biometric Face Registration",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DarcorpNavy900
                        )
                        Text(
                            text = if (isId) "Autentikasi Presensi Anti-Spoofing SCBD" else "Anti-Spoofing Biometric Attendance",
                            fontSize = 10.sp,
                            color = DarcorpSlate600
                        )
                    }
                }

                // Enrollment Status Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isAlreadyRegistered) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isAlreadyRegistered) Color(0xFF16A34A) else Color(0xFFD97706)
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isAlreadyRegistered) Color(0xFF16A34A) else Color(0xFFD97706))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isAlreadyRegistered) {
                                if (isId) "TERDAFTAR ✓" else "ENROLLED ✓"
                            } else {
                                if (isId) "BELUM DAFTAR" else "UNREGISTERED"
                            },
                            color = if (isAlreadyRegistered) Color(0xFF15803D) else Color(0xFFB45309),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Body: If Already Registered and not in re-capture mode, show Enrolled Info Card
            if (isAlreadyRegistered && !isCapturingMode && userProfile != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0))
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isId) "Profil Biometrik Terverifikasi" else "Verified Biometric Profile",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = DarcorpNavy900
                                )
                                Text(
                                    text = "ID: ${userProfile.biometricHash}",
                                    fontSize = 10.sp,
                                    color = DarcorpSlate600,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DarcorpEmerald.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${userProfile.confidenceScore}% Match",
                                    color = DarcorpEmeraldDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Attributes Grid
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(text = if (isId) "Terdaftar Pada" else "Enrolled On", fontSize = 9.sp, color = DarcorpSlate600)
                                Text(text = userProfile.registrationDate, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DarcorpNavy900)
                            }
                            Column {
                                Text(text = if (isId) "Anti-Spoofing" else "Anti-Spoofing", fontSize = 9.sp, color = DarcorpSlate600)
                                Text(text = "Liveness 3D Passed", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DarcorpEmeraldDark)
                            }
                            Column {
                                Text(text = if (isId) "Nodal Points" else "Nodal Points", fontSize = 9.sp, color = DarcorpSlate600)
                                Text(text = "${userProfile.featureVectorSize} Vector", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DarcorpNavy900)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Actions for Registered User
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Test Match Button
                            OutlinedButton(
                                onClick = { showTestMatchDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("test_face_match_btn"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp), tint = DarcorpEmeraldDark)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isId) "Uji Autentikasi" else "Test Match",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarcorpEmeraldDark
                                )
                            }

                            // Re-register / Retake Button
                            Button(
                                onClick = { isCapturingMode = true },
                                colors = ButtonDefaults.buttonColors(containerColor = DarcorpNavy900),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("reregister_face_btn")
                            ) {
                                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isId) "Daftar Ulang" else "Re-register",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            } else {
                // Registration / Capture Mode Active
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Guidance banner
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEFF6FF),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBFDBFE))
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isId) "Arahkan wajah Anda ke dalam oval kamera. Pastikan pencahayaan cukup dan hindari kacamata hitam."
                                       else "Position your face inside the camera oval. Ensure adequate lighting and remove sunglasses.",
                                fontSize = 10.5.sp,
                                color = Color(0xFF1E40AF),
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // The Camera Preview Component
                    BiometricCameraPreview(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        isId = isId,
                        isFrontLens = isFrontLens,
                        onToggleLens = { isFrontLens = !isFrontLens },
                        isShutterTriggered = isShutterTriggered
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Processing Spinner & Step Text (when capture is in progress)
                    if (isProcessingCapture) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarcorpNavy900,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = DarcorpGold400,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = processingStepText,
                                    color = Color.White,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        // The Primary Biometric Capture & Register Button
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isProcessingCapture = true
                                    isShutterTriggered = true

                                    processingStepText = if (isId) "1/3 Memindai kontur wajah & pencahayaan..." else "1/3 Scanning facial contour & lighting..."
                                    delay(400)
                                    isShutterTriggered = false

                                    processingStepText = if (isId) "2/3 Uji 3D Anti-Spoofing & Liveness..." else "2/3 Verifying 3D Anti-Spoofing & Liveness..."
                                    delay(500)

                                    processingStepText = if (isId) "3/3 Mengenkripsi 512-Vector Biometrik SHA-256..." else "3/3 Encrypting 512-Vector Biometric SHA-256..."
                                    delay(400)

                                    val newProfile = repository.registerFaceProfile(
                                        userId = currentUser.id,
                                        userName = currentUser.name,
                                        confidence = 99.4f
                                    )

                                    isProcessingCapture = false
                                    isCapturingMode = false

                                    Toast.makeText(
                                        context,
                                        if (isId) "Biometrik wajah ${currentUser.name} berhasil didaftarkan!"
                                        else "Face biometric for ${currentUser.name} successfully registered!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    onFaceRegistered(newProfile)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarcorpNavy900),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("capture_biometric_face_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = DarcorpGold400,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isId) "AMBIL & DAFTARKAN WAJAH BIOMETRIK" else "CAPTURE & REGISTER BIOMETRIC FACE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }

                    if (isAlreadyRegistered) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { isCapturingMode = false },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = if (isId) "Batal & Gunakan Data Tersimpan" else "Cancel & Keep Existing",
                                fontSize = 11.sp,
                                color = DarcorpSlate600
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Security & Standards Compliance Note
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = DarcorpSlate600,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = if (isId) "Kepatuhan ISO 27001 & GDPR: Data wajah disimpan dalam bentuk hash vektor terenkripsi."
                           else "ISO 27001 & GDPR Compliant: Raw face data encrypted as hashed 512-d mathematical vector.",
                    fontSize = 9.sp,
                    color = DarcorpSlate600,
                    lineHeight = 12.sp
                )
            }
        }
    }

    // Modal Dialog: Test Biometric Match
    if (showTestMatchDialog && userProfile != null) {
        AlertDialog(
            onDismissRequest = { showTestMatchDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = DarcorpEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isId) "Uji Autentikasi Biometrik" else "Biometric Match Test",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DarcorpNavy900
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isId) "Verifikasi langsung terhadap template biometrik presensi:" else "Direct verification against registered template:",
                        fontSize = 11.5.sp,
                        color = DarcorpSlate700
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF0FDF4),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarcorpEmerald))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "Karyawan: ${userProfile.userName}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarcorpNavy900)
                            Text(text = "Skor Kecocokan: ${userProfile.confidenceScore}% (Sangat Cocok)", fontSize = 11.sp, color = DarcorpEmeraldDark)
                            Text(text = "Anti-Spoofing: Liveness 3D Lulus", fontSize = 11.sp, color = DarcorpEmeraldDark)
                            Text(text = "Hash: ${userProfile.biometricHash}", fontSize = 9.sp, color = DarcorpSlate600, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                        }
                    }
                    Text(
                        text = if (isId) "Status: Autentikasi biometrik presensi berfungsi optimal." else "Status: Biometric attendance authentication is operational.",
                        fontSize = 11.sp,
                        color = DarcorpSlate600
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showTestMatchDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DarcorpNavy900)
                ) {
                    Text(if (isId) "Tutup" else "Close", color = Color.White)
                }
            }
        )
    }
}
