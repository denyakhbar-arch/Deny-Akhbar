package com.example.model

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    INDONESIAN("id", "Bahasa Indonesia", "🇮🇩"),
    ENGLISH("en", "English", "🇬🇧")
}

object Strings {
    fun get(key: String, lang: AppLanguage): String {
        val id = lang == AppLanguage.INDONESIAN
        return when (key) {
            // Navigation
            "nav_home" -> if (id) "Beranda" else "Home"
            "nav_about" -> if (id) "Tentang DARCORP" else "About DARCORP"
            "nav_features" -> if (id) "Fitur Unggulan" else "Key Features"
            "nav_ecosystem" -> if (id) "Ekosistem" else "Ecosystem"
            "nav_download" -> if (id) "Unduh Aplikasi" else "Download App"
            "nav_portal" -> if (id) "Buka Portal" else "Launch Portal"
            "nav_lang_toggle" -> if (id) "Ganti Bahasa" else "Switch Language"

            // Hero
            "hero_badge" -> if (id) "Sistem Tata Kelola Korporasi Modern & Legalitas Terpadu" else "Modern Corporate Governance & Unified Legal Platform"
            "hero_title" -> if (id) "Platform Manajemen Multi-Perusahaan Terpusat" else "Centralized Multi-Enterprise Management Platform"
            "hero_subtitle" -> if (id) "Dua Akun Pemegang Kendali (Konsultan Hukum & Direktur Utama), Absensi Wajah & GPS Pintar, Komunikasi Terisolasi 3-Tier, dan Portal Kerjasama Terbuka."
                               else "Dual-Master Governance (Legal Counsel & CEO), Smart GPS & Face Attendance, 3-Tier Isolated Group Communication, and Open Partner Portal."
            "hero_download_android" -> if (id) "Unduh untuk Android (.APK)" else "Download for Android (.APK)"
            "hero_download_ios" -> if (id) "Unduh untuk iOS (.IPA)" else "Download for iOS (.IPA)"
            "hero_scan_qr" -> if (id) "Pindai Kode QR" else "Scan QR Code"
            "hero_explore_portal" -> if (id) "Uji Coba Portal Interaktif" else "Explore Interactive Portal"
            "hero_version_info" -> if (id) "Versi 2.4.0 LTS • Android 8.0+ & iOS 15.0+ • Diperbarui Nov 2026" else "Version 2.4.0 LTS • Android 8.0+ & iOS 15.0+ • Updated Nov 2026"

            // Value pillars
            "feature_multi_company_title" -> if (id) "Multi-Perusahaan (PT, CV, Yayasan)" else "Multi-Company (Corp, LLC, Foundation)"
            "feature_multi_company_desc" -> if (id) "Kelola berbagai entitas legalitas dan badan usaha dalam satu atap dengan pemisahan database terisolasi." else "Manage multiple legal entities and business sectors under one unified roof with isolated tenancy."
            
            "feature_dual_master_title" -> if (id) "Kendali Ganda (Legal & Dirut)" else "Dual-Master Control (Legal & CEO)"
            "feature_dual_master_desc" -> if (id) "Akses tertinggi dipegang Konsultan Hukum untuk proteksi & penghapusan data, didampingi Direktur Utama untuk eksekusi bisnis." else "Supreme authority held by Corporate Legal Counsel for data purge & rights, partnered with CEO for operational command."

            "feature_attendance_title" -> if (id) "Absensi Wajah & Lock GPS" else "Face Selfie & GPS Device Lock"
            "feature_attendance_desc" -> if (id) "Presensi wajib foto selfie perangkat dan koordinat lokasi presisi. Laporan kerja harian wajib sebelum jam kepulangan." else "Clock-in requires live selfie photo and verified device GPS coordinates. Mandatory daily work reports before clock-out."

            "feature_chat_title" -> if (id) "Obrolan Terisolasi 3-Tier" else "3-Tier Isolated Group Chat"
            "feature_chat_desc" -> if (id) "Grup 1 Pimpinan Eksekutif, Grup 2 Karyawan Tetap, dan Grup 3 Karyawan Borongan. Mencegah kebocoran rahasia perusahaan." else "Group 1 Executives, Group 2 Permanent Staff, and Group 3 Temporary/Piece-rate staff. Eliminates confidential leaks."

            "feature_documents_title" -> if (id) "Vault Dokumen Retensi 30 Hari" else "30-Day Auto Retention Vault"
            "feature_documents_desc" -> if (id) "Bukti bayar, surat jalan, dan arus kas otomatis tersimpan 30 hari lalu terhapus permanen untuk efisiensi & kerahasiaan." else "Payment receipts, delivery invoices, and cash flows auto-stored for 30 days then purged for privacy and efficiency."

            "feature_partners_title" -> if (id) "Portal Mitra: Formulir F1 & F2" else "Partner Portal: Form F1 & F2"
            "feature_partners_desc" -> if (id) "Calon Buyer & Supplier login dengan Google, isi formulir penawaran harga (F1) dan tanda tangan kesepakatan MoA digital (F2)." else "Buyers & Suppliers login with Google, submit price quotations (F1), and sign legally binding digital agreements (F2)."

            // Download Modal / Card
            "download_modal_title" -> if (id) "Unduh Aplikasi DARCORP" else "Download DARCORP Mobile Application"
            "download_modal_subtitle" -> if (id) "Instal langsung di perangkat smartphone untuk akses cepat, notifikasi real-time, dan absensi otomatis." else "Install directly on your smartphone for fast access, real-time push alerts, and automated attendance."
            "download_android_note" -> if (id) "Paket APK Resmi (v2.4.0) • Ukuran 24.8 MB • Verifikasi SHA-256 Aman" else "Official APK Package (v2.4.0) • Size 24.8 MB • Verified SHA-256 Secure"
            "download_ios_note" -> if (id) "Tersedia di Apple TestFlight & Enterprise Deployment • iOS 15.0+" else "Available on Apple TestFlight & Enterprise Deployment • iOS 15.0+"
            "download_action_now" -> if (id) "Mulai Mengunduh Sekarang" else "Start Download Now"
            "download_close" -> if (id) "Tutup" else "Close"

            // Roles
            "role_legal" -> if (id) "Konsultan Hukum / Legal (Super Controller)" else "Corporate Legal Counsel (Super Controller)"
            "role_dirut" -> if (id) "Direktur Utama (Operational Controller)" else "Chief Executive Officer (Operational Controller)"
            "role_official" -> if (id) "Pejabat Tinggi Perusahaan" else "High Corporate Official"
            "role_permanent" -> if (id) "Karyawan Tetap (Gaji Bulanan)" else "Permanent Employee (Monthly Payroll)"
            "role_temporary" -> if (id) "Karyawan Borongan (Upah Mingguan)" else "Temporary / Piece-Rate (Weekly Payroll)"
            "role_partner" -> if (id) "Calon Mitra (Buyer / Supplier)" else "Prospective Partner (Buyer / Supplier)"

            // Common actions
            "btn_clock_in" -> if (id) "Absen Masuk (Clock In)" else "Clock In"
            "btn_clock_out" -> if (id) "Absen Pulang (Clock Out)" else "Clock Out"
            "btn_send" -> if (id) "Kirim" else "Send"
            "btn_submit" -> if (id) "Kirimkan Formulir" else "Submit Form"
            "btn_download" -> if (id) "Unduh" else "Download"
            "btn_back_to_landing" -> if (id) "Kembali ke Beranda" else "Back to Landing Page"
            "btn_switch_role" -> if (id) "Ganti Peran Demonstrasi" else "Switch Demo Role"

            else -> key
        }
    }
}
