# Solusi Perbaikan Tampilan Blank Saat Deploy ke Vercel (vercel.app)

Jika sebelumnya tampilan sempat **blank (layar putih kosong)** saat di-deploy ke Vercel, berikut adalah penyebab dan perbaikan yang telah kami selesaikan secara tuntas:

---

### Penyebab Layar Blank Sebelumnya
1. **Konflik Redirect Loop pada `vercel.json`:**
   Aturan `cleanUrls: true` yang dipadukan dengan rewrite `destination: "/index.html"` menyebabkan browser terjebak dalam *308 Permanent Redirect loop* (Vercel mengarahkan `/index.html` ke `/`, lalu rewrite mengembalikan `/` ke `/index.html`), sehingga halaman gagal dimuat dan menjadi blank.
2. **Perbedaan Deteksi Direktori (Root vs Public):**
   Beberapa konfigurasi proyek Vercel secara default membaca file `index.html` langsung di root repository (`./`). Jika `index.html` hanya berada di dalam subfolder `public/`, Vercel menganggap proyek tidak memiliki file index dan menampilkan halaman kosong.

---

### Perbaikan Tuntas yang Telah Diterapkan
1. **Pembersihan `vercel.json`:**
   - Menghapus aturan rewrite dan redirect loop yang berbenturan. Vercel secara native menyajikan `index.html` pada root `/` tanpa memerlukan rewrite regex.
   - Menghapus parameter lama `"public": true`.
   - Mempertahankan header keamanan `X-Frame-Options: SAMEORIGIN` (agar pembaca PDF 13 halaman Company Profile tetap dapat dibuka langsung).
   - Mempertahankan aturan PWA Service Worker (`sw.js`) dan Manifest.
2. **Sinkronisasi Multi-Direktori (Root `./`, `public/`, dan `dist/`):**
   - File `index.html`, `dar_emblem.png`, `manifest.json`, `sw.js`, dan folder `assets/` (termasuk PDF Company Profile) kini tersedia di **ketiga lokasi**:
     1. Direktori Utama (**Root `./`**)
     2. Direktori **`public/`**
     3. Direktori **`dist/`**
   - Menambahkan skrip `build.js` pada `package.json` (`"build": "node build.js"`). Setiap kali Vercel menjalankan proses build, skrip ini memastikan seluruh aset tersinkronisasi otomatis.

---

### Cara Melakukan Redeploy di Vercel (Agar Perbaikan Aktif)

#### Jika Menggunakan GitHub:
1. Push update terbaru ke repository GitHub Anda:
   ```bash
   git add .
   git commit -m "Fix: Solusi layar blank di Vercel (hapus redirect loop & sync multi-directory)"
   git push origin main
   ```
2. Vercel akan **otomatis melakukan build ulang** dalam 15–30 detik.
3. Buka URL `https://<nama-proyek>.vercel.app` Anda. Lakukan *Hard Refresh* pada browser (`Ctrl + F5` di Windows atau `Cmd + Shift + R` di Mac) untuk membersihkan cache browser lama.
4. **Hasil:** Tampilan DARcorporate langsung terbuka penuh dengan seluruh tombol, banner, formulir, kasir, dan pembaca PDF aktif normal.

#### Pengaturan Dashboard Vercel (Bila Diperlukan):
Di dashboard Vercel $\rightarrow$ **Project Settings** $\rightarrow$ **General**:
- **Framework Preset:** `Other`
- **Root Directory:** `./`
- **Build Command:** `node build.js` (atau biarkan default)
- **Output Directory:** Kosongkan atau biarkan default (karena file sudah tersedia di `./`, `public/`, maupun `dist/`).
