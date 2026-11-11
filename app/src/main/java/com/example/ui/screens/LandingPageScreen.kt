package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DarcorpRepository
import com.example.data.PortalTab
import com.example.model.*
import com.example.ui.components.DownloadModal
import com.example.ui.components.GoogleSitesHeader
import com.example.ui.theme.*
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.example.auth.AuthViewModel
import com.example.ui.components.GoogleOnboardingDialog
import com.example.ui.components.CompanyProfilePdfDialog
import com.example.ui.components.DevicePermissionsDialog

@Composable
fun LandingPageScreen(
    repository: DarcorpRepository,
    authViewModel: AuthViewModel,
    onLaunchPortal: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by repository.language.collectAsState()
    val isId = language == AppLanguage.INDONESIAN
    val showDownloadModal by repository.downloadModalVisible.collectAsState()
    val currentUser by repository.currentUser.collectAsState()
    var showGoogleOnboarding by remember { mutableStateOf(false) }
    var showCompanyProfileDialog by remember { mutableStateOf(false) }
    var showLoginChoiceDialog by remember { mutableStateOf(false) }
    var showPermissionsDialog by remember { mutableStateOf(false) }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    if (showGoogleOnboarding) {
        GoogleOnboardingDialog(
            authViewModel = authViewModel,
            repository = repository,
            language = language,
            onDismiss = { showGoogleOnboarding = false },
            onOnboardingComplete = { user ->
                showGoogleOnboarding = false
                onLaunchPortal()
            }
        )
    }

    if (showDownloadModal) {
        DownloadModal(
            language = language,
            onDismiss = { repository.setDownloadModalVisible(false) },
            onLaunchInstalledApp = {
                repository.setDownloadModalVisible(false)
                onLaunchPortal()
            }
        )
    }

    // Modal: Izin Akses Perangkat & Perintah Pasang Aplikasi
    if (showPermissionsDialog) {
        DevicePermissionsDialog(
            isOpen = showPermissionsDialog,
            onDismiss = { showPermissionsDialog = false },
            onAllPermissionsGranted = {
                showPermissionsDialog = false
            }
        )
    }

    // Modal Menu: Company Profile with Integrated PDF Reader (13 Halaman)
    if (showCompanyProfileDialog) {
        CompanyProfilePdfDialog(
            isId = isId,
            onDismiss = { showCompanyProfileDialog = false }
        )
    }

    // Modal Menu: Pilihan Login (Badan Usaha / Karyawan vs Perorangan)
    if (showLoginChoiceDialog) {
        AlertDialog(
            onDismissRequest = { showLoginChoiceDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Login, contentDescription = null, tint = DarcorpBrown700)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isId) "Pilih Jalur Masuk (LOG IN)" else "Select Login Pathway",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DarcorpNavy900
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isId) "Silakan pilih jenis akses sesuai kebutuhan Anda:"
                               else "Please choose your access category:",
                        fontSize = 12.sp,
                        color = DarcorpSlate600
                    )

                    // Pilihan 1: BADAN USAHA / KARYAWAN
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarcorpBrown800,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showLoginChoiceDialog = false
                                onNavigateToLogin()
                            }
                            .testTag("login_choice_business_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Apartment, contentDescription = null, tint = DarcorpBrownCream, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isId) "LOG IN SEBAGAI BADAN USAHA / KARYAWAN" else "LOGIN AS BUSINESS ENTITY / STAFF",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = if (isId) "Untuk PT, CV, Yayasan, Pejabat & Karyawan" else "For PT, CV, Foundations, Officials & Staff",
                                    color = DarcorpBrownCream.copy(alpha = 0.85f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // Pilihan 2: PERORANGAN DENGAN 2 PILIHAN (GOOGLE & FACEBOOK)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarcorpBrown700)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_choice_personal_container")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = DarcorpBrown700, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isId) "LOG IN SEBAGAI PERORANGAN" else "LOGIN AS INDIVIDUAL / CLIENT",
                                        color = DarcorpNavy900,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = if (isId) "Pilih salah satu metode autentikasi resmi:" else "Select your verified authentication provider:",
                                        color = DarcorpSlate600,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Pilihan 1: LOG IN DENGAN AKUN GOOGLE
                            Button(
                                onClick = {
                                    showLoginChoiceDialog = false
                                    onNavigateToLogin()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .testTag("login_individual_google_btn")
                            ) {
                                Surface(shape = CircleShape, color = Color(0xFF4285F4), modifier = Modifier.size(18.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = "G", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isId) "1. Log In dengan Akun Google" else "1. Sign in with Google Account",
                                    color = Color(0xFF1E293B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Pilihan 2: LOG IN DENGAN FACEBOOK
                            Button(
                                onClick = {
                                    val fbUser = UserProfile(
                                        id = "usr_fb_" + System.currentTimeMillis().toString().takeLast(4),
                                        name = "Deny Akhbar (Facebook)",
                                        email = "denyakhbar@facebook.com",
                                        role = UserRole.PARTNER_BUYER,
                                        companyId = "comp_1"
                                    )
                                    repository.switchUserRole(fbUser)
                                    showLoginChoiceDialog = false
                                    android.widget.Toast.makeText(
                                        context,
                                        if (isId) "Berhasil Log In dengan Akun Facebook!" else "Signed in with Facebook Account!",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                    onLaunchPortal()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .testTag("login_individual_facebook_btn")
                            ) {
                                Surface(shape = CircleShape, color = Color.White, modifier = Modifier.size(18.dp)) {
                                    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1877F2)), contentAlignment = Alignment.Center) {
                                        Text(text = "f", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isId) "2. Log In dengan Facebook" else "2. Sign in with Facebook",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Pilihan 3: AKSES LANGSUNG LAYANAN BERBAYAR (VA & E-WALLET)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0FDF4),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarcorpEmerald)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val personalClient = UserProfile(
                                    id = "usr_client_" + System.currentTimeMillis().toString().takeLast(4),
                                    name = "Klien Perorangan / Non-Mitra",
                                    email = "client@darcorp.id",
                                    role = UserRole.PARTNER_BUYER,
                                    companyId = "comp_1"
                                )
                                repository.switchUserRole(personalClient)
                                repository.setActiveTab(PortalTab.PAID_SERVICES)
                                showLoginChoiceDialog = false
                                onLaunchPortal()
                            }
                            .testTag("login_choice_paid_services_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = DarcorpEmerald.copy(alpha = 0.2f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.Payments, contentDescription = null, tint = DarcorpEmeraldDark, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isId) "MENU LAYANAN BERBAYAR" else "PAID SERVICES MENU",
                                        color = DarcorpNavy900,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(shape = RoundedCornerShape(4.dp), color = DarcorpGold500) {
                                        Text(text = "VA & E-W", fontSize = 8.sp, fontWeight = FontWeight.Black, color = DarcorpNavy900, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                    }
                                }
                                Text(
                                    text = if (isId) "Pembayaran langsung via Virtual Account Bank & E-Wallet"
                                           else "Direct deposit via Bank Virtual Accounts & E-Wallets",
                                    color = DarcorpEmeraldDark,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLoginChoiceDialog = false }) {
                    Text(if (isId) "Batal" else "Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            GoogleSitesHeader(
                language = language,
                onLanguageToggle = { repository.toggleLanguage() },
                onDownloadClick = { repository.setDownloadModalVisible(true) },
                onLaunchPortalClick = onLaunchPortal,
                onGoogleLoginClick = onNavigateToLogin,
                onHomeClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                onCompanyProfileClick = { showCompanyProfileDialog = true },
                onLoginClick = { showLoginChoiceDialog = true }
            )
        },
        containerColor = DarcorpSlate50,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // D.A.R Legal Corporate Emblem as Full Screen Background
            Image(
                painter = painterResource(id = R.drawable.dar_legal_corporate_emblem),
                contentDescription = "DAR Corporate Background",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .alpha(0.18f)
                    .align(Alignment.Center)
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("landing_page_scroll"),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                // Hero Section (Tema Biru Muda & Putih, Tombol Cokelat)
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        DarcorpLightBlue100.copy(alpha = 0.70f),
                                        DarcorpLightBlueBg.copy(alpha = 0.50f),
                                        Color.White.copy(alpha = 0.35f)
                                    )
                                )
                            )
                            .padding(horizontal = 20.dp, vertical = 24.dp)
                    ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Badge Cokelat
                        Surface(
                            color = DarcorpBrown700,
                            shape = RoundedCornerShape(20.dp),
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = DarcorpBrownCream,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = Strings.get("hero_badge", language),
                                    color = DarcorpBrownCream,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Big Title in Brown on Light Blue
                        Text(
                            text = if (isId) "Ekosistem Multi-Perusahaan Terpadu DARcorporate"
                                   else "DARcorporate Unified Multi-Enterprise Ecosystem",
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            color = DarcorpBrown900,
                            textAlign = TextAlign.Center,
                            lineHeight = 30.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Deskripsi Aplikasi Resmi (Sesuai Permintaan)
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(DarcorpLightBlue300)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .testTag("app_full_description_card")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Gavel,
                                        contentDescription = null,
                                        tint = DarcorpBrown700,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isId) "Deskripsi Layanan & Payung Hukum" else "Service Description & Legal Protection",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = DarcorpBrown900
                                    )
                                }
                                Text(
                                    text = if (isId) {
                                        "Aplikasi layanan hukum dengan ekosistem multi- Badan Usaha & Perorangan terpadu. Terdapat Fitur Fitur terbaik untuk Perusahaan anda, baik internal (absensi karyawan, laporan kerja harian dll) Maupun eksternal (Pengamanan Perjanjian Kerjasama, Penawaran harga) dengan form digital yang dapat di isi langsung oleh calon klien anda, amankan bisnis anda dari perbuatan penipuan atau dari pihak pihak yang memungkinkan dapat merugikan anda, Layanan kami memberikan payung hukum pada bisnis anda, dan bersedia  memberikan tindakan pemulihan kerugian, dengam memberikan tekanan hukum kepada pihak pihak yang merugikan anda, baik secara perdata, maupun pidana. Kami melayani kepastian legalitas usaha anda, daftar merek, paten dan lain sebagainya. Konsultasikan rencana atau masalah anda GRATIS, baik untuk badan usaha maupun perorangan, kami akan memberikan analisa mendalam, dan Pendapat Hukum terbaik, dengan mempertimbangkan resiko hukum melekat, Kepatuhan, dan Produk hukum yang sehat."
                                    } else {
                                        "Comprehensive legal service application with an integrated multi-business entity & individual ecosystem. Features premier tools for your enterprise, both internal (employee attendance, daily work reports, etc.) and external (cooperation agreement security, price quotations) via digital forms completed directly by prospective clients. Shield your business from fraudulent acts or potential loss-causing parties. Our services provide a legal umbrella for your business and stand ready to deliver loss recovery actions by applying legal pressure—both civil and criminal—against detrimental parties. We serve your business legal certainty, trademark registration, patents, and more. Consult your plans or legal disputes for FREE for both corporate entities and individuals; we provide in-depth legal analysis and top-tier legal opinions considering inherent risks, compliance, and sound legal products."
                                    },
                                    fontSize = 11.5.sp,
                                    color = DarcorpSlate700,
                                    lineHeight = 17.sp,
                                    textAlign = TextAlign.Justify
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Prominent Download CTA Card (Putih Bersih dengan Border Biru Muda)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(DarcorpLightBlue300)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GetApp,
                                        contentDescription = null,
                                        tint = DarcorpBrown700,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isId) "Unduh & Pasang di Smartphone Anda" else "Download & Install on Smartphone",
                                        fontWeight = FontWeight.Bold,
                                        color = DarcorpBrown900,
                                        fontSize = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isId) "Aplikasi DARcorporate tersedia dalam format APK (Android) dan IPA (iOS) untuk mobilitas operasional."
                                           else "DARcorporate app is available in APK (Android) & IPA (iOS) formats for field operations.",
                                    fontSize = 11.sp,
                                    color = DarcorpSlate600,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // OPSI UTAMA: Jalankan Langsung di Browser Web Google (Tanpa Download)
                                Surface(
                                    color = Color(0xFFECFDF5),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF10B981)),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                color = Color(0xFF10B981),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = if (isId) "★ TANPA INSTALL" else "★ NO INSTALL",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isId) "Jalankan di Web Browser Google" else "Run in Google Web Browser",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color(0xFF065F46)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (isId) "Buka dan gunakan aplikasi secara penuh langsung di Google Chrome, Safari, HP/Laptop tanpa perlu mendownload file."
                                                   else "Use the full application directly in Google Chrome, Safari, or Laptop without downloading files.",
                                            fontSize = 10.5.sp,
                                            color = Color(0xFF047857),
                                            lineHeight = 15.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = {
                                                val webUrl = "https://ais-pre-2mnhsggsimf46z3pvhnfy5-101987637411.asia-southeast1.run.app"
                                                val browserIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(webUrl))
                                                context.startActivity(browserIntent)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth().height(40.dp).testTag("open_google_web_app_btn")
                                        ) {
                                            Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isId) "BUKA APLIKASI WEB SEKARANG" else "OPEN WEB APP NOW",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.5.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                                    Text(
                                        text = if (isId) "ATAU PASANG FILE DI PERANGKAT" else "OR INSTALL ON DEVICE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarcorpSlate600,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Android Download Button (Bar Tombol Berwarna Cokelat)
                                Button(
                                    onClick = { repository.setDownloadModalVisible(true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarcorpBrown700),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("hero_download_apk_cta")
                                ) {
                                    Icon(imageVector = Icons.Default.Android, contentDescription = null, tint = DarcorpBrownCream)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = Strings.get("hero_download_android", language),
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // iOS Download Button & Launch Portal in a row (Tombol Berwarna Cokelat)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { repository.setDownloadModalVisible(true) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = DarcorpBrown600),
                                        modifier = Modifier.weight(1f).height(44.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.PhoneIphone, contentDescription = null, tint = DarcorpBrownCream, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "iOS (.IPA)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }

                                    Button(
                                        onClick = onLaunchPortal,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = DarcorpBrown800),
                                        modifier = Modifier.weight(1f).height(44.dp).testTag("hero_open_portal_btn")
                                    ) {
                                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = DarcorpBrownCream, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isId) "Buka App" else "Open App",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Bagikan Link Unduhan ke Media Sosial
                                OutlinedButton(
                                    onClick = {
                                        val shareText = "Akses & Unduh Aplikasi Resmi DARcorporate (Layanan Hukum, Presensi Wajah, Payroll, Portal Kemitraan Form F1 & F2):\n\n" +
                                                "🌐 VERSI WEB GOOGLE (Langsung Digunakan Tanpa Download):\n" +
                                                "https://ais-pre-2mnhsggsimf46z3pvhnfy5-101987637411.asia-southeast1.run.app\n\n" +
                                                "📱 Download APK Android: https://sites.google.com/view/darcorp-official/darcorporate-release.apk\n" +
                                                "🍏 Download iOS (.IPA / Web Clip): https://sites.google.com/view/darcorp-official/darcorporate-ios.ipa\n" +
                                                "🏛️ Portal Google Sites: https://sites.google.com/view/darcorp-official"
                                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        val chooser = Intent.createChooser(sendIntent, if (isId) "Bagikan Link Akses & Unduhan DARcorporate" else "Share DARcorporate Access & Download Link")
                                        context.startActivity(chooser)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(40.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = DarcorpBrown700, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isId) "Bagikan Link Unduhan ke Media Sosial" else "Share Download Link to Social Media",
                                        color = DarcorpBrown700,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Tombol Izin Akses Perangkat & Perintah Pasang Aplikasi
                                Button(
                                    onClick = { showPermissionsDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .testTag("open_permissions_dialog_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = DarcorpGold400,
                                        modifier = Modifier.size(17.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isId) "Izin Akses Perangkat & Perintah Pasang" else "Device Permissions & Install App",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = Strings.get("hero_version_info", language),
                            fontSize = 10.sp,
                            color = DarcorpSlate600
                        )
                    }
                }
            }

            // Quick Interactive Demo Role Launcher
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isId) "Uji Coba Peran Pengguna (Simulasi Sesi)"
                                   else "Interactive Role Simulation (Session Switch)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DarcorpNavy900
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DarcorpGold500.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (isId) "Aktif: ${currentUser.name.take(18)}..." else "Active: ${currentUser.name.take(18)}...",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarcorpNavy900,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isId) "Pilih salah satu peran di bawah ini untuk menguji hak akses unik pada aplikasi DARCORP:"
                               else "Select any role below to test the distinct RBAC access in the DARCORP app:",
                        fontSize = 11.sp,
                        color = DarcorpSlate600
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Role Buttons Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RoleChip(
                            title = if (isId) "Konsultan Hukum" else "Legal Counsel",
                            subtitle = if (isId) "Super Master" else "Super Admin",
                            icon = Icons.Default.Gavel,
                            accentColor = DarcorpCrimson,
                            isSelected = currentUser.role == UserRole.LEGAL_OFFICER,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                repository.switchUserRole(repository.demoUsers[0])
                                onLaunchPortal()
                            }
                        )

                        RoleChip(
                            title = if (isId) "Direktur Utama" else "CEO / Dirut",
                            subtitle = if (isId) "Operasional" else "Operations",
                            icon = Icons.Default.BusinessCenter,
                            accentColor = DarcorpGold500,
                            isSelected = currentUser.role == UserRole.DIREKTUR_UTAMA,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                repository.switchUserRole(repository.demoUsers[1])
                                onLaunchPortal()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RoleChip(
                            title = if (isId) "Karyawan Tetap" else "Permanent Staff",
                            subtitle = if (isId) "Gaji Bulanan" else "Monthly Payroll",
                            icon = Icons.Default.Badge,
                            accentColor = DarcorpNavy600,
                            isSelected = currentUser.role == UserRole.PERMANENT_STAFF,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                repository.switchUserRole(repository.demoUsers[5])
                                repository.setActiveTab(PortalTab.ATTENDANCE_REPORTS)
                                onLaunchPortal()
                            }
                        )

                        RoleChip(
                            title = if (isId) "Karyawan Borongan" else "Temporary Staff",
                            subtitle = if (isId) "Upah Mingguan" else "Weekly Payroll",
                            icon = Icons.Default.Engineering,
                            accentColor = DarcorpAmber,
                            isSelected = currentUser.role == UserRole.TEMPORARY_STAFF,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                repository.switchUserRole(repository.demoUsers[7])
                                repository.setActiveTab(PortalTab.ATTENDANCE_REPORTS)
                                onLaunchPortal()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RoleChip(
                            title = if (isId) "Calon Buyer" else "Prospective Buyer",
                            subtitle = "Form F1 & F2",
                            icon = Icons.Default.ShoppingCart,
                            accentColor = DarcorpEmerald,
                            isSelected = currentUser.role == UserRole.PARTNER_BUYER,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                repository.switchUserRole(repository.demoUsers[9])
                                repository.setActiveTab(PortalTab.PARTNER_FORMS)
                                onLaunchPortal()
                            }
                        )

                        RoleChip(
                            title = if (isId) "Calon Penyuplai" else "Supplier Partner",
                            subtitle = "Form F1 & F2",
                            icon = Icons.Default.Inventory2,
                            accentColor = Color(0xFF8B5CF6),
                            isSelected = currentUser.role == UserRole.PARTNER_SUPPLIER,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                repository.switchUserRole(repository.demoUsers[10])
                                repository.setActiveTab(PortalTab.PARTNER_FORMS)
                                onLaunchPortal()
                            }
                        )
                    }
                }
            }

            // 6 Core Architectural Pillars
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (isId) "Keunggulan Sistem Tata Kelola DARCORP"
                               else "DARCORP Corporate Governance Pillars",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DarcorpNavy900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isId) "Dirancang khusus sesuai arahan legalitas dan tata kelola perusahaan modern:"
                               else "Engineered in accordance with strict corporate compliance standards:",
                        fontSize = 11.sp,
                        color = DarcorpSlate600
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    PillarCard(
                        icon = Icons.Default.Apartment,
                        title = Strings.get("feature_multi_company_title", language),
                        description = Strings.get("feature_multi_company_desc", language),
                        accentColor = DarcorpNavy700
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PillarCard(
                        icon = Icons.Default.Security,
                        title = Strings.get("feature_dual_master_title", language),
                        description = Strings.get("feature_dual_master_desc", language),
                        accentColor = DarcorpCrimson
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PillarCard(
                        icon = Icons.Default.Face,
                        title = Strings.get("feature_attendance_title", language),
                        description = Strings.get("feature_attendance_desc", language),
                        accentColor = DarcorpEmerald
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PillarCard(
                        icon = Icons.Default.Forum,
                        title = Strings.get("feature_chat_title", language),
                        description = Strings.get("feature_chat_desc", language),
                        accentColor = DarcorpGold600
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PillarCard(
                        icon = Icons.Default.AutoDelete,
                        title = Strings.get("feature_documents_title", language),
                        description = Strings.get("feature_documents_desc", language),
                        accentColor = DarcorpAmber
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PillarCard(
                        icon = Icons.Default.Handshake,
                        title = Strings.get("feature_partners_title", language),
                        description = Strings.get("feature_partners_desc", language),
                        accentColor = DarcorpCyan
                    )
                }
            }

            // Google Sites Footer
            item {
                Surface(
                    color = DarcorpNavy900,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "DARcorporate ENTERPRISE PLATFORM",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = DarcorpGold400,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isId) "Dikelola oleh Konsultan Hukum & Direktur Utama Perusahaan\nSCBD Tower 18, Jakarta Selatan • Email: legal@darcorp.id"
                                   else "Governed by Corporate Legal Counsel & CEO\nSCBD Tower 18, South Jakarta • Email: legal@darcorp.id",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = DarcorpSlate700)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "© 2026 DARcorporate. All Rights Reserved.",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                            TextButton(
                                onClick = { repository.setDownloadModalVisible(true) },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = if (isId) "Unduh APK (v2.4.0)" else "Download APK (v2.4.0)",
                                    fontSize = 10.sp,
                                    color = DarcorpGold400,
                                    fontWeight = FontWeight.Bold
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

@Composable
fun RoleChip(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) accentColor.copy(alpha = 0.15f) else Color.White,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) accentColor else Color(0xFFE2E8F0))
        ),
        modifier = modifier.height(64.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = DarcorpNavy900,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = DarcorpSlate600,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun PillarCard(
    icon: ImageVector,
    title: String,
    description: String,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DarcorpNavy900
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = DarcorpSlate700,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
