package com.example.model

import androidx.compose.ui.geometry.Offset

enum class EntityType(val labelId: String, val labelEn: String) {
    PT("PT (Perseroan Terbatas)", "PT (Limited Liability Corp)"),
    CV("CV (Commanditaire Vennootschap)", "CV (Limited Partnership)"),
    YAYASAN("Yayasan (Social Foundation)", "Foundation (Non-profit)")
}

enum class BusinessSector(val labelId: String, val labelEn: String) {
    TRADING("Bisnis Jual Beli (Trading & Ekspor-Impor)", "Trading & Commerce (Export-Import)"),
    SERVICES("Bisnis Jual Jasa & Konsultansi", "Services & Consulting"),
    SUPPLIER("Bisnis Penyuplai Komoditas & Material", "Commodity & Material Supplier"),
    LOGISTICS("Bisnis Logistik & Pergudangan", "Logistics & Warehousing"),
    MANUFACTURING("Bisnis Manufaktur & Pabrikasi", "Manufacturing & Fabrication")
}

enum class UserRole(val labelId: String, val labelEn: String) {
    LEGAL_OFFICER("Konsultan Hukum / Legal (Super Controller)", "Legal Counsel (Super Controller)"),
    DIREKTUR_UTAMA("Direktur Utama (Operational Controller)", "CEO (Operational Controller)"),
    OFFICIAL("Pejabat Tinggi Perusahaan", "High Official"),
    PERMANENT_STAFF("Karyawan Tetap", "Permanent Staff"),
    TEMPORARY_STAFF("Karyawan Borongan / Lepas", "Piece-Rate / Temporary Staff"),
    PARTNER_BUYER("Calon Pembeli (Buyer)", "Prospective Buyer"),
    PARTNER_SUPPLIER("Calon Penyuplai (Supplier)", "Prospective Supplier")
}

enum class OfficialTitle(val titleId: String, val titleEn: String) {
    SEKRETARIS("Sekretaris Perusahaan", "Corporate Secretary"),
    MANAJER_KEUANGAN("Manajer Keuangan", "Finance Manager"),
    MANAJER_PEMASARAN("Manajer Pemasaran", "Marketing Manager"),
    MANAJER_OPERASIONAL("Manajer Operasional", "Operations Manager"),
    KEPALA_GUDANG("Kepala Gudang", "Warehouse Chief")
}

data class Company(
    val id: String,
    val name: String,
    val entityType: EntityType,
    val sector: BusinessSector,
    val nib: String,
    val npwp: String,
    val address: String,
    val legalDate: String,
    val isVerified: Boolean = true
)

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val companyId: String,
    val officialTitles: List<OfficialTitle> = emptyList(),
    val avatarInitial: String = name.take(2).uppercase()
)

data class AttendanceRecord(
    val id: String,
    val employeeId: String,
    val employeeName: String,
    val isPermanent: Boolean,
    val clockInTime: String,
    val clockOutTime: String? = null,
    val selfieColor: Long = 0xFF1E3A8A,
    val latitude: Double,
    val longitude: Double,
    val locationName: String,
    val dailyReport: String? = null,
    val status: String = "Terverifikasi"
)

data class BiometricFaceProfile(
    val userId: String,
    val userName: String,
    val isRegistered: Boolean = false,
    val registrationDate: String = "",
    val confidenceScore: Float = 0f,
    val biometricHash: String = "",
    val antiSpoofingPassed: Boolean = false,
    val livenessScore: Float = 0.98f,
    val featureVectorSize: Int = 512,
    val registeredBy: String = "SCBD Biometric Terminal Engine v2.4"
)

enum class ChatTier(val id: Int, val titleId: String, val titleEn: String, val descriptionId: String, val descriptionEn: String) {
    TIER_1_EXECUTIVE(
        1,
        "Ruang Eksekutif & Pejabat Tinggi",
        "Executive & High Officials Room",
        "Privat: Konsultan Hukum + Direktur Utama + 5 Pejabat Tinggi",
        "Private: Legal Counsel + CEO + 5 High Officials"
    ),
    TIER_2_PERMANENT(
        2,
        "Operasional Karyawan Tetap",
        "Permanent Staff Operations",
        "Konsultan Hukum + Direktur Utama + Pejabat + Seluruh Karyawan Tetap",
        "Legal Counsel + CEO + Officials + All Permanent Staff"
    ),
    TIER_3_TEMPORARY(
        3,
        "Tim Lapangan & Karyawan Borongan",
        "Field Team & Piece-Rate Staff",
        "Konsultan Hukum + Direktur Utama + Pejabat + Seluruh Karyawan Borongan/Lepas",
        "Legal Counsel + CEO + Officials + All Temporary/Piece-Rate Staff"
    )
}

data class ChatMessage(
    val id: String,
    val tier: ChatTier,
    val senderId: String,
    val senderName: String,
    val senderRole: String,
    val messageText: String,
    val timestamp: String,
    val isUrgent: Boolean = false
)

enum class DocumentCategory(val labelId: String, val labelEn: String) {
    PAYMENT_PROOF("Bukti Pembayaran", "Payment Proof"),
    DELIVERY_NOTE("Surat Jalan / Pengiriman", "Delivery Note / Waybill"),
    PURCHASE_RECEIPT("Bukti Pembelian / Faktur", "Purchase Receipt / Invoice"),
    INCOMING_FUNDS("Bukti Dana Masuk", "Incoming Funds"),
    OUTGOING_FUNDS("Bukti Dana Keluar", "Outgoing Funds")
}

data class VaultDocument(
    val id: String,
    val title: String,
    val category: DocumentCategory,
    val companyName: String,
    val uploadedBy: String,
    val uploaderRole: String,
    val uploadDate: String,
    val daysRemaining: Int = 30, // 30-day retention
    val fileSizeKb: Int = 420,
    val format: String = "JPG"
)

enum class PartnerComplianceStatus(val code: String, val labelId: String, val labelEn: String) {
    PENDING("PENDING", "Menunggu Verifikasi", "Pending"),
    VERIFIED("VERIFIED", "Terverifikasi Legal", "Verified"),
    ACTIVE("ACTIVE", "Aktif Beroperasi", "Active"),
    UNDER_REVIEW("UNDER_REVIEW", "Dalam Peninjauan", "Under Legal Review")
}

data class FormF1Submission(
    val id: String,
    val partnerType: String, // "Pembeli (Buyer)" or "Penyuplai (Supplier)"
    val entityType: String,  // "Pribadi" or "Perusahaan"
    val partnerName: String,
    val companyName: String,
    val contactEmail: String,
    val phone: String,
    val itemDescription: String,
    val itemSpecification: String,
    val samplePhotosCount: Int,
    val weightPerShipment: Double,
    val weightUnit: String, // "Kg" or "Ton"
    val unitPrice: Double,
    val currency: String = "IDR",
    val submittedAt: String,
    val deliveredToLegal: Boolean = true,
    val deliveredToDirut: Boolean = true,
    val complianceStatus: PartnerComplianceStatus = PartnerComplianceStatus.ACTIVE
)

data class FormF2Agreement(
    val id: String,
    val partnerName: String,
    val partnerType: String,
    val entityType: String,
    val idNumber: String, // NIK or NIB
    val legalAddress: String,
    val hasKtpUploaded: Boolean,
    val hasSelfieUploaded: Boolean,
    val signaturePoints: List<Offset>,
    val signedAt: String,
    val agreementDocumentNumber: String,
    val contractStatus: String = "Resmi Terikat (Legally Binding)",
    val complianceStatus: PartnerComplianceStatus = PartnerComplianceStatus.VERIFIED
)

// -------------------------------------------------------------
// PAID SERVICES & DIRECT PAYMENT MODELS (VIRTUAL ACCOUNT & E-WALLET)
// -------------------------------------------------------------
enum class ServiceTargetAudience(val labelId: String, val labelEn: String) {
    PERORANGAN("Khusus Perorangan (Individual)", "Personal / Individual Clients"),
    BADAN_USAHA_NON_MITRA("Khusus Badan Usaha Non-Mitra", "Non-Partner Business Entities"),
    SEMUA("Semua Kategori", "All Categories")
}

data class PaidServiceItem(
    val id: String,
    val titleId: String,
    val titleEn: String,
    val category: String,
    val descriptionId: String,
    val descriptionEn: String,
    val price: Long,
    val formattedPrice: String,
    val durationId: String,
    val durationEn: String,
    val targetAudience: ServiceTargetAudience,
    val features: List<String>,
    val isPopular: Boolean = false
)

enum class PaymentMethodType(val labelId: String, val labelEn: String) {
    VIRTUAL_ACCOUNT("Virtual Account Bank", "Bank Virtual Account"),
    E_WALLET("Dompet Digital (E-Wallet)", "E-Wallet"),
    QRIS("QRIS Dinamis Nasional", "National QRIS")
}

data class PaymentChannel(
    val code: String,
    val name: String,
    val type: PaymentMethodType,
    val accountNumber: String,
    val accountHolder: String,
    val fee: Long = 0,
    val formattedFee: String = "Gratis",
    val instructionsId: List<String>,
    val instructionsEn: List<String>,
    val badgeColor: Long = 0xFF1E3A8A
)

enum class PaymentStatus(val labelId: String, val labelEn: String, val colorHex: Long) {
    PENDING("Menunggu Pembayaran", "Pending Payment", 0xFFD97706),
    PAID("Lunas Terbayar", "Paid & Activated", 0xFF16A34A),
    EXPIRED("Kedaluwarsa", "Expired", 0xFFDC2626)
}

data class ServicePaymentInvoice(
    val invoiceId: String,
    val serviceId: String,
    val serviceTitle: String,
    val customerName: String,
    val customerType: String,
    val amount: Long,
    val formattedAmount: String,
    val channel: PaymentChannel,
    val status: PaymentStatus = PaymentStatus.PAID,
    val createdAt: String,
    val paidAt: String? = null,
    val referenceNumber: String,
    val receiptPdfHash: String,
    val proofAttachmentType: String? = null,
    val proofAttachmentUri: String? = null,
    val proofNotes: String? = null,
    val hasProofUploaded: Boolean = false
)

