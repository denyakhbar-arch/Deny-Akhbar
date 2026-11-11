package com.example.data

import androidx.compose.ui.geometry.Offset
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

enum class AppViewMode {
    LANDING_PAGE,
    LOGIN_SCREEN,
    APP_PORTAL
}

enum class PortalTab {
    DASHBOARD_COMPANIES,
    ATTENDANCE_REPORTS,
    PAYROLL_MODULE,
    TIERED_CHAT,
    DOCUMENT_VAULT,
    PARTNER_FORMS,
    LEGAL_SUPER_CONSOLE
}

class DarcorpRepository {

    private val _language = MutableStateFlow(AppLanguage.INDONESIAN)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _viewMode = MutableStateFlow(AppViewMode.LANDING_PAGE)
    val viewMode: StateFlow<AppViewMode> = _viewMode.asStateFlow()

    private val _activeTab = MutableStateFlow(PortalTab.DASHBOARD_COMPANIES)
    val activeTab: StateFlow<PortalTab> = _activeTab.asStateFlow()

    private val _downloadModalVisible = MutableStateFlow(false)
    val downloadModalVisible: StateFlow<Boolean> = _downloadModalVisible.asStateFlow()

    // Pre-seeded Users
    val demoUsers = listOf(
        UserProfile("usr_legal", "Adv. Hendra Kusuma, S.H., M.H.", "legal@darcorp.id", UserRole.LEGAL_OFFICER, "comp_1"),
        UserProfile("usr_dirut", "Bambang Wijaya, B.Eng., M.M.", "dirut@darcorp.id", UserRole.DIREKTUR_UTAMA, "comp_1"),
        UserProfile("usr_fin_mgr", "Siti Rahmawati, S.E., Ak.", "finance@darcorp.id", UserRole.OFFICIAL, "comp_1", listOf(OfficialTitle.MANAJER_KEUANGAN)),
        UserProfile("usr_ops_mgr", "Rudy Prasetyo, S.T.", "ops@darcorp.id", UserRole.OFFICIAL, "comp_1", listOf(OfficialTitle.MANAJER_OPERASIONAL, OfficialTitle.KEPALA_GUDANG)),
        UserProfile("usr_sec_mkt", "Amanda Putri, S.I.Kom.", "secretary@darcorp.id", UserRole.OFFICIAL, "comp_1", listOf(OfficialTitle.SEKRETARIS, OfficialTitle.MANAJER_PEMASARAN)),
        UserProfile("usr_perm_1", "Dimas Adi Pratama", "dimas.adi@darcorp.id", UserRole.PERMANENT_STAFF, "comp_1"),
        UserProfile("usr_perm_2", "Ratna Dewi Lestari", "ratna.dewi@darcorp.id", UserRole.PERMANENT_STAFF, "comp_1"),
        UserProfile("usr_temp_1", "Joko Santoso (Borongan Gudang)", "joko.field@darcorp.id", UserRole.TEMPORARY_STAFF, "comp_1"),
        UserProfile("usr_temp_2", "Ahmad Fauzi (Borongan Angkut)", "fauzi.field@darcorp.id", UserRole.TEMPORARY_STAFF, "comp_1"),
        UserProfile("usr_partner_buyer", "Ir. David Tan (PT Nusantara Maju)", "david.tan@nusantaramaju.co.id", UserRole.PARTNER_BUYER, "comp_1"),
        UserProfile("usr_partner_supp", "H. Ridwan Kamil (Supplier Agro Mandiri)", "ridwan@agromandiri.com", UserRole.PARTNER_SUPPLIER, "comp_1")
    )

    private val _currentUser = MutableStateFlow(demoUsers[1]) // Default to Direktur Utama
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Companies
    private val _companies = MutableStateFlow(
        listOf(
            Company(
                id = "comp_1",
                name = "PT DARCORP NUSANTARA",
                entityType = EntityType.PT,
                sector = BusinessSector.TRADING,
                nib = "0120239481928",
                npwp = "01.345.678.9-012.000",
                address = "Sudirman Central Business District (SCBD) Tower 18, Jakarta Selatan",
                legalDate = "14 Januari 2021"
            ),
            Company(
                id = "comp_2",
                name = "CV DARCORP LOGISTIK EKSPRES",
                entityType = EntityType.CV,
                sector = BusinessSector.LOGISTICS,
                nib = "9182736451234",
                npwp = "02.987.654.3-456.000",
                address = "Kawasan Industri MM2100 Blok C-4, Cikarang Barat, Bekasi",
                legalDate = "22 Mei 2022"
            ),
            Company(
                id = "comp_3",
                name = "YAYASAN DARCORP KARYA PEDULI",
                entityType = EntityType.YAYASAN,
                sector = BusinessSector.SERVICES,
                nib = "8877665544332",
                npwp = "03.112.233.4-789.000",
                address = "Jl. Diponegoro No. 45, Menteng, Jakarta Pusat",
                legalDate = "08 Agustus 2023"
            )
        )
    )
    val companies: StateFlow<List<Company>> = _companies.asStateFlow()

    // Attendances
    private val _attendances = MutableStateFlow(
        listOf(
            AttendanceRecord(
                id = "att_1",
                employeeId = "usr_perm_1",
                employeeName = "Dimas Adi Pratama",
                isPermanent = true,
                clockInTime = "07:54 WIB",
                clockOutTime = null,
                latitude = -6.225588,
                longitude = 106.809444,
                locationName = "SCBD Office Tower Lot 18",
                dailyReport = "Mempersiapkan dokumen audit keuangan Q4 & sinkronisasi data pajak bulanan.",
                status = "Terverifikasi Wajah & GPS"
            ),
            AttendanceRecord(
                id = "att_2",
                employeeId = "usr_perm_2",
                employeeName = "Ratna Dewi Lestari",
                isPermanent = true,
                clockInTime = "08:02 WIB",
                clockOutTime = "17:15 WIB",
                latitude = -6.225610,
                longitude = 106.809512,
                locationName = "SCBD Office Tower Lot 18",
                dailyReport = "Rekapitulasi berkas legalitas 3 mitra supplier baru dan pengesahan surat perjanjian.",
                status = "Lengkap (Clock-Out Sukses)"
            ),
            AttendanceRecord(
                id = "att_3",
                employeeId = "usr_temp_1",
                employeeName = "Joko Santoso (Borongan)",
                isPermanent = false,
                clockInTime = "07:30 WIB",
                clockOutTime = null,
                latitude = -6.294112,
                longitude = 107.082215,
                locationName = "Gudang Logistik Cikarang Hub",
                dailyReport = "Bongkar muat 2 kontainer komoditas berat 32 Ton, proses grading fisik selesai.",
                status = "Terverifikasi Lapangan"
            )
        )
    )
    val attendances: StateFlow<List<AttendanceRecord>> = _attendances.asStateFlow()

    // 3-Tier Chat Messages
    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage("c1", ChatTier.TIER_1_EXECUTIVE, "usr_legal", "Adv. Hendra Kusuma", "Legal Officer", "Rancangan kontrak kerjasama PT Mitra Agro telah diaudit. Klausul arbitrase sudah sesuai hukum.", "08:15", false),
            ChatMessage("c2", ChatTier.TIER_1_EXECUTIVE, "usr_dirut", "Bambang Wijaya", "Direktur Utama", "Terima kasih Pak Hendra. Manajer Keuangan harap segera siapkan pencairan termin pertama.", "08:22", false),
            ChatMessage("c3", ChatTier.TIER_1_EXECUTIVE, "usr_fin_mgr", "Siti Rahmawati", "Manajer Keuangan", "Siap Pak Dirut, invoice dan bukti transfer sudah kami unggah ke Vault 30 hari.", "08:30", false),

            ChatMessage("c4", ChatTier.TIER_2_PERMANENT, "usr_dirut", "Bambang Wijaya", "Direktur Utama", "Selamat pagi seluruh tim kantor pusat. Jangan lupa isi laporan harian sebelum pulang.", "08:00", false),
            ChatMessage("c5", ChatTier.TIER_2_PERMANENT, "usr_perm_1", "Dimas Adi Pratama", "Staff Keuangan", "Baik Pak Dirut, rekap payroll bulanan sedang diproses.", "08:45", false),

            ChatMessage("c6", ChatTier.TIER_3_TEMPORARY, "usr_ops_mgr", "Rudy Prasetyo", "Manajer Operasional", "Perhatian tim borongan gudang: target pengiriman 50 Ton komoditas hari ini harus selesai sebelum 16:00.", "07:15", true),
            ChatMessage("c7", ChatTier.TIER_3_TEMPORARY, "usr_temp_1", "Joko Santoso", "Staff Borongan", "Siap Pak Rudy, 2 armada truk kontainer sudah mulai proses muat.", "07:40", false)
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // 30-Day Retention Vault
    private val _documents = MutableStateFlow(
        listOf(
            VaultDocument("doc_1", "Bukti Transfer Termin-1 PT Agro Sejahtera.jpg", DocumentCategory.PAYMENT_PROOF, "PT DARCORP NUSANTARA", "Siti Rahmawati", "Manajer Keuangan", "10 Nov 2026", 29, 640),
            VaultDocument("doc_2", "Surat Jalan Muatan 35 Ton Kontainer A-12.jpg", DocumentCategory.DELIVERY_NOTE, "PT DARCORP NUSANTARA", "Rudy Prasetyo", "Kepala Gudang", "08 Nov 2026", 27, 480),
            VaultDocument("doc_3", "Invoice Pembelian Sparepart Crane Heavy.jpg", DocumentCategory.PURCHASE_RECEIPT, "CV DARCORP LOGISTIK", "Rudy Prasetyo", "Manajer Operasional", "05 Nov 2026", 24, 720),
            VaultDocument("doc_4", "Rekening Koran Arus Dana Masuk Nov 2026.jpg", DocumentCategory.INCOMING_FUNDS, "PT DARCORP NUSANTARA", "Siti Rahmawati", "Manajer Keuangan", "02 Nov 2026", 21, 910),
            VaultDocument("doc_5", "Kwitansi Pembayaran Upah Borongan Gudang.jpg", DocumentCategory.OUTGOING_FUNDS, "PT DARCORP NUSANTARA", "Bambang Wijaya", "Direktur Utama", "25 Okt 2026", 14, 530)
        )
    )
    val documents: StateFlow<List<VaultDocument>> = _documents.asStateFlow()

    // Form F1 Submissions
    private val _f1Submissions = MutableStateFlow(
        listOf(
            FormF1Submission(
                id = "F1-2026-001",
                partnerType = "Penyuplai (Supplier)",
                entityType = "Perusahaan",
                partnerName = "H. Ridwan Kamil",
                companyName = "PT Agro Mandiri Sejahtera",
                contactEmail = "ridwan@agromandiri.com",
                phone = "+62 812-9988-7766",
                itemDescription = "Biji Kopi Arabika Specialty Grade A",
                itemSpecification = "Moisture 11%, Defect < 2%, Screen Size 16-18, Single Origin Gayo",
                samplePhotosCount = 4,
                weightPerShipment = 15.0,
                weightUnit = "Ton",
                unitPrice = 115000.0,
                submittedAt = "10 Nov 2026, 09:30 WIB"
            )
        )
    )
    val f1Submissions: StateFlow<List<FormF1Submission>> = _f1Submissions.asStateFlow()

    // Form F2 Agreements
    private val _f2Agreements = MutableStateFlow(
        listOf(
            FormF2Agreement(
                id = "F2-AGR-8801",
                partnerName = "Ir. David Tan",
                partnerType = "Pembeli (Buyer)",
                entityType = "Perusahaan (PT Nusantara Maju)",
                idNumber = "3171092837490001 / NIB: 0928374615243",
                legalAddress = "Wisma Nusantara Lt. 12, Jl. MH Thamrin, Jakarta",
                hasKtpUploaded = true,
                hasSelfieUploaded = true,
                signaturePoints = listOf(Offset(10f, 10f), Offset(50f, 40f), Offset(100f, 80f)),
                signedAt = "09 Nov 2026, 14:20 WIB",
                agreementDocumentNumber = "DARCORP/LEGAL/MOA/XI/2026-042"
            )
        )
    )
    val f2Agreements: StateFlow<List<FormF2Agreement>> = _f2Agreements.asStateFlow()

    // Actions
    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.INDONESIAN) AppLanguage.ENGLISH else AppLanguage.INDONESIAN
    }

    fun setViewMode(mode: AppViewMode) {
        _viewMode.value = mode
    }

    fun setActiveTab(tab: PortalTab) {
        _activeTab.value = tab
    }

    fun setDownloadModalVisible(visible: Boolean) {
        _downloadModalVisible.value = visible
    }

    fun switchUserRole(user: UserProfile) {
        _currentUser.value = user
    }

    fun addCompany(company: Company) {
        _companies.value = _companies.value + company
    }

    fun deleteCompanyAsLegal(companyId: String): Boolean {
        // Only Legal Officer can delete company
        if (_currentUser.value.role == UserRole.LEGAL_OFFICER) {
            _companies.value = _companies.value.filter { it.id != companyId }
            return true
        }
        return false
    }

    fun clockInAttendance(employeeName: String, isPermanent: Boolean, lat: Double, lng: Double, locName: String) {
        val sdf = SimpleDateFormat("HH:mm 'WIB'", Locale.getDefault())
        val timeNow = sdf.format(Date())
        val newRecord = AttendanceRecord(
            id = "att_" + System.currentTimeMillis(),
            employeeId = _currentUser.value.id,
            employeeName = employeeName,
            isPermanent = isPermanent,
            clockInTime = timeNow,
            latitude = lat,
            longitude = lng,
            locationName = locName,
            dailyReport = null,
            status = "Terverifikasi Foto Selfie & GPS"
        )
        _attendances.value = listOf(newRecord) + _attendances.value
    }

    fun submitDailyReportAndClockOut(reportText: String) {
        val sdf = SimpleDateFormat("HH:mm 'WIB'", Locale.getDefault())
        val timeNow = sdf.format(Date())
        val userId = _currentUser.value.id
        _attendances.value = _attendances.value.map { record ->
            if (record.employeeId == userId && record.clockOutTime == null) {
                record.copy(
                    clockOutTime = timeNow,
                    dailyReport = reportText,
                    status = "Lengkap (Laporan Diterima)"
                )
            } else {
                record
            }
        }
    }

    fun sendChatMessage(tier: ChatTier, text: String, urgent: Boolean = false) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val user = _currentUser.value
        val msg = ChatMessage(
            id = "c_" + System.currentTimeMillis(),
            tier = tier,
            senderId = user.id,
            senderName = user.name,
            senderRole = user.role.labelId,
            messageText = text,
            timestamp = sdf.format(Date()),
            isUrgent = urgent
        )
        _chatMessages.value = _chatMessages.value + msg
    }

    fun uploadVaultDocument(title: String, category: DocumentCategory, companyName: String) {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val doc = VaultDocument(
            id = "doc_" + System.currentTimeMillis(),
            title = title,
            category = category,
            companyName = companyName,
            uploadedBy = _currentUser.value.name,
            uploaderRole = _currentUser.value.role.labelId,
            uploadDate = sdf.format(Date()),
            daysRemaining = 30,
            fileSizeKb = 512
        )
        _documents.value = listOf(doc) + _documents.value
    }

    fun submitFormF1(submission: FormF1Submission) {
        _f1Submissions.value = listOf(submission) + _f1Submissions.value
    }

    fun submitFormF2(agreement: FormF2Agreement) {
        _f2Agreements.value = listOf(agreement) + _f2Agreements.value
    }
}
