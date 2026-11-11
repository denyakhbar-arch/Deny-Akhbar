package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppLanguage
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DownloadModal(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onLaunchInstalledApp: () -> Unit
) {
    val isId = language == AppLanguage.INDONESIAN
    val coroutineScope = rememberCoroutineScope()
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var isDownloadComplete by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("download_modal_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarcorpNavy900),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download",
                                tint = DarcorpGold400
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isId) "Unduh Aplikasi DARCORP" else "Download DARCORP Mobile",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = DarcorpNavy900
                            )
                            Text(
                                text = "Android (.APK) & iOS (.IPA)",
                                fontSize = 12.sp,
                                color = DarcorpSlate600
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = DarcorpSlate600)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // App Package Card
                Surface(
                    color = DarcorpSlate50,
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "darcorp-official-release.apk",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = DarcorpNavy900
                            )
                            Surface(
                                color = DarcorpEmerald.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "v2.4.0 LTS",
                                    color = DarcorpEmeraldDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isId) "Ukuran: 24.8 MB • Android 8.0+ • Keamanan Terverifikasi SHA-256"
                                   else "Size: 24.8 MB • Android 8.0+ • SHA-256 Verified Secure",
                            fontSize = 11.sp,
                            color = DarcorpSlate600
                        )
                        Text(
                            text = "SHA: 47aa95548a84cd159b2860a9d8374695f9a382b4",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Download Progress or Buttons
                if (isDownloading) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = { downloadProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = DarcorpGold500,
                            trackColor = DarcorpSlate100
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isId) "Mengunduh paket instalasi... ${(downloadProgress * 100).toInt()}%"
                                   else "Downloading installation package... ${(downloadProgress * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = DarcorpNavy900,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else if (isDownloadComplete) {
                    Surface(
                        color = DarcorpEmerald.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarcorpEmerald))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Complete",
                                tint = DarcorpEmerald,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isId) "Paket APK Berhasil Diunduh!" else "APK Package Downloaded Successfully!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = DarcorpEmeraldDark
                            )
                            Text(
                                text = if (isId) "Tersimpan di: Download/darcorp-v2.4.0.apk" else "Saved at: Download/darcorp-v2.4.0.apk",
                                fontSize = 11.sp,
                                color = DarcorpSlate600
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    onDismiss()
                                    onLaunchInstalledApp()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarcorpEmeraldDark),
                                modifier = Modifier.fillMaxWidth().testTag("launch_installed_app_btn")
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isId) "Pasang & Buka Aplikasi DARCORP" else "Install & Open DARCORP App",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    // Primary Download Button (Android)
                    Button(
                        onClick = {
                            isDownloading = true
                            downloadProgress = 0f
                            coroutineScope.launch {
                                for (i in 1..10) {
                                    delay(180)
                                    downloadProgress = i / 10f
                                }
                                isDownloading = false
                                isDownloadComplete = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarcorpNavy900),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("start_download_apk_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Android, contentDescription = "Android", tint = DarcorpGold400)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isId) "Unduh Paket Android (.APK) Sekarang" else "Download Android (.APK) Package Now",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Secondary Download Button (iOS) (Tombol Cokelat)
                    Button(
                        onClick = {
                            isDownloading = true
                            downloadProgress = 0f
                            coroutineScope.launch {
                                for (i in 1..10) {
                                    delay(150)
                                    downloadProgress = i / 10f
                                }
                                isDownloading = false
                                isDownloadComplete = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarcorpBrown700),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PhoneIphone, contentDescription = "iOS", tint = DarcorpBrownCream)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isId) "Unduh Paket iOS (TestFlight / .IPA)" else "Download iOS (TestFlight / .IPA)",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Direct Instant Run
                    TextButton(
                        onClick = {
                            onDismiss()
                            onLaunchInstalledApp()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("direct_run_portal_btn")
                    ) {
                        Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = null, tint = DarcorpNavy700, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isId) "Atau Jalankan Langsung Versi Terpasang" else "Or Launch Installed Version Directly",
                            color = DarcorpNavy700,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Installation Guide Steps
                Text(
                    text = if (isId) "Cara Memasang di HP Android:" else "How to Install on Android:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DarcorpNavy900
                )
                Spacer(modifier = Modifier.height(8.dp))

                val steps = if (isId) listOf(
                    "1. Klik tombol 'Unduh Paket Android (.APK)'.",
                    "2. Setelah selesai, buka berkas dari bar notifikasi ponsel.",
                    "3. Jika muncul peringatan, pilih 'Setelan' lalu aktifkan 'Izinkan dari sumber ini'.",
                    "4. Tekan 'Instal', kemudian buka aplikasi DARCORP yang sudah terpasang."
                ) else listOf(
                    "1. Tap 'Download Android (.APK) Package'.",
                    "2. Once done, open the file from your phone's notification bar.",
                    "3. If prompted, select 'Settings' and enable 'Allow from this source'.",
                    "4. Tap 'Install', then open the installed DARCORP app on your phone."
                )

                steps.forEach { step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = DarcorpEmerald,
                            modifier = Modifier.size(14.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = step,
                            fontSize = 11.sp,
                            color = DarcorpSlate700,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
