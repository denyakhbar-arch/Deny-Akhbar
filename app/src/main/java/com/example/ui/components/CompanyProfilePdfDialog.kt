package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.example.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class PdfPageTopic(
    val pageIndex: Int,
    val shortTitle: String,
    val fullTitle: String,
    val category: String
)

val COMPANY_PROFILE_TOPICS = listOf(
    PdfPageTopic(0, "Cover", "Cover Proposal Kerjasama Hukum D.A.R", "Pengantar"),
    PdfPageTopic(1, "Risiko", "Risiko Bisnis & Pentingnya Aspek Hukum", "Latar Belakang"),
    PdfPageTopic(2, "Solusi", "DAR Law Office: Solusi Hukum Terintegrasi", "Solusi"),
    PdfPageTopic(3, "Kontak", "Profil Kantor (Tangerang & Jakarta Barat)", "Kontak & Alamat"),
    PdfPageTopic(4, "Paket", "Bidang-Bidang Hukum D.A.R", "Layanan"),
    PdfPageTopic(5, "8 Bidang", "8 Layanan Bidang Hukum Komprehensif", "Layanan Utama"),
    PdfPageTopic(6, "Lingkup", "Ruang Lingkup Litigasi & Non-Litigasi", "Metode"),
    PdfPageTopic(7, "Kelebihan", "Kelebihan Advokat Muda & Profesional", "Keunggulan"),
    PdfPageTopic(8, "Mitra 1", "PT. Satria Saftindo Jaya (LTC Glodok)", "Portofolio"),
    PdfPageTopic(9, "Mitra 2", "CV. Sejahtera Mandiri Metal (Percetakan)", "Portofolio"),
    PdfPageTopic(10, "Mitra 3A", "PT. Infiniti Sumber Alam (Logistik Kelapa)", "Portofolio"),
    PdfPageTopic(11, "Mitra 3B", "PT. Infiniti Sumber Alam (Sengketa Beres)", "Portofolio"),
    PdfPageTopic(12, "Mitra 4", "Yayasan Purgatorio Tangerang (Pendidikan)", "Portofolio")
)

/**
 * Interactive Modal Dialog to view the uploaded Company Profile PDF:
 * /public/assets/aistudio/COMPANY_PROFIL_DAR_2026_15e291c9.pdf
 * (bundled locally in app assets as company_profile_dar.pdf).
 */
@Composable
fun CompanyProfilePdfDialog(
    isId: Boolean = true,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var activeTab by remember { mutableStateOf(0) } // 0: PDF Viewer, 1: Ringkasan Eksekutif
    var currentPageIndex by remember { mutableStateOf(0) }
    var totalPages by remember { mutableStateOf(13) }
    var pageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoadingPage by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var pdfRenderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var parcelFd by remember { mutableStateOf<ParcelFileDescriptor?>(null) }
    var cachedPdfFile by remember { mutableStateOf<File?>(null) }

    // Initialize PdfRenderer from app assets
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val cacheFile = File(context.cacheDir, "company_profile_dar.pdf")
                if (!cacheFile.exists() || cacheFile.length() < 1000L) {
                    context.assets.open("company_profile_dar.pdf").use { input ->
                        FileOutputStream(cacheFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                }
                cachedPdfFile = cacheFile
                val pfd = ParcelFileDescriptor.open(cacheFile, ParcelFileDescriptor.MODE_READ_ONLY)
                parcelFd = pfd
                val renderer = PdfRenderer(pfd)
                pdfRenderer = renderer
                totalPages = renderer.pageCount

                // Render first page
                val initialBitmap = renderSinglePage(renderer, 0)
                withContext(Dispatchers.Main) {
                    pageBitmap = initialBitmap
                    isLoadingPage = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    errorMessage = e.localizedMessage ?: "Gagal memuat dokumen PDF"
                    isLoadingPage = false
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                pdfRenderer?.close()
                parcelFd?.close()
            } catch (e: Exception) {
                // safely ignored
            }
        }
    }

    // Function to change page
    fun goToPage(targetIndex: Int) {
        if (targetIndex !in 0 until totalPages) return
        currentPageIndex = targetIndex
        isLoadingPage = true
        scope.launch(Dispatchers.IO) {
            val renderer = pdfRenderer
            if (renderer != null) {
                val bmp = renderSinglePage(renderer, targetIndex)
                withContext(Dispatchers.Main) {
                    pageBitmap = bmp
                    isLoadingPage = false
                }
            } else {
                withContext(Dispatchers.Main) {
                    isLoadingPage = false
                }
            }
        }
    }

    // Function to open PDF externally or share
    fun openPdfExternally() {
        val file = cachedPdfFile
        if (file == null || !file.exists()) {
            Toast.makeText(context, "File PDF sedang disiapkan...", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Buka Company Profile D.A.R dengan:"))
        } catch (e: Exception) {
            Toast.makeText(context, "Membuka file PDF: ${file.name}", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, DarcorpGold400, RoundedCornerShape(16.dp)),
            color = Color(0xFFFBF8F3),
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // Header Bar
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
                            color = DarcorpBrown900,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = DarcorpGold400,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "COMPANY PROFILE D.A.R 2026",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = DarcorpNavy900
                            )
                            Text(
                                text = "D.A.R DENY AKHBAR S.H & REKAN • DARcorporate",
                                fontSize = 10.sp,
                                color = DarcorpBrown700,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Tombol Buka / Unduh PDF
                        IconButton(
                            onClick = { openPdfExternally() },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("open_pdf_external_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Buka PDF Eksternal",
                                tint = DarcorpBrown700
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("close_company_profile_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = DarcorpSlate600
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mode Tabs (Baca PDF Asli vs Ringkasan Teks Eksekutif)
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = Color(0xFFF1ECE4),
                    contentColor = DarcorpNavy900,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isId) "Baca Dokumen PDF Asli" else "Read Original PDF",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            }
                        }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isId) "Ringkasan & Kontak Kantor" else "Summary & Office Contacts",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Content View according to activeTab
                if (activeTab == 0) {
                    // TAB 0: INTERACTIVE PDF PAGE READER
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        // Quick Jump Topics Horizontal Strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            COMPANY_PROFILE_TOPICS.forEach { topic ->
                                val isSelected = topic.pageIndex == currentPageIndex
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) DarcorpBrown800 else Color.White,
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(
                                            if (isSelected) DarcorpGold400 else Color(0xFFE2E8F0)
                                        )
                                    ),
                                    modifier = Modifier
                                        .clickable { goToPage(topic.pageIndex) }
                                        .testTag("jump_page_${topic.pageIndex + 1}")
                                ) {
                                    Text(
                                        text = "${topic.pageIndex + 1}. ${topic.shortTitle}",
                                        color = if (isSelected) Color.White else DarcorpNavy900,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Page Title Banner
                        val currentTopic = COMPANY_PROFILE_TOPICS.getOrNull(currentPageIndex)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFFBEB),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarcorpGold400)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentTopic?.fullTitle ?: "Halaman ${currentPageIndex + 1}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = DarcorpBrown900
                                )
                                Text(
                                    text = "${currentPageIndex + 1} / $totalPages",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarcorpBrown700
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // PDF Page Display Viewport
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFCBD5E1))),
                            shadowElevation = 3.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLoadingPage) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(32.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            color = DarcorpBrown700,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Merender Halaman ${currentPageIndex + 1} dari file PDF...",
                                            fontSize = 11.sp,
                                            color = DarcorpSlate600
                                        )
                                    }
                                } else if (pageBitmap != null) {
                                    Image(
                                        bitmap = pageBitmap!!.asImageBitmap(),
                                        contentDescription = "Halaman ${currentPageIndex + 1} PDF",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(6.dp),
                                        contentScale = ContentScale.FillWidth
                                    )
                                } else {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(24.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = Color.Red)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = errorMessage ?: "Gagal memuat halaman PDF",
                                            fontSize = 11.sp,
                                            color = Color.Red
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = { openPdfExternally() },
                                            colors = ButtonDefaults.buttonColors(containerColor = DarcorpBrown700)
                                        ) {
                                            Text("Buka dengan Pembaca PDF Eksternal", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Bottom Navigation Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { goToPage(currentPageIndex - 1) },
                                enabled = currentPageIndex > 0 && !isLoadingPage,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarcorpNavy900),
                                modifier = Modifier.testTag("pdf_prev_page_btn")
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isId) "Sebelumnya" else "Previous", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DarcorpBrown900
                            ) {
                                Text(
                                    text = "HALAMAN ${currentPageIndex + 1} DARI $totalPages",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.5.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }

                            Button(
                                onClick = { goToPage(currentPageIndex + 1) },
                                enabled = currentPageIndex < totalPages - 1 && !isLoadingPage,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DarcorpBrown700),
                                modifier = Modifier.testTag("pdf_next_page_btn")
                            ) {
                                Text(if (isId) "Berikutnya" else "Next", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                } else {
                    // TAB 1: EXECUTIVE SUMMARY & OFFICE CONTACT DETAILS
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Card: Kontak Resmi Kantor
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarcorpGold400))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "D.A.R. LAW OFFICE (DENY AKHBAR S.H & REKAN)",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = DarcorpNavy900
                                )
                                Text(
                                    text = "Satu-satunya Layanan Hukum Berbasis Aplikasi: Praktis, Taktis, Strategis",
                                    fontSize = 10.5.sp,
                                    color = DarcorpBrown700,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                                Spacer(modifier = Modifier.height(8.dp))

                                // Alamat Kantor Tangerang
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = DarcorpBrown700, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(text = "Kantor Tangerang:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DarcorpNavy900)
                                        Text(text = "Barata Residences, Jl. Sepatan, Blok C Nomor 6, Kabupaten Tangerang (Pusat dukungan hukum strategis untuk konsultasi regional)", fontSize = 10.sp, color = DarcorpSlate700)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Alamat Kantor Jakarta Barat
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(imageVector = Icons.Default.LocationCity, contentDescription = null, tint = DarcorpBrown700, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(text = "Kantor Jakarta Barat:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DarcorpNavy900)
                                        Text(text = "Jl. Duri Kosambi, RT 003/RW 005, Nomor 52, Kel. Duri Kosambi, Kec. Cengkareng, Jakarta Barat (Pusat utama advokasi perkotaan & korporasi)", fontSize = 10.sp, color = DarcorpSlate700)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Kontak Komunikasi
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(text = "Telepon:", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = DarcorpSlate600)
                                        Text(text = "0877-6548-3134", fontWeight = FontWeight.Black, fontSize = 11.5.sp, color = DarcorpNavy900)
                                    }
                                    Column {
                                        Text(text = "Email Resmi:", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = DarcorpSlate600)
                                        Text(text = "darlawoffices@gmail.com", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DarcorpBrown700)
                                    }
                                    Column {
                                        Text(text = "Website / Portal:", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = DarcorpSlate600)
                                        Text(text = "www.denyalfatih.top", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DarcorpEmeraldDark)
                                    }
                                }
                            }
                        }

                        // Card: 8 Layanan Bidang Hukum
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFCBD5E1)))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "8 Bidang Hukum yang Ditangani D.A.R:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = DarcorpNavy900
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                val services = listOf(
                                    "1. Hukum Korporat & Bisnis" to "Kontrak kerja sama, sewa-menyewa, jual beli, restrukturisasi hutang piutang.",
                                    "2. Hukum Pidana" to "Penanganan perkara pidana penipuan, penggelapan aset, pemalsuan, korupsi & perbankan.",
                                    "3. Hukum Perdata" to "Wanprestasi / cidera janji, ganti rugi, jual beli, hibah, waris & sengketa perikatan.",
                                    "4. Hukum Ketenagakerjaan" to "Pendampingan administratif PHK, bipartite/tripartite, dan audit regulasi ketenagakerjaan.",
                                    "5. Hukum Pertanahan & Properti" to "Sengketa lahan, legal due diligence tanah, pencegahan konflik, konsultasi real estat.",
                                    "6. Hukum Perpajakan" to "Pendampingan pemeriksaan pajak, keberatan, banding di Pengadilan Pajak RI.",
                                    "7. Hukum Investasi" to "Penanaman Modal Asing/Dalam Negeri (PMA/PMDN), perizinan OSS RBA & perjanjian investasi.",
                                    "8. HKI, Paten & Perizinan" to "Pendaftaran Merek, Hak Cipta, Paten, Rahasia Dagang, Hukum Lingkungan & Kepailitan."
                                )
                                services.forEach { (title, desc) ->
                                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = DarcorpBrown700)
                                    Text(text = desc, fontSize = 9.5.sp, color = DarcorpSlate700, modifier = Modifier.padding(bottom = 4.dp))
                                }
                            }
                        }

                        // Card: Portofolio Klien Nyata
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF0FDF4),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarcorpEmerald))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Portofolio Klien & Kemitraan Tetap D.A.R:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = DarcorpNavy900
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "• PT. SATRIA SAFTINDO JAYA (PT. SSJ): Perlindungan merek dagang dan hak ekonomi 30 unit toko alat safety di LTC Glodok dari pembajakan kompetitor.\n" +
                                           "• CV. SEJAHTERA MANDIRI METAL (CV. SMM): Kemenangan 2 perkara wanprestasi / cidera janji di Pengadilan Negeri Jakarta Barat tahun 2023.\n" +
                                           "• PT. INFINITI SUMBER ALAM (PT. ISA): Pergudangan Mutiara Kosambi 1, penyelesaian sengketa supplier & buyer (Perkara Surabaya & Tasikmalaya).\n" +
                                           "• YAYASAN PURGATORIO TANGERANG: Sekolah Islam setara SLTP & SLTA, dipimpin Prof. Kiyai Qurtubi, pendampingan legalitas dan kemenangan gugatan perdata.",
                                    fontSize = 10.sp,
                                    color = DarcorpSlate700,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Footer Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { openPdfExternally() }) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = DarcorpBrown700, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Unduh / Buka Dokumen Asli", color = DarcorpBrown700, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = DarcorpNavy900),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (isId) "Tutup" else "Close", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

/**
 * Render single page of PDF into high-res Bitmap.
 */
private fun renderSinglePage(renderer: PdfRenderer, pageIndex: Int): Bitmap {
    val page = renderer.openPage(pageIndex)
    // 2.2x scaling for high-definition text readability on phone screens
    val targetWidth = (page.width * 2.2f).toInt()
    val targetHeight = (page.height * 2.2f).toInt()
    val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
    bitmap.eraseColor(android.graphics.Color.WHITE)
    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
    page.close()
    return bitmap
}
