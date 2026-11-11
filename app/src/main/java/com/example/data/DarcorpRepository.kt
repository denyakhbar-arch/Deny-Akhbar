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
    PAID_SERVICES,
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

    // Biometric Face Profiles for Biometric Attendance Authentication
    private val _biometricProfiles = MutableStateFlow<Map<String, BiometricFaceProfile>>(
        mapOf(
            "usr_legal" to BiometricFaceProfile(
                userId = "usr_legal",
                userName = "Adv. Hendra Kusuma, S.H., M.H.",
                isRegistered = true,
                registrationDate = "01 Nov 2026, 09:15 WIB",
                confidenceScore = 99.8f,
                biometricHash = "DAR-BIO-7A9F32BC14E0",
                antiSpoofingPassed = true,
                livenessScore = 0.99f
            ),
            "usr_dirut" to BiometricFaceProfile(
                userId = "usr_dirut",
                userName = "Bambang Wijaya, B.Eng., M.M.",
                isRegistered = true,
                registrationDate = "01 Nov 2026, 08:30 WIB",
                confidenceScore = 99.4f,
                biometricHash = "DAR-BIO-81EC20D99FA1",
                antiSpoofingPassed = true,
                livenessScore = 0.98f
            ),
            "usr_perm_1" to BiometricFaceProfile(
                userId = "usr_perm_1",
                userName = "Dimas Adi Pratama",
                isRegistered = true,
                registrationDate = "03 Nov 2026, 07:45 WIB",
                confidenceScore = 98.9f,
                biometricHash = "DAR-BIO-34DF56AB8901",
                antiSpoofingPassed = true,
                livenessScore = 0.97f
            )
        )
    )
    val biometricProfiles: StateFlow<Map<String, BiometricFaceProfile>> = _biometricProfiles.asStateFlow()

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

    // Form F1 Submissions with Dynamic Compliance Statuses
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
                submittedAt = "10 Nov 2026, 09:30 WIB",
                complianceStatus = PartnerComplianceStatus.ACTIVE
            ),
            FormF1Submission(
                id = "F1-2026-002",
                partnerType = "Pembeli (Buyer)",
                entityType = "Perusahaan",
                partnerName = "Budi Hartono",
                companyName = "CV Sumber Rejeki Abadi",
                contactEmail = "procurement@sumberrejeki.co.id",
                phone = "+62 813-1122-3344",
                itemDescription = "Jagung Pipil Kering Pakan Ternak",
                itemSpecification = "Kadar air maks 14%, butir rusak < 5%, bebas jamur & aflatoksin",
                samplePhotosCount = 2,
                weightPerShipment = 50.0,
                weightUnit = "Ton",
                unitPrice = 5200.0,
                submittedAt = "10 Nov 2026, 11:15 WIB",
                complianceStatus = PartnerComplianceStatus.VERIFIED
            ),
            FormF1Submission(
                id = "F1-2026-003",
                partnerType = "Penyuplai (Supplier)",
                entityType = "Pribadi",
                partnerName = "Drs. Hendro Wibowo",
                companyName = "Peternakan Unggas Mandiri",
                contactEmail = "hendro.wibowo@gmail.com",
                phone = "+62 856-7788-9900",
                itemDescription = "Telur Ayam Ras Segar Grade AA",
                itemSpecification = "Ukuran besar 60-65 gr/butir, cangkang tebal higienis, lolos uji salmonella",
                samplePhotosCount = 3,
                weightPerShipment = 8.0,
                weightUnit = "Ton",
                unitPrice = 28500.0,
                submittedAt = "10 Nov 2026, 13:45 WIB",
                complianceStatus = PartnerComplianceStatus.PENDING
            ),
            FormF1Submission(
                id = "F1-2026-004",
                partnerType = "Penyuplai (Supplier)",
                entityType = "Perusahaan",
                partnerName = "Dr. Michael Chen",
                companyName = "PT Nusantara Herbal Biotech",
                contactEmail = "m.chen@nusantara-herbal.com",
                phone = "+62 811-9988-1122",
                itemDescription = "Ekstrak Jahe Merah & Temulawak Konsentrat",
                itemSpecification = "Standar BPOM, sertifikasi organik internasional, analisis COA batch 09",
                samplePhotosCount = 5,
                weightPerShipment = 2.5,
                weightUnit = "Ton",
                unitPrice = 450000.0,
                submittedAt = "09 Nov 2026, 16:30 WIB",
                complianceStatus = PartnerComplianceStatus.UNDER_REVIEW
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
                agreementDocumentNumber = "DARCORP/LEGAL/MOA/XI/2026-042",
                complianceStatus = PartnerComplianceStatus.ACTIVE
            ),
            FormF2Agreement(
                id = "F2-AGR-8802",
                partnerName = "H. Ridwan Kamil",
                partnerType = "Penyuplai (Supplier)",
                entityType = "Perusahaan (PT Agro Mandiri Sejahtera)",
                idNumber = "3273018274650002 / NIB: 8129384756201",
                legalAddress = "Kawasan Industri Gede Bage Blok D-4, Bandung",
                hasKtpUploaded = true,
                hasSelfieUploaded = true,
                signaturePoints = listOf(Offset(20f, 30f), Offset(80f, 60f), Offset(120f, 90f)),
                signedAt = "10 Nov 2026, 10:00 WIB",
                agreementDocumentNumber = "DARCORP/LEGAL/MOA/XI/2026-043",
                complianceStatus = PartnerComplianceStatus.VERIFIED
            )
        )
    )
    val f2Agreements: StateFlow<List<FormF2Agreement>> = _f2Agreements.asStateFlow()

    // -------------------------------------------------------------
    // CHANNELS PEMBAYARAN LANGSUNG KE REKENING DARCORP (VA & E-WALLET)
    // -------------------------------------------------------------
    // SALURAN PEMBAYARAN LANGSUNG KORPORASI (VA & E-WALLET)
    // -------------------------------------------------------------
    val paymentChannels: List<PaymentChannel> = listOf(
        PaymentChannel(
            code = "VA_SEABANK",
            name = "SeaBank Rekening Penerima",
            type = PaymentMethodType.VIRTUAL_ACCOUNT,
            accountNumber = "901118227810",
            accountHolder = "PT DAR CORPORATE INDONESIA",
            fee = 0,
            formattedFee = "Bebas Biaya Transfer",
            instructionsId = listOf(
                "Buka aplikasi SeaBank atau m-Banking bank manapun (BCA, Mandiri, BRI, BNI).",
                "Pilih menu 'Transfer' > masukkan Bank Tujuan: SeaBank (Kode Bank: 535) atau nomor rekening: 901118227810.",
                "Periksa konfirmasi atas nama 'PT DAR CORPORATE INDONESIA' dan selesaikan transaksi."
            ),
            instructionsEn = listOf(
                "Open SeaBank app or any mobile banking app.",
                "Select 'Transfer' > Destination: SeaBank (Code: 535), Account Number: 901118227810.",
                "Verify beneficiary 'PT DAR CORPORATE INDONESIA' and confirm."
            ),
            badgeColor = 0xFFFF5722
        ),
        PaymentChannel(
            code = "VA_BCA",
            name = "BCA Virtual Account",
            type = PaymentMethodType.VIRTUAL_ACCOUNT,
            accountNumber = "8277 0812 3456 7890",
            accountHolder = "PT DAR CORPORATE INDONESIA",
            fee = 0,
            formattedFee = "Bebas Biaya Transfer",
            instructionsId = listOf(
                "Buka m-BCA / myBCA / ATM BCA, pilih menu 'Transfer' > 'BCA Virtual Account'.",
                "Masukkan nomor Virtual Account resmi DARcorporate: 8277 0812 3456 7890.",
                "Periksa konfirmasi atas nama 'PT DAR CORPORATE INDONESIA' dan selesaikan transaksi."
            ),
            instructionsEn = listOf(
                "Open BCA mobile / myBCA / ATM BCA, select 'Transfer' > 'BCA Virtual Account'.",
                "Enter Virtual Account number: 8277 0812 3456 7890.",
                "Verify beneficiary name 'PT DAR CORPORATE INDONESIA' and confirm."
            ),
            badgeColor = 0xFF005DAA
        ),
        PaymentChannel(
            code = "VA_MANDIRI",
            name = "Mandiri Virtual Account",
            type = PaymentMethodType.VIRTUAL_ACCOUNT,
            accountNumber = "8890 8123 4567 8901",
            accountHolder = "DAR CORPORATE LEGAL",
            fee = 0,
            formattedFee = "Bebas Biaya Transfer",
            instructionsId = listOf(
                "Buka aplikasi Livin' by Mandiri, pilih menu 'Bayar' > 'Multi Payment'.",
                "Pilih penyedia jasa 'DAR Corporate' atau kode perusahaan '88908'.",
                "Masukkan nomor VA: 8890 8123 4567 8901 dan konfirmasi pembayaran."
            ),
            instructionsEn = listOf(
                "Open Livin' by Mandiri, select 'Pay' > 'Multi Payment'.",
                "Select biller 'DAR Corporate' or company code '88908'.",
                "Enter VA number: 8890 8123 4567 8901 and confirm."
            ),
            badgeColor = 0xFF003876
        ),
        PaymentChannel(
            code = "VA_BRI",
            name = "BRI Virtual Account (BRIVA)",
            type = PaymentMethodType.VIRTUAL_ACCOUNT,
            accountNumber = "1280 0812 3456 7890",
            accountHolder = "PT DAR CORPORATE",
            fee = 0,
            formattedFee = "Bebas Biaya Transfer",
            instructionsId = listOf(
                "Buka aplikasi BRImo / ATM BRI, pilih menu 'Pembayaran' > 'BRIVA'.",
                "Masukkan nomor BRIVA: 1280 0812 3456 7890.",
                "Verifikasi jumlah tagihan dan nama penerima 'PT DAR CORPORATE'."
            ),
            instructionsEn = listOf(
                "Open BRImo / ATM BRI, select 'Payment' > 'BRIVA'.",
                "Enter BRIVA number: 1280 0812 3456 7890.",
                "Verify invoice amount and confirm."
            ),
            badgeColor = 0xFF00529C
        ),
        PaymentChannel(
            code = "VA_BNI",
            name = "BNI Virtual Account",
            type = PaymentMethodType.VIRTUAL_ACCOUNT,
            accountNumber = "9881 2345 6789 0123",
            accountHolder = "DAR CORPORATE SERVICES",
            fee = 0,
            formattedFee = "Bebas Biaya Transfer",
            instructionsId = listOf(
                "Buka BNI Mobile Banking, pilih menu 'Pembayaran' > 'Virtual Account Billing'.",
                "Masukkan nomor VA: 9881 2345 6789 0123.",
                "Lanjutkan proses dan simpan bukti konfirmasi transfer."
            ),
            instructionsEn = listOf(
                "Open BNI Mobile Banking, select 'Payment' > 'Virtual Account Billing'.",
                "Input VA number: 9881 2345 6789 0123.",
                "Complete authorization and save confirmation."
            ),
            badgeColor = 0xFFF15A24
        ),
        PaymentChannel(
            code = "QRIS",
            name = "QRIS Instant (Semua Bank & Dompet Digital)",
            type = PaymentMethodType.QRIS,
            accountNumber = "NMID: ID1020268899001",
            accountHolder = "DAR CORPORATE OFFICIAL",
            fee = 0,
            formattedFee = "Instan 24 Jam Bebas Biaya",
            instructionsId = listOf(
                "Buka aplikasi mobile banking (BCA, Mandiri, BRI, BNI) atau e-wallet (GoPay, OVO, DANA, ShopeePay).",
                "Pindai barcode QRIS resmi DARcorporate yang tertera pada layar aplikasi.",
                "Periksa nominal dan nama merchant penerima 'DAR CORPORATE OFFICIAL'."
            ),
            instructionsEn = listOf(
                "Open any mobile banking or e-wallet app with QRIS scanner.",
                "Scan the DARcorporate dynamic QR code displayed on screen.",
                "Confirm merchant name 'DAR CORPORATE OFFICIAL' and pay."
            ),
            badgeColor = 0xFFE11D48
        ),
        PaymentChannel(
            code = "EWAL_DANA",
            name = "DANA",
            type = PaymentMethodType.E_WALLET,
            accountNumber = "0895345657270",
            accountHolder = "DAR Corporate Services / Official",
            fee = 0,
            formattedFee = "Bebas Biaya",
            instructionsId = listOf(
                "Buka aplikasi DANA di ponsel Anda, pilih menu 'Kirim' / 'Send'.",
                "Ketik nomor akun DANA resmi DARcorporate: 0895345657270.",
                "Pastikan nominal sesuai tagihan layanan dan selesaikan transaksi."
            ),
            instructionsEn = listOf(
                "Open DANA app, tap 'Send'.",
                "Enter official DANA business number: 0895345657270.",
                "Verify recipient and confirm transfer."
            ),
            badgeColor = 0xFF118EEA
        ),
        PaymentChannel(
            code = "EWAL_OVO",
            name = "OVO",
            type = PaymentMethodType.E_WALLET,
            accountNumber = "0895345657270",
            accountHolder = "PT DAR CORPORATE",
            fee = 0,
            formattedFee = "Bebas Biaya",
            instructionsId = listOf(
                "Buka aplikasi OVO, pilih menu 'Transfer' > 'Sesama OVO'.",
                "Ketik nomor akun OVO resmi: 0895345657270.",
                "Periksa nama penerima dan masukkan PIN transaksi."
            ),
            instructionsEn = listOf(
                "Open OVO app, choose 'Transfer' > 'To OVO'.",
                "Enter official OVO account: 0895345657270.",
                "Confirm recipient and pay."
            ),
            badgeColor = 0xFF4C3494
        ),
        PaymentChannel(
            code = "EWAL_GOPAY",
            name = "GoPay",
            type = PaymentMethodType.E_WALLET,
            accountNumber = "087765483134",
            accountHolder = "DAR Corporate Services",
            fee = 0,
            formattedFee = "Bebas Biaya",
            instructionsId = listOf(
                "Buka aplikasi GoPay atau Gojek, pilih 'Kirim / Transfer'.",
                "Ketik nomor akun GoPay resmi DARcorporate: 087765483134.",
                "Verifikasi nama akun 'DAR Corporate Services' dan selesaikan pembayaran."
            ),
            instructionsEn = listOf(
                "Open GoPay app, select 'Send / Transfer'.",
                "Enter business phone: 087765483134.",
                "Verify recipient 'DAR Corporate Services' and send."
            ),
            badgeColor = 0xFF00AA13
        )
    )

    // KATALOG LAYANAN BERBAYAR (KHUSUS PERORANGAN & BADAN USAHA NON-MITRA)
    val paidServicesCatalog: List<PaidServiceItem> = listOf(
        // Layanan Khusus Perorangan (Individual)
        PaidServiceItem(
            id = "srv_consult_personal",
            titleId = "Konsultasi Hukum Privat Advokat (Perorangan)",
            titleEn = "Private Legal Advocate Consultation (Personal)",
            category = "Konsultasi Legal",
            descriptionId = "Konsultasi hukum privat tatap muka langsung di kantor SCBD atau via video conference terenkripsi bersama Advokat DARcorporate untuk perkara hukum pribadi, waris, tanah/properti, atau ketenagakerjaan.",
            descriptionEn = "Confidential private legal session with licensed corporate attorneys for personal civil disputes, property/land disputes, inheritance, or employment matters.",
            price = 350000,
            formattedPrice = "Rp 350.000",
            durationId = "45 Menit / Sesi",
            durationEn = "45 Mins / Session",
            targetAudience = ServiceTargetAudience.PERORANGAN,
            features = listOf(
                "Konsultasi Privat bersama Advokat Resmi",
                "Legal Opinion Ringkas Tertulis",
                "Kerahasiaan Dokumen Terjamin 100%",
                "Rekomendasi Langkah Mitigasi Pidana / Perdata"
            ),
            isPopular = true
        ),
        PaidServiceItem(
            id = "srv_contract_personal",
            titleId = "Review & Drafting Perjanjian / Surat Kuasa Pribadi",
            titleEn = "Personal Agreement & Power of Attorney Drafting",
            category = "Penyusunan Kontrak",
            descriptionId = "Penyusunan surat perjanjian perorangan, perjanjian utang-piutang berjaminan, sewa-menyewa, atau surat kuasa khusus berkekuatan hukum otentik.",
            descriptionEn = "Drafting and formal review of personal loan agreements, lease contracts, or special power of attorney under Indonesian civil law.",
            price = 750000,
            formattedPrice = "Rp 750.000",
            durationId = "1 - 2 Hari Kerja",
            durationEn = "1 - 2 Business Days",
            targetAudience = ServiceTargetAudience.PERORANGAN,
            features = listOf(
                "Penyusunan Sesuai KUHPerdata RI",
                "Revisi Draft hingga 3 Kali",
                "Panduan Meterai & Klausul Eksekutorial",
                "Dokumen Format PDF & Word Siap Tanda Tangan"
            )
        ),
        PaidServiceItem(
            id = "srv_hki_personal",
            titleId = "Pendaftaran Hak Cipta & Merek Dagang Pribadi (HKI)",
            titleEn = "Individual Trademark & Copyright Registration (IP)",
            category = "Kekayaan Intelektual",
            descriptionId = "Pendaftaran hak cipta karya atau merek dagang pribadi ke DJKI Kemenkumham RI dengan penelusuran kemiripan awal untuk menghindari penolakan.",
            descriptionEn = "Direct filing of individual trademark or copyright certification to the Directorate General of Intellectual Property (DJKI).",
            price = 1850000,
            formattedPrice = "Rp 1.850.000",
            durationId = "Proses Berkelanjutan",
            durationEn = "Full Processing",
            targetAudience = ServiceTargetAudience.PERORANGAN,
            features = listOf(
                "Penelusuran Awal Kemiripan Merek",
                "Pengisian Berkas Permohonan DJKI Lengkap",
                "Monitoring Status Publikasi Berita Resmi",
                "Sertifikat HKI Resmi Kemenkumham"
            )
        ),

        // Layanan Khusus Badan Usaha yang BUKAN Mitra
        PaidServiceItem(
            id = "srv_fasttrack_partner_audit",
            titleId = "Audit & Verifikasi Fast-Track Kemitraan (F1 & F2)",
            titleEn = "Fast-Track Partner Audit & Compliance Verification",
            category = "Kemitraan Korporat",
            descriptionId = "Layanan prioritas verifikasi legalitas NIB, NPWP, dan rekening badan usaha bukan mitra untuk langsung disahkan menjadi Mitra Resmi Buyer / Supplier DARcorporate dalam waktu 1x24 jam.",
            descriptionEn = "Express compliance audit of company registration (NIB, Tax ID) for non-partner business entities to qualify as verified DARcorporate Buyer/Supplier partners within 24 hours.",
            price = 950000,
            formattedPrice = "Rp 950.000",
            durationId = "Selesai 1x24 Jam",
            durationEn = "24 Hours Fast-Track",
            targetAudience = ServiceTargetAudience.BADAN_USAHA_NON_MITRA,
            features = listOf(
                "Verifikasi Prioritas 1x24 Jam",
                "Audit Legalitas Dokumen Akta & NIB",
                "Penerbitan ID Mitra Resmi & Status Verified",
                "Prioritas Matching Penawaran Barang Komoditas"
            ),
            isPopular = true
        ),
        PaidServiceItem(
            id = "srv_company_incorporation",
            titleId = "Pendirian Badan Usaha PT / CV Lengkap & NIB OSS",
            titleEn = "Full Corporate Formation (PT / CV) & OSS NIB",
            category = "Legalitas Badan Usaha",
            descriptionId = "Paket lengkap pendirian perseroan terbatas atau persekutuan komanditer: Akta Notaris, SK Pengesahan Kemenkumham RI, NIB OSS RBA, NPWP Badan, dan rekening giro perusahaan.",
            descriptionEn = "Complete corporate legal incorporation package: Notarial Deed, Ministry of Law Decree, OSS Risk-Based NIB, and Corporate Tax ID.",
            price = 4500000,
            formattedPrice = "Rp 4.500.000",
            durationId = "3 - 5 Hari Kerja",
            durationEn = "3 - 5 Business Days",
            targetAudience = ServiceTargetAudience.BADAN_USAHA_NON_MITRA,
            features = listOf(
                "Akta Notaris & SK Kemenkumham RI",
                "NIB Berbasis Risiko (OSS RBA)",
                "NPWP & SKT Pajak Badan Usaha",
                "Konsultasi Struktur Direksi & Rangkap Jabatan"
            )
        ),
        PaidServiceItem(
            id = "srv_commercial_due_diligence",
            titleId = "Legal Due Diligence & Audit Kontrak Komersial",
            titleEn = "Legal Due Diligence & Commercial Contract Audit",
            category = "Audit Kepatuhan",
            descriptionId = "Audit forensik kepatuhan kontrak bisnis pihak ketiga, pencegahan wanprestasi, dan mitigasi risiko sengketa hukum dagang bernilai tinggi.",
            descriptionEn = "Comprehensive legal due diligence and contract audit for non-partner enterprises entering multi-million commercial transactions.",
            price = 2750000,
            formattedPrice = "Rp 2.750.000",
            durationId = "2 - 3 Hari Kerja",
            durationEn = "2 - 3 Business Days",
            targetAudience = ServiceTargetAudience.BADAN_USAHA_NON_MITRA,
            features = listOf(
                "Laporan Forensic Legal Due Diligence",
                "Audit Klausul Pinalti & Arbitrase",
                "Penyusunan Addendum Perlindungan Aset",
                "Konsultasi Bersama Konsultan Hukum Super Master"
            )
        ),
        PaidServiceItem(
            id = "srv_somasi_asset_recovery",
            titleId = "Somasi Hukum Resmi & Tindakan Pemulihan Kerugian",
            titleEn = "Official Legal Demand Notice & Asset Recovery Pressure",
            category = "Penegakan Hukum",
            descriptionId = "Penerbitan surat somasi resmi dengan kop surat firma hukum DARcorporate dan tindakan tekanan hukum terukur untuk menagih piutang macet atau pemulihan kerugian bisnis.",
            descriptionEn = "Official legal demand notices issued on DARcorporate law firm letterhead to enforce settlement and debt/asset recovery.",
            price = 3200000,
            formattedPrice = "Rp 3.200.000",
            durationId = "Langsung Diproses",
            durationEn = "Immediate Execution",
            targetAudience = ServiceTargetAudience.BADAN_USAHA_NON_MITRA,
            features = listOf(
                "Surat Somasi I & II Kop Resmi Advokat",
                "Tekanan Hukum Pidana/Perdata Proporsional",
                "Pendampingan Mediasi Negosiasi Pelunasan",
                "Penyusunan Akta Perdamaian Dading Notariil"
            )
        )
    )

    // RIWAYAT TRANSAKSI & INVOICE PEMBAYARAN LAYANAN
    private val _serviceInvoices = MutableStateFlow<List<ServicePaymentInvoice>>(
        listOf(
            ServicePaymentInvoice(
                invoiceId = "INV-DAR-2026-901",
                serviceId = "srv_fasttrack_partner_audit",
                serviceTitle = "Audit & Verifikasi Fast-Track Kemitraan (F1 & F2)",
                customerName = "CV Sumber Rejeki Abadi",
                customerType = "Badan Usaha Non-Mitra",
                amount = 950000,
                formattedAmount = "Rp 950.000",
                channel = paymentChannels[0], // BCA VA
                status = PaymentStatus.PAID,
                createdAt = "10 Nov 2026, 14:20 WIB",
                paidAt = "10 Nov 2026, 14:25 WIB",
                referenceNumber = "REF-BCA-88992301",
                receiptPdfHash = "SHA256:7B9E21FC08A1B2C3"
            )
        )
    )
    val serviceInvoices: StateFlow<List<ServicePaymentInvoice>> = _serviceInvoices.asStateFlow()

    fun processDirectPayment(
        service: PaidServiceItem,
        channel: PaymentChannel,
        customerName: String,
        customerType: String,
        proofType: String? = null,
        proofUri: String? = null,
        proofNotes: String? = null
    ): ServicePaymentInvoice {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale.getDefault())
        val dateNow = sdf.format(Date())
        val randomRef = "REF-" + channel.code.takeLast(3) + "-" + System.currentTimeMillis().toString().takeLast(6)
        val randomHash = "SHA256:" + UUID.randomUUID().toString().replace("-", "").take(16).uppercase()
        val invoice = ServicePaymentInvoice(
            invoiceId = "INV-DAR-2026-" + (100 + _serviceInvoices.value.size + 1),
            serviceId = service.id,
            serviceTitle = service.titleId,
            customerName = customerName,
            customerType = customerType,
            amount = service.price,
            formattedAmount = service.formattedPrice,
            channel = channel,
            status = PaymentStatus.PAID,
            createdAt = dateNow,
            paidAt = dateNow,
            referenceNumber = randomRef,
            receiptPdfHash = randomHash,
            proofAttachmentType = proofType,
            proofAttachmentUri = proofUri,
            proofNotes = proofNotes,
            hasProofUploaded = (proofType != null || proofUri != null)
        )
        _serviceInvoices.value = listOf(invoice) + _serviceInvoices.value
        return invoice
    }

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

    fun updateF1ComplianceStatus(id: String, newStatus: PartnerComplianceStatus) {
        _f1Submissions.value = _f1Submissions.value.map {
            if (it.id == id) it.copy(complianceStatus = newStatus) else it
        }
    }

    fun updateF2ComplianceStatus(id: String, newStatus: PartnerComplianceStatus) {
        _f2Agreements.value = _f2Agreements.value.map {
            if (it.id == id) it.copy(complianceStatus = newStatus) else it
        }
    }

    // Biometric Face Registration & Verification Methods
    fun registerFaceProfile(userId: String, userName: String, confidence: Float = 99.4f): BiometricFaceProfile {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale.getDefault())
        val dateNow = sdf.format(Date())
        val randomHex = UUID.randomUUID().toString().replace("-", "").take(12).uppercase()
        val profile = BiometricFaceProfile(
            userId = userId,
            userName = userName,
            isRegistered = true,
            registrationDate = dateNow,
            confidenceScore = confidence,
            biometricHash = "DAR-BIO-$randomHex",
            antiSpoofingPassed = true,
            livenessScore = 0.98f
        )
        _biometricProfiles.value = _biometricProfiles.value + (userId to profile)
        return profile
    }

    fun isFaceRegistered(userId: String): Boolean {
        return _biometricProfiles.value[userId]?.isRegistered == true
    }

    fun getFaceProfile(userId: String): BiometricFaceProfile? {
        return _biometricProfiles.value[userId]
    }

    fun resetFaceProfile(userId: String) {
        _biometricProfiles.value = _biometricProfiles.value - userId
    }
}

