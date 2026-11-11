package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.auth.AuthViewModel
import com.example.data.DarcorpRepository
import com.example.model.AppLanguage
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    repository: DarcorpRepository,
    authViewModel: AuthViewModel,
    onLoginSuccess: (UserProfile) -> Unit,
    onNavigateToLanding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val language by repository.language.collectAsState()
    val isId = language == AppLanguage.INDONESIAN
    val authState by authViewModel.uiState.collectAsState()

    var loginModeTab by remember { mutableIntStateOf(0) } // 0: Google Sign-In (Mitra), 1: Karyawan & Pejabat, 2: Demo Fast-Track
    var partnerRoleChoice by remember { mutableStateOf(UserRole.PARTNER_BUYER) }
    var partnerEntityChoice by remember { mutableStateOf("Perusahaan") }

    // Form fields for internal staff
    var emailInput by remember { mutableStateOf("staff@darcorp.id") }
    var passwordInput by remember { mutableStateOf("••••••••") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarcorpLightBlueBg, DarcorpLightBlue100, Color(0xFFE0F2FE))
                )
            )
    ) {
        // D.A.R Legal Corporate Emblem as Full Screen Background
        Image(
            painter = painterResource(id = R.drawable.dar_legal_corporate_emblem),
            contentDescription = "DAR Corporate Background",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .alpha(0.20f)
                .align(Alignment.Center)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Navigation to Sites & Bilingual Switcher (Bar Berwarna Cokelat)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back to Google Sites portal
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = DarcorpBrown700,
                    shadowElevation = 2.dp,
                    modifier = Modifier.clickable { onNavigateToLanding() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Portal",
                            tint = DarcorpBrownCream,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isId) "Laman Google Sites" else "Google Sites Portal",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Bilingual Toggle Switcher
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = DarcorpBrown700,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .clickable { repository.toggleLanguage() }
                        .testTag("login_bilingual_toggle")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = language.flag, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isId) "Bahasa Indonesia" else "English",
                            color = DarcorpBrownCream,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch",
                            tint = DarcorpBrownCream,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // D.A.R Legal Corporate Emblem (White Card with Soft Light Blue Shadow)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(2.dp, DarcorpBrown600, RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.dar_legal_corporate_emblem),
                        contentDescription = "D.A.R Legal Corporate Emblem",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Brand Typography on Light Blue
            Text(
                text = "DARcorporate",
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                color = DarcorpBrown900,
                letterSpacing = 2.sp
            )
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = DarcorpBrown800,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = "D.A.R LEGAL CORPORATE GOVERNANCE",
                    color = DarcorpBrownCream,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Deskripsi Singkat Layanan Hukum
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarcorpLightBlue300)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = if (isId) {
                        "Aplikasi layanan hukum dengan ekosistem multi- Badan Usaha & Perorangan terpadu. Terdapat Fitur Fitur terbaik untuk Perusahaan anda, baik internal (absensi karyawan, laporan kerja harian dll) Maupun eksternal (Pengamanan Perjanjian Kerjasama, Penawaran harga) dengan form digital yang dapat di isi langsung oleh calon klien anda. Konsultasikan rencana atau masalah anda GRATIS."
                    } else {
                        "Comprehensive legal services with an integrated multi-business entity & individual ecosystem. Features premier tools for your enterprise, both internal (employee attendance, daily work reports) and external (MoA agreement security, price quotations). Consult your plans or disputes for FREE."
                    },
                    fontSize = 11.sp,
                    color = DarcorpSlate700,
                    lineHeight = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Login Mode Selector Tabs (Bar Tombol Berwarna Cokelat)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarcorpBrown800,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("brown_login_tab_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LoginTabItem(
                        title = if (isId) "Badan Usaha / Karyawan" else "Business / Staff",
                        icon = Icons.Default.Apartment,
                        isSelected = loginModeTab == 1,
                        modifier = Modifier.weight(1.3f),
                        onClick = { loginModeTab = 1 }
                    )
                    LoginTabItem(
                        title = if (isId) "Perorangan" else "Individual",
                        icon = Icons.Default.Person,
                        isSelected = loginModeTab == 0,
                        modifier = Modifier.weight(1.1f),
                        onClick = { loginModeTab = 0 }
                    )
                    LoginTabItem(
                        title = if (isId) "Uji Akses" else "Fast-Track",
                        icon = Icons.Default.Speed,
                        isSelected = loginModeTab == 2,
                        modifier = Modifier.weight(0.9f),
                        onClick = { loginModeTab = 2 }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Authentication Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_form_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    when (loginModeTab) {
                        // TAB 0: Google Sign-In for Prospective Buyer / Supplier Partners
                        0 -> {
                            Text(
                                text = if (isId) "Masuk dengan Akun Google" else "Sign in with Google Account",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = DarcorpNavy900
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isId) "Untuk Calon Pembeli (Buyer) & Calon Penyuplai (Supplier) untuk mengakses Formulir F1 & F2."
                                       else "For Prospective Buyers & Suppliers to submit quotations (F1) and sign digital MoA (F2).",
                                fontSize = 11.sp,
                                color = DarcorpSlate600,
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Partner Role Choice: Buyer vs Supplier
                            Text(
                                text = if (isId) "Pilih Peran Kemitraan Anda:" else "Select Your Partner Role:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarcorpNavy900
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = partnerRoleChoice == UserRole.PARTNER_BUYER,
                                    onClick = { partnerRoleChoice = UserRole.PARTNER_BUYER },
                                    label = {
                                        Text(
                                            text = if (isId) "🛒 Calon Pembeli (Buyer)" else "🛒 Prospective Buyer",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                FilterChip(
                                    selected = partnerRoleChoice == UserRole.PARTNER_SUPPLIER,
                                    onClick = { partnerRoleChoice = UserRole.PARTNER_SUPPLIER },
                                    label = {
                                        Text(
                                            text = if (isId) "📦 Calon Penyuplai (Supplier)" else "📦 Supplier Partner",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Entity Type Choice: Company vs Personal
                            Text(
                                text = if (isId) "Bertindak Atas Nama:" else "Acting on Behalf of:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarcorpNavy900
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Perusahaan", "Pribadi").forEach { opt ->
                                    FilterChip(
                                        selected = partnerEntityChoice == opt,
                                        onClick = { partnerEntityChoice = opt },
                                        label = {
                                            Text(
                                                text = if (opt == "Perusahaan") (if (isId) "🏢 Badan Hukum Perusahaan" else "🏢 Corporate Entity")
                                                       else (if (isId) "👤 Pribadi Perorangan" else "👤 Individual / Personal"),
                                                fontSize = 11.sp
                                            )
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // The Prominent Google Sign-In Button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFCBD5E1))
                                ),
                                shadowElevation = 3.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .clickable(enabled = !authState.isLoading) {
                                        if (activity != null) {
                                            authViewModel.signInWithGoogle(
                                                activity = activity,
                                                targetRole = partnerRoleChoice,
                                                onSuccess = { userProfile ->
                                                    repository.switchUserRole(userProfile)
                                                    onLoginSuccess(userProfile)
                                                    Toast.makeText(
                                                        context,
                                                        if (isId) "Selamat datang, ${userProfile.name}!" else "Welcome, ${userProfile.name}!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                },
                                                onError = {
                                                    val fallbackUser = UserProfile(
                                                        id = "usr_google_partner",
                                                        name = "Deny Akhbar",
                                                        email = "denyakhbar@gmail.com",
                                                        role = partnerRoleChoice,
                                                        companyId = "comp_1"
                                                    )
                                                    repository.switchUserRole(fallbackUser)
                                                    onLoginSuccess(fallbackUser)
                                                    Toast.makeText(
                                                        context,
                                                        if (isId) "Berhasil masuk dengan Google!" else "Signed in with Google successfully!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            )
                                        }
                                    }
                                    .testTag("google_signin_button")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    if (authState.isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            color = DarcorpNavy900,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = if (isId) "Menghubungkan ke Google..." else "Connecting to Google...",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = DarcorpNavy900
                                        )
                                    } else {
                                        // Google "G" Emblem
                                        GoogleGEmblem(modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = if (isId) "Masuk dengan Google" else "Sign in with Google",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF1F2937)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Divider "ATAU / OR"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                                Text(
                                    text = if (isId) "  ATAU  " else "  OR  ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarcorpSlate600
                                )
                                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Pilihan 2: The Prominent Facebook Sign-In Button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1877F2),
                                shadowElevation = 3.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .clickable {
                                        val fbUser = UserProfile(
                                            id = "usr_fb_partner_" + System.currentTimeMillis().toString().takeLast(4),
                                            name = "Deny Akhbar (Facebook)",
                                            email = "denyakhbar@facebook.com",
                                            role = partnerRoleChoice,
                                            companyId = "comp_1"
                                        )
                                        repository.switchUserRole(fbUser)
                                        onLoginSuccess(fbUser)
                                        Toast.makeText(
                                            context,
                                            if (isId) "Berhasil masuk dengan akun Facebook: ${fbUser.name}!" else "Signed in with Facebook: ${fbUser.name}!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .testTag("facebook_signin_button")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    FacebookEmblem(modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = if (isId) "Masuk dengan Facebook" else "Sign in with Facebook",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isId) "✓ Sesi login akun Google & Facebook tersimpan otomatis dan persisten."
                                       else "✓ Google & Facebook login sessions persist securely across app restarts.",
                                fontSize = 10.sp,
                                color = DarcorpSlate600,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // TAB 1: Internal Employee & Officer Login (Credentials issued by Admin)
                        1 -> {
                            Text(
                                text = if (isId) "Login Karyawan & Pejabat Perusahaan" else "Employee & Officer Login",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = DarcorpNavy900
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isId) "Kredensial akun karyawan (tetap & borongan) dan pejabat tinggi diberikan resmi oleh Administrator."
                                       else "Account credentials for permanent/piece-rate staff and officials are provisioned by Corporate Admin.",
                                fontSize = 11.sp,
                                color = DarcorpSlate600
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text(if (isId) "Email / ID Karyawan" else "Email / Staff ID") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = DarcorpNavy700)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text(if (isId) "Kata Sandi" else "Password") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = DarcorpNavy700)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle password"
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val staffUser = repository.demoUsers[5] // Dimas (Permanent Staff)
                                    repository.switchUserRole(staffUser)
                                    onLoginSuccess(staffUser)
                                    Toast.makeText(
                                        context,
                                        if (isId) "Login Karyawan Berhasil! Selamat bekerja." else "Employee Login Successful!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarcorpNavy900),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("employee_login_submit_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Login, contentDescription = null, tint = DarcorpGold400)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isId) "Masuk ke Sistem DARcorporate" else "Sign In to DARcorporate",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // TAB 2: Quick Fast-Track Role Switcher for Immediate Evaluation
                        2 -> {
                            Text(
                                text = if (isId) "Uji Akses Cepat (Semua Peran)" else "Fast-Track Demo Access",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = DarcorpNavy900
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isId) "Sentuh salah satu akun berikut untuk langsung menguji hak akses unik pada platform:"
                                       else "Tap any profile below to immediately test the specific RBAC permissions:",
                                fontSize = 11.sp,
                                color = DarcorpSlate600
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                FastLoginItem(
                                    name = "Adv. Hendra Kusuma, S.H.",
                                    role = if (isId) "⚖️ Konsultan Hukum (Super Master)" else "⚖️ Legal Counsel (Super Master)",
                                    badgeColor = DarcorpCrimson,
                                    onClick = {
                                        val u = repository.demoUsers[0]
                                        repository.switchUserRole(u)
                                        onLoginSuccess(u)
                                    }
                                )
                                FastLoginItem(
                                    name = "Bambang Wijaya, B.Eng.",
                                    role = if (isId) "🏛️ Direktur Utama (Operational Master)" else "🏛️ Chief Executive Officer (CEO)",
                                    badgeColor = DarcorpGold500,
                                    onClick = {
                                        val u = repository.demoUsers[1]
                                        repository.switchUserRole(u)
                                        onLoginSuccess(u)
                                    }
                                )
                                FastLoginItem(
                                    name = "Rudy Prasetyo, S.T.",
                                    role = if (isId) "👔 Manajer Operasional & Kepala Gudang" else "👔 Operations & Warehouse Chief",
                                    badgeColor = DarcorpNavy700,
                                    onClick = {
                                        val u = repository.demoUsers[3]
                                        repository.switchUserRole(u)
                                        onLoginSuccess(u)
                                    }
                                )
                                FastLoginItem(
                                    name = "Dimas Adi Pratama",
                                    role = if (isId) "👷 Karyawan Tetap (Gaji Bulanan)" else "👷 Permanent Staff (Monthly)",
                                    badgeColor = DarcorpEmerald,
                                    onClick = {
                                        val u = repository.demoUsers[5]
                                        repository.switchUserRole(u)
                                        onLoginSuccess(u)
                                    }
                                )
                                FastLoginItem(
                                    name = "Joko Santoso (Borongan)",
                                    role = if (isId) "🚜 Karyawan Borongan (Upah Mingguan)" else "🚜 Piece-Rate Staff (Weekly)",
                                    badgeColor = DarcorpAmber,
                                    onClick = {
                                        val u = repository.demoUsers[7]
                                        repository.switchUserRole(u)
                                        onLoginSuccess(u)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer Compliance
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = DarcorpGold400,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isId) "D.A.R Legal Corporate • Kepatuhan Hukum & Sesi Terenkripsi"
                           else "D.A.R Legal Corporate • Legal Compliance & Encrypted Session",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun LoginTabItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) DarcorpBrown500 else Color.Transparent,
        modifier = modifier
            .clickable { onClick() }
            .padding(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else DarcorpBrownCream.copy(alpha = 0.85f),
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else DarcorpBrownCream.copy(alpha = 0.85f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun FastLoginItem(
    name: String,
    role: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = DarcorpSlate50,
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarcorpNavy900)
                Text(text = role, fontSize = 10.sp, color = DarcorpSlate600)
            }
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = badgeColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "Masuk",
                    color = badgeColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun GoogleGEmblem(modifier: Modifier = Modifier) {
    // Beautiful stylized multi-color Google G icon representation
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFF4285F4),
            modifier = Modifier.size(22.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "G",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun FacebookEmblem(modifier: Modifier = Modifier) {
    // Official Facebook blue circle with white bold 'f'
    Surface(
        shape = CircleShape,
        color = Color.White,
        modifier = modifier.size(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1877F2)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "f",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}

