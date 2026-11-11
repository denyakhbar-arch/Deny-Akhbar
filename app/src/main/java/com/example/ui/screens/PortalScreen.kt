package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.R
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DarcorpRepository
import com.example.data.PortalTab
import com.example.model.*
import com.example.ui.components.DigitalSignaturePad
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalScreen(
    repository: DarcorpRepository,
    onBackToLanding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by repository.language.collectAsState()
    val isId = language == AppLanguage.INDONESIAN
    val currentUser by repository.currentUser.collectAsState()
    val activeTab by repository.activeTab.collectAsState()
    val companies by repository.companies.collectAsState()
    val attendances by repository.attendances.collectAsState()
    val chatMessages by repository.chatMessages.collectAsState()
    val documents by repository.documents.collectAsState()
    val f1Submissions by repository.f1Submissions.collectAsState()
    val f2Agreements by repository.f2Agreements.collectAsState()

    var selectedCompany by remember { mutableStateOf(companies.firstOrNull() ?: Company("c", "DARCORP", EntityType.PT, BusinessSector.TRADING, "1", "1", "SCBD", "2026")) }
    var showRoleSwitchDialog by remember { mutableStateOf(false) }
    var showAddCompanyDialog by remember { mutableStateOf(false) }

    // Role Switcher Dialog
    if (showRoleSwitchDialog) {
        AlertDialog(
            onDismissRequest = { showRoleSwitchDialog = false },
            title = {
                Text(
                    text = if (isId) "Ganti Akun & Hak Akses" else "Switch Account & Role",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isId) "Pilih profil untuk menguji isolasi hak akses:"
                               else "Select account to test RBAC isolation:",
                        fontSize = 12.sp,
                        color = DarcorpSlate600
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn(modifier = Modifier.heightIn(max = 350.dp)) {
                        items(repository.demoUsers) { user ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (user.id == currentUser.id) DarcorpGold500.copy(alpha = 0.2f) else Color.Transparent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        repository.switchUserRole(user)
                                        showRoleSwitchDialog = false
                                    }
                                    .padding(vertical = 4.dp, horizontal = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(DarcorpNavy900),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.avatarInitial,
                                            color = DarcorpGold400,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = user.name,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp,
                                            color = DarcorpNavy900
                                        )
                                        Text(
                                            text = if (isId) user.role.labelId else user.role.labelEn,
                                            fontSize = 10.sp,
                                            color = DarcorpSlate600
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoleSwitchDialog = false }) {
                    Text(if (isId) "Tutup" else "Close")
                }
            }
        )
    }

    // Add Company Dialog (PT, CV, Yayasan)
    if (showAddCompanyDialog) {
        var newName by remember { mutableStateOf("") }
        var selectedEntityType by remember { mutableStateOf(EntityType.PT) }
        var selectedSector by remember { mutableStateOf(BusinessSector.TRADING) }
        var newNib by remember { mutableStateOf("") }
        var newNpwp by remember { mutableStateOf("") }
        var newAddress by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddCompanyDialog = false },
            title = {
                Text(
                    text = if (isId) "Daftarkan Perusahaan Baru" else "Register New Enterprise",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                ) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text(if (isId) "Nama Perusahaan" else "Company Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = if (isId) "Badan Usaha:" else "Entity Type:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        EntityType.values().forEach { type ->
                            FilterChip(
                                selected = selectedEntityType == type,
                                onClick = { selectedEntityType = type },
                                label = { Text(type.name, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newNib,
                        onValueChange = { newNib = it },
                        label = { Text("Nomor NIB") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newNpwp,
                        onValueChange = { newNpwp = it },
                        label = { Text("NPWP Perusahaan") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newAddress,
                        onValueChange = { newAddress = it },
                        label = { Text(if (isId) "Alamat Domisili Hukum" else "Legal Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            val newComp = Company(
                                id = "comp_" + System.currentTimeMillis(),
                                name = newName,
                                entityType = selectedEntityType,
                                sector = selectedSector,
                                nib = if (newNib.isBlank()) "0123984719283" else newNib,
                                npwp = if (newNpwp.isBlank()) "01.234.567.8-901.000" else newNpwp,
                                address = if (newAddress.isBlank()) "SCBD Jakarta" else newAddress,
                                legalDate = "Nov 2026"
                            )
                            repository.addCompany(newComp)
                            selectedCompany = newComp
                            showAddCompanyDialog = false
                            Toast.makeText(context, if (isId) "Perusahaan berhasil didaftarkan!" else "Company registered!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarcorpNavy900)
                ) {
                    Text(if (isId) "Simpan & Daftarkan" else "Register")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCompanyDialog = false }) {
                    Text(if (isId) "Batal" else "Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Surface(
                color = DarcorpNavy900,
                shadowElevation = 6.dp
            ) {
                Column {
                    // Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBackToLanding) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "DARCORP APP",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = DarcorpGold400
                                )
                                Text(
                                    text = selectedCompany.name,
                                    fontSize = 11.sp,
                                    color = Color(0xFFCBD5E1),
                                    maxLines = 1
                                )
                            }
                        }

                        // Right actions: Role button + Language toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // User Role Badge (Tappable to switch)
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = DarcorpGold500.copy(alpha = 0.2f),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(DarcorpGold400)
                                ),
                                modifier = Modifier
                                    .clickable { showRoleSwitchDialog = true }
                                    .testTag("app_role_switch_badge")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = DarcorpGold400,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = currentUser.name.split(" ").firstOrNull() ?: "User",
                                        color = DarcorpGold400,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Language Pill
                            IconButton(onClick = { repository.toggleLanguage() }) {
                                Text(text = language.flag, fontSize = 16.sp)
                            }
                        }
                    }

                    // Horizontal Navigation Scroll Tabs
                    ScrollableTabRow(
                        selectedTabIndex = activeTab.ordinal,
                        containerColor = DarcorpNavy800,
                        contentColor = Color.White,
                        edgePadding = 8.dp,
                        indicator = {}
                    ) {
                        Tab(
                            selected = activeTab == PortalTab.DASHBOARD_COMPANIES,
                            onClick = { repository.setActiveTab(PortalTab.DASHBOARD_COMPANIES) },
                            text = { Text(if (isId) "🏢 Entitas" else "🏢 Entities", fontSize = 12.sp) }
                        )
                        Tab(
                            selected = activeTab == PortalTab.ATTENDANCE_REPORTS,
                            onClick = { repository.setActiveTab(PortalTab.ATTENDANCE_REPORTS) },
                            text = { Text(if (isId) "📍 Absensi" else "📍 Attendance", fontSize = 12.sp) }
                        )
                        Tab(
                            selected = activeTab == PortalTab.PAYROLL_MODULE,
                            onClick = { repository.setActiveTab(PortalTab.PAYROLL_MODULE) },
                            text = { Text(if (isId) "💰 Upah" else "💰 Payroll", fontSize = 12.sp) }
                        )
                        Tab(
                            selected = activeTab == PortalTab.TIERED_CHAT,
                            onClick = { repository.setActiveTab(PortalTab.TIERED_CHAT) },
                            text = { Text(if (isId) "💬 Obrolan 3-Tier" else "💬 3-Tier Chat", fontSize = 12.sp) }
                        )
                        Tab(
                            selected = activeTab == PortalTab.DOCUMENT_VAULT,
                            onClick = { repository.setActiveTab(PortalTab.DOCUMENT_VAULT) },
                            text = { Text(if (isId) "📁 Dokumen 30H" else "📁 30D Vault", fontSize = 12.sp) }
                        )
                        Tab(
                            selected = activeTab == PortalTab.PARTNER_FORMS,
                            onClick = { repository.setActiveTab(PortalTab.PARTNER_FORMS) },
                            text = { Text(if (isId) "🤝 Mitra (F1 & F2)" else "🤝 Partners (F1&F2)", fontSize = 12.sp) }
                        )
                        if (currentUser.role == UserRole.LEGAL_OFFICER) {
                            Tab(
                                selected = activeTab == PortalTab.LEGAL_SUPER_CONSOLE,
                                onClick = { repository.setActiveTab(PortalTab.LEGAL_SUPER_CONSOLE) },
                                text = { Text(if (isId) "⚖️ Legal Super" else "⚖️ Legal Console", fontSize = 12.sp, color = DarcorpCrimson) }
                            )
                        }
                    }
                }
            }
        },
        containerColor = DarcorpSlate50,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // D.A.R Legal Corporate Emblem as Full Screen Background Watermark
            Image(
                painter = painterResource(id = R.drawable.dar_legal_corporate_emblem),
                contentDescription = "DAR Corporate Background",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .alpha(0.12f)
                    .align(Alignment.Center)
            )

            when (activeTab) {
                PortalTab.DASHBOARD_COMPANIES -> EntitiesAndOfficialsTab(
                    isId = isId,
                    selectedCompany = selectedCompany,
                    companies = companies,
                    onSelectCompany = { selectedCompany = it },
                    onOpenAddCompany = { showAddCompanyDialog = true },
                    currentUser = currentUser
                )
                PortalTab.ATTENDANCE_REPORTS -> AttendanceAndDailyReportTab(
                    isId = isId,
                    repository = repository,
                    currentUser = currentUser,
                    attendances = attendances
                )
                PortalTab.PAYROLL_MODULE -> PayrollModuleTab(
                    isId = isId,
                    currentUser = currentUser
                )
                PortalTab.TIERED_CHAT -> TieredChatTab(
                    isId = isId,
                    repository = repository,
                    currentUser = currentUser,
                    messages = chatMessages
                )
                PortalTab.DOCUMENT_VAULT -> DocumentVaultTab(
                    isId = isId,
                    repository = repository,
                    documents = documents,
                    selectedCompany = selectedCompany,
                    currentUser = currentUser
                )
                PortalTab.PARTNER_FORMS -> PartnerFormsTab(
                    isId = isId,
                    repository = repository,
                    f1List = f1Submissions,
                    f2List = f2Agreements
                )
                PortalTab.LEGAL_SUPER_CONSOLE -> LegalSuperConsoleTab(
                    isId = isId,
                    repository = repository,
                    companies = companies
                )
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 1: Entitas & Pejabat Tinggi (Companies & 5 High Officials)
// -------------------------------------------------------------
@Composable
fun EntitiesAndOfficialsTab(
    isId: Boolean,
    selectedCompany: Company,
    companies: List<Company>,
    onSelectCompany: (Company) -> Unit,
    onOpenAddCompany: () -> Unit,
    currentUser: UserProfile
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Company Selector Row
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isId) "Pilih Entitas Perusahaan:" else "Select Active Company Entity:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarcorpNavy900
                        )

                        if (currentUser.role == UserRole.LEGAL_OFFICER || currentUser.role == UserRole.DIREKTUR_UTAMA) {
                            TextButton(onClick = onOpenAddCompany) {
                                Icon(imageVector = Icons.Default.AddBusiness, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isId) "+ Daftarkan Baru" else "+ Register New", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        companies.forEach { comp ->
                            val isSelected = comp.id == selectedCompany.id
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) DarcorpNavy900 else DarcorpSlate100,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSelectCompany(comp) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = comp.entityType.name,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) DarcorpGold400 else DarcorpSlate600
                                    )
                                    Text(
                                        text = comp.name.replace("DARCORP ", ""),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else DarcorpNavy900,
                                        maxLines = 1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Company Legal Details
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = selectedCompany.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = DarcorpNavy900
                            )
                            Text(
                                text = if (isId) selectedCompany.entityType.labelId else selectedCompany.entityType.labelEn,
                                fontSize = 12.sp,
                                color = DarcorpSlate600
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DarcorpEmerald.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (isId) "Legalitas Terdaftar" else "Legally Registered",
                                color = DarcorpEmeraldDark,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = DarcorpSlate100)
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailItem(label = if (isId) "Bidang Bisnis" else "Business Sector", value = if (isId) selectedCompany.sector.labelId else selectedCompany.sector.labelEn)
                    DetailItem(label = "Nomor NIB", value = selectedCompany.nib)
                    DetailItem(label = "NPWP Perusahaan", value = selectedCompany.npwp)
                    DetailItem(label = if (isId) "Alamat Domisili Hukum" else "Legal Domicile", value = selectedCompany.address)
                    DetailItem(label = if (isId) "Tanggal Pengesahan" else "Ratification Date", value = selectedCompany.legalDate)
                }
            }
        }

        // Dual-Master Controller Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarcorpNavy900),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isId) "Dua Akun Pemegang Kendali Perusahaan" else "Dual-Master Control Governance",
                        color = DarcorpGold400,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarcorpNavy800,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (isId) "⚖️ Konsultan Hukum" else "⚖️ Legal Counsel",
                                    color = DarcorpCrimson,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "Adv. Hendra Kusuma, S.H.", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = if (isId) "Kuasa Mutlak: Hapus Data/Akun & Kelola Member" else "Supreme Authority: Data Purge & Members", color = Color(0xFF94A3B8), fontSize = 9.sp)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarcorpNavy800,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (isId) "🏛️ Direktur Utama" else "🏛️ Chief Executive",
                                    color = DarcorpGold400,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "Bambang Wijaya, B.Eng.", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = if (isId) "Operasional: Pendaftaran & Koordinasi 5 Pejabat" else "Ops: Enterprise Setup & 5 High Officials", color = Color(0xFF94A3B8), fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }

        // 5 High Officials Section (Support multi-role assignment)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isId) "Struktur 5 Pejabat Tinggi (Rangkap Jabatan Didukung)"
                               else "5 High Officials (Multi-Role Assignment Supported)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DarcorpNavy900
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val officialsList = listOf(
                        Triple("Siti Rahmawati, S.E.", listOf("Manajer Keuangan"), "finance@darcorp.id"),
                        Triple("Rudy Prasetyo, S.T.", listOf("Manajer Operasional", "Kepala Gudang"), "ops@darcorp.id"),
                        Triple("Amanda Putri, S.I.Kom.", listOf("Sekretaris Perusahaan", "Manajer Pemasaran"), "secretary@darcorp.id")
                    )

                    officialsList.forEach { (name, roles, email) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(DarcorpNavy800),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = name.take(2), color = DarcorpGold400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DarcorpNavy900)
                                Text(text = email, fontSize = 10.sp, color = DarcorpSlate600)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                roles.forEach { r ->
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = DarcorpNavy700.copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            text = r,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DarcorpNavy700,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                        HorizontalDivider(color = DarcorpSlate50)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = DarcorpSlate600)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DarcorpNavy900, maxLines = 1)
    }
}

// -------------------------------------------------------------
// TAB 2: Absensi Wajah & GPS + Laporan Kerja Harian Wajib
// -------------------------------------------------------------
@Composable
fun AttendanceAndDailyReportTab(
    isId: Boolean,
    repository: DarcorpRepository,
    currentUser: UserProfile,
    attendances: List<AttendanceRecord>
) {
    val context = LocalContext.current
    var hasPhotoCaptured by remember { mutableStateOf(false) }
    var dailyReportText by remember { mutableStateOf("") }
    var isClockedIn by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Attendance Control Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isId) "Presensi Pintar (Wajah & GPS)" else "Smart Attendance (Face & GPS)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DarcorpNavy900
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (currentUser.role == UserRole.PERMANENT_STAFF) DarcorpNavy700.copy(alpha = 0.15f) else DarcorpAmber.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (currentUser.role == UserRole.PERMANENT_STAFF) (if (isId) "Karyawan Tetap" else "Permanent")
                                       else (if (isId) "Karyawan Borongan" else "Piece-Rate"),
                                color = if (currentUser.role == UserRole.PERMANENT_STAFF) DarcorpNavy700 else Color(0xFFB45309),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live GPS Status Bar
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF0FDF4),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarcorpEmerald))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = "GPS", tint = DarcorpEmerald, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isId) "Lokasi Terkunci: SCBD Tower Lot 18, Jakarta" else "Locked Location: SCBD Tower Lot 18, Jakarta",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = DarcorpEmeraldDark
                                )
                                Text(
                                    text = "Lat: -6.225588 • Long: 106.809444 • Presisi: 3.8 meter",
                                    fontSize = 10.sp,
                                    color = DarcorpSlate600
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Simulated Camera Face Viewfinder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarcorpNavy900)
                            .border(1.dp, DarcorpGold500, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasPhotoCaptured) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = DarcorpEmerald, modifier = Modifier.size(44.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isId) "Wajah Terverifikasi AI (Anti-Spoofing Valid)" else "Face Verified by AI (Anti-Spoofing Passed)",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = currentUser.name,
                                    color = DarcorpGold400,
                                    fontSize = 11.sp
                                )
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Default.Face, contentDescription = null, tint = DarcorpGold400, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isId) "Arahkan wajah ke bingkai kamera" else "Align face inside frame",
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Capture Selfie Button
                    OutlinedButton(
                        onClick = {
                            hasPhotoCaptured = true
                            Toast.makeText(context, if (isId) "Foto wajah selfie terambil!" else "Selfie photo captured!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (hasPhotoCaptured) (if (isId) "Ambil Ulang Foto Selfie" else "Retake Selfie")
                                   else (if (isId) "Ambil Foto Wajah Sekarang" else "Take Face Selfie"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Clock In Button
                    Button(
                        onClick = {
                            if (!hasPhotoCaptured) {
                                Toast.makeText(context, if (isId) "Wajib ambil foto selfie terlebih dahulu!" else "Selfie required before clocking in!", Toast.LENGTH_SHORT).show()
                            } else {
                                repository.clockInAttendance(
                                    employeeName = currentUser.name,
                                    isPermanent = currentUser.role == UserRole.PERMANENT_STAFF,
                                    lat = -6.225588,
                                    lng = 106.809444,
                                    locName = "SCBD Office Tower Lot 18"
                                )
                                isClockedIn = true
                                Toast.makeText(context, if (isId) "Absen Masuk Berhasil Tercatat!" else "Clock In Recorded Successfully!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarcorpNavy900),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isClockedIn
                    ) {
                        Icon(imageVector = Icons.Default.Login, contentDescription = null, tint = DarcorpGold400)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isClockedIn) (if (isId) "Sudah Absen Masuk Hari Ini" else "Clocked In For Today")
                                   else (if (isId) "Absen Masuk (Clock In)" else "Clock In"),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DarcorpSlate100)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Mandatory Daily Work Report Before Clock-Out
                    Text(
                        text = if (isId) "Laporan Kerja Harian (Wajib Sebelum Jam Pulang)"
                               else "Daily Work Report (Mandatory Before Clock-Out)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DarcorpNavy900
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = dailyReportText,
                        onValueChange = { dailyReportText = it },
                        placeholder = {
                            Text(
                                if (isId) "Uraikan rincian pekerjaan dan pencapaian hari ini..."
                                else "Detail your tasks and accomplishments today...",
                                fontSize = 11.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Clock Out Button
                    Button(
                        onClick = {
                            if (dailyReportText.isBlank()) {
                                Toast.makeText(context, if (isId) "Peringatan: Laporan kerja harian wajib diisi sebelum absen pulang!" else "Warning: Daily report required before clock out!", Toast.LENGTH_LONG).show()
                            } else {
                                repository.submitDailyReportAndClockOut(dailyReportText)
                                dailyReportText = ""
                                Toast.makeText(context, if (isId) "Absen Pulang & Laporan Kerja Berhasil Disimpan!" else "Clock Out & Report Saved Successfully!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarcorpEmeraldDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.AssignmentTurnedIn, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isId) "Kirim Laporan & Absen Pulang (Clock Out)" else "Submit Report & Clock Out",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Attendance & Report History List
        item {
            Text(
                text = if (isId) "Histori Absensi & Laporan Kerja Seluruh Karyawan" else "Attendance & Daily Report History",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = DarcorpNavy900
            )
        }

        items(attendances) { record ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = record.employeeName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarcorpNavy900)
                            Text(
                                text = if (record.isPermanent) (if (isId) "Karyawan Tetap (Gaji Bulanan)" else "Permanent Staff")
                                       else (if (isId) "Karyawan Borongan (Upah Mingguan)" else "Piece-Rate Staff"),
                                fontSize = 10.sp,
                                color = DarcorpSlate600
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DarcorpEmerald.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = record.clockInTime,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarcorpEmeraldDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "📍 ${record.locationName} (${record.latitude}, ${record.longitude})",
                        fontSize = 10.sp,
                        color = DarcorpSlate600
                    )

                    if (record.dailyReport != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DarcorpSlate50,
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = if (isId) "Laporan Harian:" else "Daily Report:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarcorpNavy900
                                )
                                Text(
                                    text = record.dailyReport,
                                    fontSize = 11.sp,
                                    color = DarcorpSlate700
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 3: Upah & Payroll (Bulanan Tetap & Mingguan Borongan)
// -------------------------------------------------------------
@Composable
fun PayrollModuleTab(
    isId: Boolean,
    currentUser: UserProfile
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = if (isId) "Modul Penggajian & Upah Payroll" else "Payroll & Wage Management Module",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = DarcorpNavy900
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isId) "Skema otomatis: Bulanan untuk Karyawan Tetap, dan Mingguan untuk Karyawan Borongan/Lepas."
                       else "Automated schemas: Monthly for Permanent Staff, Weekly for Piece-Rate/Temporary Staff.",
                fontSize = 11.sp,
                color = DarcorpSlate600
            )
        }

        // Scheme 1: Karyawan Tetap (Monthly)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarcorpNavy900),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = DarcorpGold400)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isId) "Skema Karyawan Tetap" else "Permanent Staff Scheme",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = DarcorpNavy900
                                )
                                Text(
                                    text = if (isId) "Siklus: Pembayaran Bulanan (Setiap Tgl 25)" else "Cycle: Monthly Payment (Every 25th)",
                                    fontSize = 11.sp,
                                    color = DarcorpSlate600
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DarcorpEmerald.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (isId) "Bulan Nov 2026" else "Nov 2026",
                                color = DarcorpEmeraldDark,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    DetailItem(label = if (isId) "Gaji Pokok Standar" else "Base Salary", value = "Rp 8.500.000")
                    DetailItem(label = if (isId) "Tunjangan Transport & Makan" else "Allowances", value = "Rp 1.850.000")
                    DetailItem(label = if (isId) "Insentif Kehadiran Penuh (100%)" else "Attendance Incentive", value = "Rp 750.000")
                    DetailItem(label = if (isId) "Potongan BPJS Ketenagakerjaan" else "BPJS Deduction", value = "- Rp 245.000")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DarcorpSlate100)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isId) "Total Transfer Bersih" else "Net Payout", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarcorpNavy900)
                        Text(text = "Rp 10.855.000", fontWeight = FontWeight.Black, fontSize = 14.sp, color = DarcorpNavy700)
                    }
                }
            }
        }

        // Scheme 2: Karyawan Borongan / Tidak Tetap (Weekly)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarcorpAmber),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Scale, contentDescription = null, tint = DarcorpNavy900)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isId) "Skema Karyawan Borongan / Lepas" else "Piece-Rate / Temporary Scheme",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = DarcorpNavy900
                                )
                                Text(
                                    text = if (isId) "Siklus: Pembayaran Mingguan (Setiap Jumat)" else "Cycle: Weekly Payment (Every Friday)",
                                    fontSize = 11.sp,
                                    color = DarcorpSlate600
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DarcorpAmber.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (isId) "Minggu ke-2 Nov" else "Week 2 Nov",
                                color = Color(0xFFB45309),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    DetailItem(label = if (isId) "Total Tonase Muat / Bongkar" else "Total Loaded Weight", value = "48.5 Ton")
                    DetailItem(label = if (isId) "Tarif Satuan per Ton" else "Rate per Ton", value = "Rp 45.000 / Ton")
                    DetailItem(label = if (isId) "Total Upah Borongan Fisik" else "Physical Wage", value = "Rp 2.182.500")
                    DetailItem(label = if (isId) "Bonus Lembur Akhir Pekan" else "Weekend Bonus", value = "Rp 250.000")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DarcorpSlate100)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isId) "Total Pembayaran Mingguan" else "Total Weekly Payout", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarcorpNavy900)
                        Text(text = "Rp 2.432.500", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFFB45309))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: Obrolan Terisolasi 3-Tier (3-Tier Chat)
// -------------------------------------------------------------
@Composable
fun TieredChatTab(
    isId: Boolean,
    repository: DarcorpRepository,
    currentUser: UserProfile,
    messages: List<ChatMessage>
) {
    var selectedTier by remember { mutableStateOf(ChatTier.TIER_1_EXECUTIVE) }
    var inputText by remember { mutableStateOf("") }

    // RBAC verification for group access:
    val canAccessTier1 = currentUser.role == UserRole.LEGAL_OFFICER ||
                         currentUser.role == UserRole.DIREKTUR_UTAMA ||
                         currentUser.role == UserRole.OFFICIAL

    val canAccessTier2 = canAccessTier1 || currentUser.role == UserRole.PERMANENT_STAFF

    val canAccessTier3 = canAccessTier1 || currentUser.role == UserRole.TEMPORARY_STAFF

    val isCurrentTierAuthorized = when (selectedTier) {
        ChatTier.TIER_1_EXECUTIVE -> canAccessTier1
        ChatTier.TIER_2_PERMANENT -> canAccessTier2
        ChatTier.TIER_3_TEMPORARY -> canAccessTier3
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Channel Selector Header
        Surface(
            color = DarcorpNavy900,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (isId) "3 Ruang Obrolan Terisolasi (Anti-Bocor)" else "3-Tier Isolated Group Chats",
                    color = DarcorpGold400,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ChatTier.values().forEach { tier ->
                        val isSelected = selectedTier == tier
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) DarcorpGold500 else DarcorpNavy800,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTier = tier }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Grup ${tier.id}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (isSelected) DarcorpNavy900 else Color.White
                                )
                                Text(
                                    text = when (tier) {
                                        ChatTier.TIER_1_EXECUTIVE -> if (isId) "Eksekutif" else "Execs"
                                        ChatTier.TIER_2_PERMANENT -> if (isId) "Tetap" else "Staff"
                                        ChatTier.TIER_3_TEMPORARY -> if (isId) "Borongan" else "Field"
                                    },
                                    fontSize = 9.sp,
                                    color = if (isSelected) DarcorpNavy900 else Color(0xFF94A3B8),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tier Description & Members Bar
        Surface(
            color = DarcorpSlate100,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (selectedTier == ChatTier.TIER_1_EXECUTIVE) Icons.Default.Lock else Icons.Default.Group,
                    contentDescription = null,
                    tint = DarcorpNavy900,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isId) selectedTier.descriptionId else selectedTier.descriptionEn,
                    fontSize = 10.sp,
                    color = DarcorpSlate700
                )
            }
        }

        if (!isCurrentTierAuthorized) {
            // Access Denied Screen
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = DarcorpCrimson, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isId) "Akses Ruang Chat Dibatasi" else "Access Restricted",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DarcorpCrimson
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isId) "Akun Anda (${currentUser.name} - ${currentUser.role.labelId}) tidak memiliki otoritas di grup chat ini. Karyawan tetap dan borongan diisolasi terpisah untuk mencegah kebocoran informasi."
                                   else "Your account (${currentUser.name}) is not authorized in this group. Permanent and piece-rate teams are strictly isolated.",
                            fontSize = 11.sp,
                            color = DarcorpSlate600,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // Messages List
            val tierMessages = messages.filter { it.tier == selectedTier }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tierMessages) { msg ->
                    val isMe = msg.senderId == currentUser.id
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isMe) DarcorpNavy900 else Color.White,
                            shadowElevation = 1.dp
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                if (!isMe) {
                                    Text(
                                        text = "${msg.senderName} (${msg.senderRole})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = DarcorpGold500
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                                Text(
                                    text = msg.messageText,
                                    fontSize = 12.sp,
                                    color = if (isMe) Color.White else DarcorpNavy900
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.timestamp,
                                    fontSize = 9.sp,
                                    color = if (isMe) Color(0xFF94A3B8) else DarcorpSlate600,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text(if (isId) "Tulis pesan tim..." else "Type team message...", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                repository.sendChatMessage(selectedTier, inputText)
                                inputText = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = DarcorpNavy900)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = DarcorpGold400)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 5: Vault Dokumen Retensi 30 Hari (Auto-Delete)
// -------------------------------------------------------------
@Composable
fun DocumentVaultTab(
    isId: Boolean,
    repository: DarcorpRepository,
    documents: List<VaultDocument>,
    selectedCompany: Company,
    currentUser: UserProfile
) {
    val context = LocalContext.current
    var showUploadDialog by remember { mutableStateOf(false) }

    if (showUploadDialog) {
        var docTitle by remember { mutableStateOf("") }
        var selectedCat by remember { mutableStateOf(DocumentCategory.PAYMENT_PROOF) }

        AlertDialog(
            onDismissRequest = { showUploadDialog = false },
            title = {
                Text(
                    text = if (isId) "Unggah Dokumen Operasional" else "Upload Operational Document",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = docTitle,
                        onValueChange = { docTitle = it },
                        label = { Text(if (isId) "Nama / Keterangan Dokumen" else "Document Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = if (isId) "Kategori:" else "Category:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    DocumentCategory.values().forEach { cat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCat = cat }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = selectedCat == cat, onClick = { selectedCat = cat })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (isId) cat.labelId else cat.labelEn, fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (docTitle.isNotBlank()) {
                            repository.uploadVaultDocument(docTitle, selectedCat, selectedCompany.name)
                            showUploadDialog = false
                            Toast.makeText(context, if (isId) "Dokumen diunggah ke Vault 30 hari!" else "Document uploaded to 30-day vault!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarcorpNavy900)
                ) {
                    Text(if (isId) "Simpan" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadDialog = false }) {
                    Text(if (isId) "Batal" else "Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Vault Notice Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarcorpNavy900),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isId) "Vault Dokumen Retensi 30 Hari" else "30-Day Auto Retention Vault",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DarcorpGold400
                        )
                        Icon(imageVector = Icons.Default.AutoDelete, contentDescription = null, tint = DarcorpAmber)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isId) "Semua bukti transfer, surat jalan, faktur pembelian, dan arus dana tersimpan tepat selama 30 hari, kemudian otomatis terhapus demi kerahasiaan & efisiensi."
                               else "All payment receipts, invoices, and cash flow files are stored for 30 days, then automatically purged for confidentiality.",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showUploadDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DarcorpGold500),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.UploadFile, contentDescription = null, tint = DarcorpNavy900)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isId) "Unggah Berkas Baru (.JPG)" else "Upload New Document (.JPG)",
                            color = DarcorpNavy900,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        items(documents) { doc ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DarcorpNavy700.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = if (isId) doc.category.labelId else doc.category.labelEn,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarcorpNavy700,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        // Countdown Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (doc.daysRemaining <= 15) DarcorpCrimson.copy(alpha = 0.15f) else DarcorpAmber.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = if (doc.daysRemaining <= 15) DarcorpCrimson else DarcorpAmber, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isId) "Sisa ${doc.daysRemaining} hari" else "${doc.daysRemaining} days left",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (doc.daysRemaining <= 15) DarcorpCrimsonDark else Color(0xFFB45309)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = doc.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarcorpNavy900)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isId) "Diunggah oleh ${doc.uploadedBy} (${doc.uploaderRole}) • ${doc.uploadDate} • ${doc.fileSizeKb} KB"
                               else "Uploaded by ${doc.uploadedBy} • ${doc.uploadDate} • ${doc.fileSizeKb} KB",
                        fontSize = 10.sp,
                        color = DarcorpSlate600
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, if (isId) "Berkas '${doc.title}' berhasil diunduh dalam format JPG!" else "Downloaded '${doc.title}' as JPG!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (isId) "Unduh .JPG" else "Download .JPG", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 6: Mitra Buyer & Supplier (Formulir F1 & F2)
// -------------------------------------------------------------
@Composable
fun PartnerFormsTab(
    isId: Boolean,
    repository: DarcorpRepository,
    f1List: List<FormF1Submission>,
    f2List: List<FormF2Agreement>
) {
    val context = LocalContext.current
    var subTab by remember { mutableStateOf(0) } // 0: Form F1, 1: Form F2, 2: Submissions

    // F1 State
    var partnerType by remember { mutableStateOf("Penyuplai (Supplier)") }
    var entityType by remember { mutableStateOf("Perusahaan") }
    var partnerName by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var itemDesc by remember { mutableStateOf("") }
    var itemSpec by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }
    var weightUnit by remember { mutableStateOf("Ton") }
    var priceText by remember { mutableStateOf("") }

    // F2 State
    var f2Name by remember { mutableStateOf("") }
    var f2Entity by remember { mutableStateOf("") }
    var f2IdNumber by remember { mutableStateOf("") }
    var f2Address by remember { mutableStateOf("") }
    var f2KtpUploaded by remember { mutableStateOf(false) }
    var f2SelfieUploaded by remember { mutableStateOf(false) }
    var f2SignaturePoints by remember { mutableStateOf<List<Offset>>(emptyList()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Google Sign-in Verified Badge for Partners
        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = DarcorpNavy700, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = if (isId) "Akun Google Terhubung" else "Google Account Connected", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarcorpNavy900)
                            Text(text = "denyakhbar@gmail.com", fontSize = 11.sp, color = DarcorpSlate600)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = DarcorpEmerald.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (isId) "Terverifikasi" else "Verified",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarcorpEmeraldDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Subtabs: F1 vs F2 vs Inboxes
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = subTab == 0,
                    onClick = { subTab = 0 },
                    label = { Text(if (isId) "Formulir F1 (Penawaran)" else "Form F1 (Quotation)", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = subTab == 1,
                    onClick = { subTab = 1 },
                    label = { Text(if (isId) "Formulir F2 (MoA Legal)" else "Form F2 (Legal MoA)", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = subTab == 2,
                    onClick = { subTab = 2 },
                    label = { Text(if (isId) "Arsip Masuk" else "Inboxes", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Subtab 0: Formulir F1
        if (subTab == 0) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isId) "Formulir F1: Pengajuan & Penawaran Harga" else "Form F1: Procurement & Price Quotation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DarcorpNavy900
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isId) "Data penawaran akan langsung dikirimkan ke Gmail Direktur Utama & Konsultan Legal."
                                   else "Quotation will be forwarded to CEO & Legal Counsel inboxes.",
                            fontSize = 11.sp,
                            color = DarcorpSlate600
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Partner Type
                        Text(text = if (isId) "Bertindak Sebagai:" else "Acting as:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Penyuplai (Supplier)", "Pembeli (Buyer)").forEach { t ->
                                FilterChip(
                                    selected = partnerType == t,
                                    onClick = { partnerType = t },
                                    label = { Text(t, fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Entity Type
                        Text(text = if (isId) "Atas Nama:" else "On Behalf of:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Perusahaan", "Pribadi").forEach { e ->
                                FilterChip(
                                    selected = entityType == e,
                                    onClick = { entityType = e },
                                    label = { Text(e, fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = partnerName,
                            onValueChange = { partnerName = it },
                            label = { Text(if (isId) "Nama Lengkap Perwakilan" else "Representative Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = companyName,
                            onValueChange = { companyName = it },
                            label = { Text(if (isId) "Nama Perusahaan / Usaha" else "Company / Business Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = itemDesc,
                            onValueChange = { itemDesc = it },
                            label = { Text(if (isId) "Nama / Kategori Komoditas Barang" else "Commodity / Item Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = itemSpec,
                            onValueChange = { itemSpec = it },
                            label = { Text(if (isId) "Rincian Spesifikasi Barang Lengkap" else "Detailed Item Specifications") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Weight & Unit
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = weightText,
                                onValueChange = { weightText = it },
                                label = { Text(if (isId) "Berat Pengiriman" else "Weight per Ship") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            Row(modifier = Modifier.padding(top = 8.dp)) {
                                FilterChip(selected = weightUnit == "Kg", onClick = { weightUnit = "Kg" }, label = { Text("Kg") })
                                Spacer(modifier = Modifier.width(4.dp))
                                FilterChip(selected = weightUnit == "Ton", onClick = { weightUnit = "Ton" }, label = { Text("Ton") })
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text(if (isId) "Penawaran Harga Satuan (Rp per $weightUnit)" else "Unit Price (IDR per $weightUnit)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (partnerName.isNotBlank() && itemDesc.isNotBlank()) {
                                    val sub = FormF1Submission(
                                        id = "F1-" + System.currentTimeMillis().toString().takeLast(6),
                                        partnerType = partnerType,
                                        entityType = entityType,
                                        partnerName = partnerName,
                                        companyName = if (companyName.isBlank()) partnerName else companyName,
                                        contactEmail = if (contactEmail.isBlank()) "denyakhbar@gmail.com" else contactEmail,
                                        phone = "+62 812-3456-7890",
                                        itemDescription = itemDesc,
                                        itemSpecification = itemSpec,
                                        samplePhotosCount = 3,
                                        weightPerShipment = weightText.toDoubleOrNull() ?: 10.0,
                                        weightUnit = weightUnit,
                                        unitPrice = priceText.toDoubleOrNull() ?: 50000.0,
                                        submittedAt = "10 Nov 2026, Hari Ini"
                                    )
                                    repository.submitFormF1(sub)
                                    Toast.makeText(context, if (isId) "Formulir F1 Terkirim ke Dirut & Legal!" else "Form F1 Sent to CEO & Legal!", Toast.LENGTH_LONG).show()
                                    subTab = 2 // Switch to inboxes
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarcorpNavy900),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = DarcorpGold400)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isId) "Kirimkan Formulir F1 ke Perusahaan" else "Submit Form F1 to Enterprise",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Subtab 1: Formulir F2 (MoA Legal dengan Tanda Tangan Digital)
        if (subTab == 1) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isId) "Formulir F2: Kesepakatan Perjanjian Kerjasama (MoA)"
                                   else "Form F2: Digital Cooperation Agreement (MoA)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DarcorpNavy900
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isId) "Wajib lampirkan KTP, swafoto diri, dan bubuhkan tanda tangan digital langsung pada layar."
                                   else "Requires ID card, photo verification, and touchscreen digital signature.",
                            fontSize = 11.sp,
                            color = DarcorpSlate600
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = f2Name,
                            onValueChange = { f2Name = it },
                            label = { Text(if (isId) "Nama Pihak Kedua" else "Second Party Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = f2Entity,
                            onValueChange = { f2Entity = it },
                            label = { Text(if (isId) "Nama Badan Usaha / Mitra" else "Entity / Business Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = f2IdNumber,
                            onValueChange = { f2IdNumber = it },
                            label = { Text(if (isId) "Nomor KTP (NIK) / NIB" else "ID Number (NIK / NIB)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = f2Address,
                            onValueChange = { f2Address = it },
                            label = { Text(if (isId) "Alamat Domisili Hukum" else "Legal Domicile Address") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // KTP & Selfie Upload Buttons
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    f2KtpUploaded = true
                                    Toast.makeText(context, if (isId) "Foto KTP Berhasil Diunggah!" else "ID Card Uploaded!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = if (f2KtpUploaded) Icons.Default.CheckCircle else Icons.Default.Badge, contentDescription = null, tint = if (f2KtpUploaded) DarcorpEmerald else DarcorpNavy900)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = if (f2KtpUploaded) (if (isId) "KTP Valid" else "ID Valid") else (if (isId) "Upload KTP" else "Upload ID"), fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    f2SelfieUploaded = true
                                    Toast.makeText(context, if (isId) "Swafoto Diri Berhasil Diunggah!" else "Selfie Photo Uploaded!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = if (f2SelfieUploaded) Icons.Default.CheckCircle else Icons.Default.CameraAlt, contentDescription = null, tint = if (f2SelfieUploaded) DarcorpEmerald else DarcorpNavy900)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = if (f2SelfieUploaded) (if (isId) "Foto Valid" else "Photo Valid") else (if (isId) "Upload Foto" else "Upload Photo"), fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Touch Signature Pad
                        DigitalSignaturePad(
                            points = f2SignaturePoints,
                            onPointsChange = { f2SignaturePoints = it },
                            isIndonesian = isId
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (f2Name.isNotBlank() && f2SignaturePoints.isNotEmpty()) {
                                    val agreement = FormF2Agreement(
                                        id = "F2-" + System.currentTimeMillis().toString().takeLast(6),
                                        partnerName = f2Name,
                                        partnerType = "Penyuplai / Pembeli",
                                        entityType = if (f2Entity.isBlank()) f2Name else f2Entity,
                                        idNumber = if (f2IdNumber.isBlank()) "3171092837490001" else f2IdNumber,
                                        legalAddress = if (f2Address.isBlank()) "Jakarta" else f2Address,
                                        hasKtpUploaded = f2KtpUploaded,
                                        hasSelfieUploaded = f2SelfieUploaded,
                                        signaturePoints = f2SignaturePoints,
                                        signedAt = "10 Nov 2026",
                                        agreementDocumentNumber = "DARCORP/LEGAL/MOA/2026-${System.currentTimeMillis().toString().takeLast(3)}"
                                    )
                                    repository.submitFormF2(agreement)
                                    Toast.makeText(context, if (isId) "Perjanjian Kerjasama (MoA) Resmi Disahkan!" else "Legal Agreement (MoA) Signed & Ratified!", Toast.LENGTH_LONG).show()
                                    subTab = 2
                                } else {
                                    Toast.makeText(context, if (isId) "Nama dan tanda tangan digital wajib dilengkapi!" else "Name and digital signature are required!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarcorpEmeraldDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.DoneAll, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isId) "Sahkan & Terbitkan Perjanjian Kerjasama (F2)" else "Ratify & Issue Cooperation Agreement (F2)",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Subtab 2: Submissions & Agreement Inboxes
        if (subTab == 2) {
            item {
                Text(
                    text = if (isId) "Berkas Penawaran F1 & Perjanjian F2 Diterima" else "Received F1 Quotations & F2 Agreements",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DarcorpNavy900
                )
            }

            items(f1List) { f1 ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Formulir F1 • ${f1.id}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarcorpNavy900)
                            Surface(shape = RoundedCornerShape(4.dp), color = DarcorpNavy800) {
                                Text(text = f1.partnerType, color = DarcorpGold400, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "${f1.companyName} (${f1.partnerName})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text(text = "Komoditas: ${f1.itemDescription} (${f1.weightPerShipment} ${f1.weightUnit})", fontSize = 11.sp, color = DarcorpSlate700)
                        Text(text = "Harga: Rp ${f1.unitPrice.toLong()} per ${f1.weightUnit}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarcorpEmeraldDark)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isId) "✓ Notifikasi diteruskan ke Gmail Direktur Utama & Legal"
                                   else "✓ Relayed to CEO & Legal Counsel inboxes",
                            fontSize = 9.sp,
                            color = DarcorpSlate600
                        )
                    }
                }
            }

            items(f2List) { f2 ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Perjanjian MoA F2 • ${f2.agreementDocumentNumber}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarcorpNavy900)
                            Surface(shape = RoundedCornerShape(4.dp), color = DarcorpEmerald.copy(alpha = 0.2f)) {
                                Text(text = "Resmi Sah", color = DarcorpEmeraldDark, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Pihak: ${f2.partnerName} (${f2.entityType})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text(text = "Identitas: ${f2.idNumber}", fontSize = 11.sp, color = DarcorpSlate700)
                        Text(text = "Status Verifikasi: KTP [✓] | Selfie [✓] | Tanda Tangan Digital [✓]", fontSize = 10.sp, color = DarcorpEmeraldDark)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 7: Super Console Konsultan Hukum (Legal Super Controller)
// -------------------------------------------------------------
@Composable
fun LegalSuperConsoleTab(
    isId: Boolean,
    repository: DarcorpRepository,
    companies: List<Company>
) {
    val context = LocalContext.current
    var companyToDelete by remember { mutableStateOf<Company?>(null) }

    if (companyToDelete != null) {
        val target = companyToDelete!!
        AlertDialog(
            onDismissRequest = { companyToDelete = null },
            title = {
                Text(
                    text = if (isId) "Konfirmasi Penghapusan Mutlak (Legal Root)" else "Confirm Hard Delete (Legal Root)",
                    fontWeight = FontWeight.Bold,
                    color = DarcorpCrimson
                )
            },
            text = {
                Text(
                    text = if (isId) "Sebagai Konsultan Hukum pemegang kuasa tertinggi, apakah Anda yakin ingin menghapus permanen entitas '${target.name}' beserta seluruh data akun dan transaksinya? Tindakan ini tidak dapat dibatalkan oleh Direktur Utama sekalipun."
                           else "As Corporate Legal Counsel with supreme authority, do you confirm permanent deletion of '${target.name}'? This cannot be undone.",
                    fontSize = 12.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.deleteCompanyAsLegal(target.id)
                        companyToDelete = null
                        Toast.makeText(context, if (isId) "Entitas berhasil dihapus permanen oleh Legal!" else "Company purged by Legal Counsel!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarcorpCrimson)
                ) {
                    Text(if (isId) "Hapus Permanen" else "Hard Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { companyToDelete = null }) {
                    Text(if (isId) "Batal" else "Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF450A0A)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isId) "Konsol Kuasa Mutlak Konsultan Hukum" else "Legal Counsel Supreme Authority Console",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFFCA5A5)
                        )
                        Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = Color(0xFFFCA5A5))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isId) "Hanya akun Konsultan Hukum / Legal yang berhak menghapus data perusahaan, member, dan membatalkan status entitas bisnis."
                               else "Only the Corporate Legal Counsel holds the exclusive power to hard-delete company data and manage member expulsion.",
                        fontSize = 11.sp,
                        color = Color(0xFFFECACA),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        item {
            Text(
                text = if (isId) "Daftar Entitas Berisiko & Otoritas Hapus" else "Registered Entities & Purge Authority",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = DarcorpNavy900
            )
        }

        items(companies) { comp ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = comp.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarcorpNavy900)
                        Text(text = "NIB: ${comp.nib} • NPWP: ${comp.npwp}", fontSize = 10.sp, color = DarcorpSlate600)
                    }

                    Button(
                        onClick = { companyToDelete = comp },
                        colors = ButtonDefaults.buttonColors(containerColor = DarcorpCrimson),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isId) "Hapus Data" else "Purge Data", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
