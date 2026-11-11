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
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
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
                                text = if (isId) "Unduh Aplikasi DARcorporate" else "Download DARcorporate Mobile",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
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
                                    text = if (isId) "Pasang & Buka Aplikasi DARcorporate" else "Install & Open DARcorporate App",
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

                    Spacer(modifier = Modifier.height(14.dp))

                    // Direct Download Links Card with Copy Buttons
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isId) "Tautan Unduhan Langsung (Direct Download Link):"
                                       else "Direct Download Links:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = DarcorpNavy900
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // APK Link Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Android (.APK) Direct Link:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarcorpNavy700)
                                    Text(
                                        text = "https://sites.google.com/view/darcorp-official/darcorporate-release.apk",
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = DarcorpSlate600,
                                        maxLines = 1
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("DARcorporate APK Link", "https://sites.google.com/view/darcorp-official/darcorporate-release.apk")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, if (isId) "Tautan APK disalin ke clipboard!" else "APK Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy APK Link", tint = DarcorpNavy900, modifier = Modifier.size(16.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            HorizontalDivider(color = DarcorpSlate100)
                            Spacer(modifier = Modifier.height(6.dp))

                            // iOS Link Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Apple iOS (.IPA / Web Clip) Direct Link:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarcorpBrown700)
                                    Text(
                                        text = "https://sites.google.com/view/darcorp-official/darcorporate-ios.ipa",
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = DarcorpSlate600,
                                        maxLines = 1
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("DARcorporate iOS Link", "https://sites.google.com/view/darcorp-official/darcorporate-ios.ipa")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, if (isId) "Tautan iOS disalin ke clipboard!" else "iOS Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy iOS Link", tint = DarcorpBrown700, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Social Media Sharing Section
                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarcorpEmerald))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = DarcorpEmeraldDark, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isId) "Bagikan File & Link ke Media Sosial:" else "Share Download Link to Social Media:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = DarcorpEmeraldDark
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            // Share to All Social Media (Native Android Chooser)
                            Button(
                                onClick = {
                                    val shareText = "Unduh Aplikasi Resmi DARcorporate (Layanan Hukum, Presensi Wajah & QR, Payroll, dan Portal Kemitraan):\n\n" +
                                            "• Download APK Android: https://sites.google.com/view/darcorp-official/darcorporate-release.apk\n" +
                                            "• Download iOS (.IPA / Web Clip): https://sites.google.com/view/darcorp-official/darcorporate-ios.ipa\n" +
                                            "• Portal Resmi: https://sites.google.com/view/darcorp-official"
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, if (isId) "Bagikan Tautan Unduhan DARcorporate" else "Share DARcorporate Download Link")
                                    context.startActivity(shareIntent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarcorpEmeraldDark),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isId) "Bagikan ke Semua Media Sosial (WhatsApp, Telegram, dll)"
                                           else "Share to All Social Media (WhatsApp, Telegram, etc)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick Action Chips: WhatsApp, Telegram, Copy All Links
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val text = "Halo! Silakan unduh aplikasi DARcorporate resmi di sini: https://sites.google.com/view/darcorp-official/darcorporate-release.apk"
                                        val url = "https://api.whatsapp.com/send?text=" + Uri.encode(text)
                                        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(i)
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f).height(34.dp)
                                ) {
                                    Text("WhatsApp", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                                }

                                OutlinedButton(
                                    onClick = {
                                        val text = "Unduh aplikasi DARcorporate resmi: https://sites.google.com/view/darcorp-official/darcorporate-release.apk"
                                        val url = "https://t.me/share/url?url=" + Uri.encode("https://sites.google.com/view/darcorp-official") + "&text=" + Uri.encode(text)
                                        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(i)
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f).height(34.dp)
                                ) {
                                    Text("Telegram", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF))
                                }

                                OutlinedButton(
                                    onClick = {
                                        val shareText = "Unduh Aplikasi Resmi DARcorporate:\nAndroid APK: https://sites.google.com/view/darcorp-official/darcorporate-release.apk\niOS IPA: https://sites.google.com/view/darcorp-official/darcorporate-ios.ipa"
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("DARcorporate Links", shareText)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, if (isId) "Semua tautan unduhan disalin!" else "All download links copied!", Toast.LENGTH_SHORT).show()
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f).height(34.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Salin Link", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

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

                // Installation Guide Steps (Android & iOS)
                Text(
                    text = if (isId) "Petunjuk Pemasangan Android & iOS:" else "Android & iOS Installation Guide:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DarcorpNavy900
                )
                Spacer(modifier = Modifier.height(8.dp))

                val androidSteps = if (isId) listOf(
                    "Android: Klik 'Unduh Paket Android (.APK)'. Buka berkas, aktifkan 'Izinkan dari sumber ini', lalu tekan Instal.",
                    "iOS (iPhone/iPad): Unduh berkas .IPA / Profil Konfigurasi atau buka portal resmi di Safari, tekan tombol Share lalu pilih 'Add to Home Screen' (Tambah ke Layar Utama)."
                ) else listOf(
                    "Android: Tap 'Download Android (.APK) Package', allow unknown sources in Settings, and tap Install.",
                    "iOS (iPhone/iPad): Download .IPA or open in Safari, tap Share, and select 'Add to Home Screen'."
                )

                androidSteps.forEach { step ->
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
