package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

import com.example.auth.AuthViewModel
import com.example.ui.components.GoogleOnboardingDialog

@Composable
fun LandingPageScreen(
    repository: DarcorpRepository,
    authViewModel: AuthViewModel,
    onLaunchPortal: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val language by repository.language.collectAsState()
    val isId = language == AppLanguage.INDONESIAN
    val showDownloadModal by repository.downloadModalVisible.collectAsState()
    val currentUser by repository.currentUser.collectAsState()
    var showGoogleOnboarding by remember { mutableStateOf(false) }

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

    Scaffold(
        topBar = {
            GoogleSitesHeader(
                language = language,
                onLanguageToggle = { repository.toggleLanguage() },
                onDownloadClick = { repository.setDownloadModalVisible(true) },
                onLaunchPortalClick = onLaunchPortal,
                onGoogleLoginClick = onNavigateToLogin
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
                            text = if (isId) "Ekosistem Multi-Perusahaan Terpadu DARCORP"
                                   else "DARCORP Unified Multi-Enterprise Ecosystem",
                            fontWeight = FontWeight.Black,
                            fontSize = 25.sp,
                            color = DarcorpBrown900,
                            textAlign = TextAlign.Center,
                            lineHeight = 31.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Subtitle
                        Text(
                            text = Strings.get("hero_subtitle", language),
                            fontSize = 12.sp,
                            color = DarcorpSlate700,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

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
                                    text = if (isId) "Aplikasi DARCORP tersedia dalam format APK (Android) dan IPA (iOS) untuk mobilitas operasional."
                                           else "DARCORP app is available in APK (Android) & IPA (iOS) formats for field operations.",
                                    fontSize = 11.sp,
                                    color = DarcorpSlate600,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(16.dp))

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
                            text = "DARCORP ENTERPRISE PLATFORM",
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
                                text = "© 2026 DARCORP. All Rights Reserved.",
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
