package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.ui.theme.*

@Composable
fun DevicePermissionsDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onAllPermissionsGranted: () -> Unit = {}
) {
    if (!isOpen) return

    val context = LocalContext.current

    // Helper function to check if permission is granted
    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    var locationGranted by remember { mutableStateOf(isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) }
    var cameraGranted by remember { mutableStateOf(isPermissionGranted(Manifest.permission.CAMERA)) }
    var micGranted by remember { mutableStateOf(isPermissionGranted(Manifest.permission.RECORD_AUDIO)) }
    var storageGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                isPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        )
    }
    var installAllowed by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.packageManager.canRequestPackageInstalls()
            } else {
                true
            }
        )
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        locationGranted = results[Manifest.permission.ACCESS_FINE_LOCATION] ?: locationGranted
        cameraGranted = results[Manifest.permission.CAMERA] ?: cameraGranted
        micGranted = results[Manifest.permission.RECORD_AUDIO] ?: micGranted

        val storageResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            results[Manifest.permission.READ_MEDIA_IMAGES]
        } else {
            results[Manifest.permission.READ_EXTERNAL_STORAGE]
        }
        storageGranted = storageResult ?: storageGranted

        if (locationGranted && cameraGranted && micGranted && storageGranted) {
            android.widget.Toast.makeText(
                context,
                "Semua izin akses perangkat berhasil diaktifkan!",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            onAllPermissionsGranted()
        } else {
            android.widget.Toast.makeText(
                context,
                "Status izin perangkat telah diperbarui.",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, DarcorpLightBlue200, RoundedCornerShape(20.dp))
                .testTag("device_permissions_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarcorpBrown800,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = DarcorpBrownCream,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Izin Akses & Otorisasi Perangkat",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = DarcorpBrown900
                            )
                            Text(
                                text = "Kepatuhan Hukum & Keamanan Multi-Perusahaan",
                                fontSize = 10.sp,
                                color = DarcorpSlate600
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_permissions_dialog_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = DarcorpSlate700)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DarcorpLightBlue200)

                // Scrollable List of Requested Permissions
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Aplikasi DARcorporate membutuhkan otorisasi akses berikut pada perangkat Anda agar seluruh modul (Presensi GPS, Kamera Wajah, Bukti Kasir, Obrolan Enkripsi & Vault) dapat berjalan optimal:",
                        fontSize = 11.sp,
                        color = DarcorpSlate700,
                        lineHeight = 16.sp
                    )

                    // 1. Lokasi GPS
                    PermissionItemCard(
                        title = "1. Akses Lokasi Perangkat (GPS)",
                        description = "Digunakan untuk mengunci koordinat geofencing valid saat presensi karyawan di SCBD Tower 18 atau Kantor Tangerang.",
                        icon = Icons.Default.LocationOn,
                        iconBg = Color(0xFFE0F2FE),
                        iconColor = Color(0xFF0284C7),
                        isGranted = locationGranted
                    )

                    // 2. Kamera
                    PermissionItemCard(
                        title = "2. Akses Kamera Perangkat",
                        description = "Digunakan untuk verifikasi biometrik 3D Liveness wajah presensi dan pengambilan foto struk bukti pembayaran secara langsung.",
                        icon = Icons.Default.CameraAlt,
                        iconBg = Color(0xFFDCFCE7),
                        iconColor = Color(0xFF16A34A),
                        isGranted = cameraGranted
                    )

                    // 3. Album Galeri & Dokumen Penyimpanan
                    PermissionItemCard(
                        title = "3. Akses Album Galeri & Dokumen",
                        description = "Digunakan untuk mengunggah file bukti transfer dari galeri dan membuka dokumen perjanjian kemitraan MoA (Form F1 & F2).",
                        icon = Icons.Default.PhotoLibrary,
                        iconBg = Color(0xFFFEF3C7),
                        iconColor = Color(0xFFD97706),
                        isGranted = storageGranted
                    )

                    // 4. Mikrofon
                    PermissionItemCard(
                        title = "4. Akses Mikrofon Perangkat",
                        description = "Digunakan untuk konsultasi hukum audio privat bersama Advokat dan rekaman voice memo laporan kerja harian.",
                        icon = Icons.Default.Mic,
                        iconBg = Color(0xFFF3E8FF),
                        iconColor = Color(0xFF9333EA),
                        isGranted = micGranted
                    )

                    // 5. Waktu & Alarm Sistem
                    PermissionItemCard(
                        title = "5. Izin Sinkronisasi Waktu & Alarm",
                        description = "Memastikan waktu absensi menggunakan patokan waktu resmi server WIB dan mengontrol hitungan mundur retensi 30 hari dokumen vault.",
                        icon = Icons.Default.Schedule,
                        iconBg = Color(0xFFFFEDD5),
                        iconColor = Color(0xFFEA580C),
                        isGranted = true // System schedule/time check
                    )

                    // 6. Perintah Pasang / Install Aplikasi
                    PermissionItemCard(
                        title = "6. Otorisasi Perintah Pasang Aplikasi (Google)",
                        description = "Memungkinkan sistem memberikan perintah memasang paket aplikasi resmi langsung ke perangkat Anda.",
                        icon = Icons.Default.InstallMobile,
                        iconBg = Color(0xFFCCFBF1),
                        iconColor = Color(0xFF0D9488),
                        isGranted = installAllowed
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Tombol 1: Izinkan Semua Akses
                    Button(
                        onClick = {
                            val permsToRequest = mutableListOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO
                            )
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
                            } else {
                                permsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                            permissionLauncher.launch(permsToRequest.toTypedArray())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarcorpBrown700),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("grant_all_permissions_btn")
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = DarcorpBrownCream, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "IZINKAN SEMUA AKSES PERANGKAT",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }

                    // Tombol 2: Perintah Pasang Aplikasi pada Perangkat
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if (!context.packageManager.canRequestPackageInstalls()) {
                                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                                        data = Uri.parse("package:${context.packageName}")
                                    }
                                    context.startActivity(intent)
                                    android.widget.Toast.makeText(
                                        context,
                                        "Buka pengaturan untuk mengizinkan pemasangan aplikasi",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    // Trigger installation intent or open download portal
                                    val installIntent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("https://sites.google.com/view/darcorp-official/darcorporate-release.apk")
                                    }
                                    context.startActivity(installIntent)
                                    android.widget.Toast.makeText(
                                        context,
                                        "Memulai perintah pasang aplikasi pada perangkat...",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val installIntent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://sites.google.com/view/darcorp-official/darcorporate-release.apk")
                                }
                                context.startActivity(installIntent)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("trigger_install_command_btn")
                    ) {
                        Icon(imageVector = Icons.Default.DownloadForOffline, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PERINTAH PASANG APLIKASI DI PERANGKAT",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionItemCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    isGranted: Boolean
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isGranted) Color(0xFFBBF7D0) else Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = iconBg,
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        color = DarcorpNavy900
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isGranted) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = if (isGranted) "DIIZINKAN ✓" else "PERLU IZIN",
                            color = if (isGranted) Color(0xFF16A34A) else Color(0xFF64748B),
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = description,
                    fontSize = 10.sp,
                    color = DarcorpSlate600,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
