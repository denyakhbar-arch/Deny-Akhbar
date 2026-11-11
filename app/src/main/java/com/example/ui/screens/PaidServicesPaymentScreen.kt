package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DarcorpRepository
import com.example.model.*
import com.example.ui.theme.*

/**
 * Screen displaying the paid service menus specifically tailored for:
 * 1. Individuals logging in as personal clients (Pribadi Perorangan)
 * 2. Non-partner business entities (Badan Usaha Bukan Mitra)
 *
 * Integrated with direct Bank Virtual Accounts (BCA, Mandiri, BRI, BNI)
 * and verified E-Wallets (GoPay, OVO, DANA, ShopeePay, QRIS) for direct receipt
 * into DARcorporate corporate receiving accounts.
 */
@Composable
fun PaidServicesPaymentScreen(
    repository: DarcorpRepository,
    currentUser: UserProfile,
    isId: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val paidServices = repository.paidServicesCatalog
    val paymentChannels = repository.paymentChannels
    val invoices by repository.serviceInvoices.collectAsState()

    var selectedFilter by remember {
        mutableStateOf(
            if (currentUser.role == UserRole.PARTNER_BUYER || currentUser.role == UserRole.PARTNER_SUPPLIER) {
                ServiceTargetAudience.SEMUA
            } else {
                ServiceTargetAudience.PERORANGAN
            }
        )
    }

    var selectedServiceForCheckout by remember { mutableStateOf<PaidServiceItem?>(null) }
    var selectedChannel by remember { mutableStateOf(paymentChannels[0]) }
    var selectedTabCategory by remember { mutableStateOf(0) } // 0: Katalog, 1: Riwayat Pembayaran
    var lastPaidInvoice by remember { mutableStateOf<ServicePaymentInvoice?>(null) }

    val filteredServices = remember(selectedFilter, paidServices) {
        if (selectedFilter == ServiceTargetAudience.SEMUA) {
            paidServices
        } else {
            paidServices.filter { it.targetAudience == selectedFilter || it.targetAudience == ServiceTargetAudience.SEMUA }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner Penjelasan Layanan Berbayar Khusus Perorangan & Badan Usaha Non-Mitra
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarcorpNavy900),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("paid_services_banner_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = DarcorpGold500.copy(alpha = 0.2f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = DarcorpGold400,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isId) "Layanan Berbayar & Kasir Langsung" else "Paid Services & Direct Gateway",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = DarcorpGold500
                                ) {
                                    Text(
                                        text = "OFFICIAL",
                                        color = DarcorpNavy900,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (isId) "Khusus Pengguna Perorangan & Badan Usaha Non-Mitra"
                                       else "Exclusively for Personal Clients & Non-Partner Entities",
                                color = DarcorpGold400,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isId)
                            "Pembayaran layanan hukum, audit kemitraan, konsultasi, dan drafting kontrak langsung diterima ke rekening Virtual Account korporasi kami (BCA, Mandiri, BRI, BNI) atau akun E-Wallet resmi (GoPay, OVO, DANA, ShopeePay, QRIS) tanpa potongan pihak ketiga."
                        else
                            "Payments for legal retainers, partnership compliance audits, advocate consultations, and contract reviews are directly deposited into our corporate Virtual Accounts or registered E-Wallets.",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Direct Receiving Accounts Pill
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.08f),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(DarcorpGold500.copy(alpha = 0.5f), Color(0xFF38BDF8).copy(alpha = 0.5f)))
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = DarcorpGold400, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isId) "Rekening Penerima: PT DAR CORPORATE INDONESIA" else "Receiving: PT DAR CORPORATE INDONESIA",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "BCA • MANDIRI • BRI • BNI • EWALLET",
                                color = Color(0xFF38BDF8),
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // Sub-tabs: Katalog Layanan vs Riwayat Kuitansi Pembayaran
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTabCategory == 0,
                    onClick = { selectedTabCategory = 0 },
                    label = {
                        Text(
                            text = if (isId) "📋 Katalog Layanan (${filteredServices.size})" else "📋 Service Catalog (${filteredServices.size})",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedTabCategory == 1,
                    onClick = { selectedTabCategory = 1 },
                    label = {
                        Text(
                            text = if (isId) "🧾 Bukti & Kuitansi (${invoices.size})" else "🧾 Invoices (${invoices.size})",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Audience Filters (if in Katalog tab)
        if (selectedTabCategory == 0) {
            item {
                Column {
                    Text(
                        text = if (isId) "Kategori Sasaran Layanan:" else "Target Service Category:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarcorpNavy900
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ServiceTargetAudience.values().forEach { audience ->
                            FilterChip(
                                selected = selectedFilter == audience,
                                onClick = { selectedFilter = audience },
                                label = {
                                    Text(
                                        text = if (isId) audience.labelId else audience.labelEn,
                                        fontSize = 10.5.sp
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // List of Paid Service Cards
            items(filteredServices) { service ->
                PaidServiceCard(
                    service = service,
                    isId = isId,
                    onSelect = {
                        selectedServiceForCheckout = service
                    }
                )
            }
        } else {
            // Tab 1: Riwayat Transaksi & Kuitansi Pembayaran
            if (invoices.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, tint = DarcorpSlate600, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isId) "Belum Ada Transaksi Pembayaran" else "No Payment Invoices Yet",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DarcorpNavy900
                            )
                            Text(
                                text = if (isId) "Pilih salah satu layanan di katalog untuk melakukan pembayaran langsung."
                                       else "Select any service from the catalog to complete direct checkout.",
                                fontSize = 11.sp,
                                color = DarcorpSlate600,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(invoices) { inv ->
                    PaidInvoiceCard(invoice = inv, isId = isId)
                }
            }
        }
    }

    // Modal Checkout & Direct Payment Dialog
    if (selectedServiceForCheckout != null) {
        val service = selectedServiceForCheckout!!
        DirectPaymentCheckoutDialog(
            service = service,
            channels = paymentChannels,
            selectedChannel = selectedChannel,
            onSelectChannel = { selectedChannel = it },
            currentUser = currentUser,
            repository = repository,
            isId = isId,
            onDismiss = { selectedServiceForCheckout = null },
            onPaymentConfirmed = { paidInv ->
                selectedServiceForCheckout = null
                lastPaidInvoice = paidInv
                selectedTabCategory = 1 // Switch to invoices tab
                Toast.makeText(
                    context,
                    if (isId) "Pembayaran Berhasil! Layanan ${service.titleId} telah diaktifkan."
                    else "Payment Successful! Service ${service.titleEn} activated.",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    // Receipt Detail Modal
    if (lastPaidInvoice != null) {
        ReceiptDetailDialog(
            invoice = lastPaidInvoice!!,
            isId = isId,
            onDismiss = { lastPaidInvoice = null }
        )
    }
}

/**
 * Card displaying an individual Paid Service item.
 */
@Composable
fun PaidServiceCard(
    service: PaidServiceItem,
    isId: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("service_card_${service.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (service.isPopular) DarcorpGold500 else Color(0xFFE2E8F0)
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Badges Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DarcorpBrown700.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = service.category,
                        color = DarcorpBrown700,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (service.isPopular) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DarcorpGold500
                        ) {
                            Text(
                                text = if (isId) "★ TERPOPULER" else "★ POPULAR",
                                color = DarcorpNavy900,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (service.targetAudience == ServiceTargetAudience.PERORANGAN) Color(0xFFEDE9FE) else Color(0xFFDCFCE7)
                    ) {
                        Text(
                            text = if (service.targetAudience == ServiceTargetAudience.PERORANGAN) "PERORANGAN" else "BADAN USAHA NON-MITRA",
                            color = if (service.targetAudience == ServiceTargetAudience.PERORANGAN) Color(0xFF6D28D9) else Color(0xFF15803D),
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Price
            Text(
                text = if (isId) service.titleId else service.titleEn,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = DarcorpNavy900
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = service.formattedPrice,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = DarcorpBrown700
                )
                Text(
                    text = if (isId) service.durationId else service.durationEn,
                    fontSize = 10.5.sp,
                    color = DarcorpSlate600
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isId) service.descriptionId else service.descriptionEn,
                fontSize = 11.sp,
                color = DarcorpSlate700,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Features Checklist
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                service.features.forEach { feat ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = DarcorpEmerald, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = feat, fontSize = 10.5.sp, color = DarcorpNavy900)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Checkout Action Button
            Button(
                onClick = onSelect,
                colors = ButtonDefaults.buttonColors(containerColor = DarcorpNavy900),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("checkout_service_${service.id}")
            ) {
                Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = DarcorpGold400, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isId) "Pilih & Bayar Langsung (VA / E-Wallet)" else "Direct Payment (VA / E-Wallet)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Interactive Checkout Dialog linking direct Virtual Accounts & E-Wallets.
 */
@Composable
fun DirectPaymentCheckoutDialog(
    service: PaidServiceItem,
    channels: List<PaymentChannel>,
    selectedChannel: PaymentChannel,
    onSelectChannel: (PaymentChannel) -> Unit,
    currentUser: UserProfile,
    repository: DarcorpRepository,
    isId: Boolean,
    onDismiss: () -> Unit,
    onPaymentConfirmed: (ServicePaymentInvoice) -> Unit
) {
    val context = LocalContext.current
    var channelTypeTab by remember { mutableStateOf(0) } // 0: Virtual Account, 1: E-Wallet, 2: QRIS

    val vaChannels = remember(channels) { channels.filter { it.type == PaymentMethodType.VIRTUAL_ACCOUNT } }
    val ewalletChannels = remember(channels) { channels.filter { it.type == PaymentMethodType.E_WALLET } }
    val qrisChannel = remember(channels) { channels.find { it.type == PaymentMethodType.QRIS } ?: channels[0] }

    val activeList = when (channelTypeTab) {
        0 -> vaChannels
        1 -> ewalletChannels
        else -> listOf(qrisChannel)
    }

    val clipboardManager = remember {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    var proofBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var proofUri by remember { mutableStateOf<Uri?>(null) }
    var proofUploadType by remember { mutableStateOf<String?>(null) }
    var proofNotes by remember { mutableStateOf("") }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            proofBitmap = bitmap
            proofUri = null
            proofUploadType = if (isId) "Kamera Langsung" else "Direct Camera"
            Toast.makeText(context, if (isId) "Foto struk bukti pembayaran berhasil diambil!" else "Payment receipt photo captured!", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            proofUri = uri
            proofUploadType = if (isId) "Galeri / Album HP" else "Device Gallery"
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val decoded = BitmapFactory.decodeStream(inputStream)
                proofBitmap = decoded
                inputStream?.close()
            } catch (e: Exception) {
                // Uri retained for record
            }
            Toast.makeText(context, if (isId) "Bukti pembayaran dipilih dari galeri perangkat!" else "Receipt selected from gallery!", Toast.LENGTH_SHORT).show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = DarcorpBrown700)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isId) "Kasir Pembayaran Langsung" else "Direct Payment Gateway",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DarcorpNavy900
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = DarcorpSlate600)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState())
                    .clip(RoundedCornerShape(8.dp)),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Service Summary Pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF1F5F9),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFCBD5E1)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = if (isId) service.titleId else service.titleEn, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = DarcorpNavy900)
                            Text(
                                text = "Pembayar: ${currentUser.name} (${if (currentUser.role == UserRole.PARTNER_BUYER || currentUser.role == UserRole.PARTNER_SUPPLIER) "Badan Usaha Non-Mitra" else "Pribadi Perorangan"})",
                                fontSize = 9.5.sp,
                                color = DarcorpSlate600
                            )
                        }
                        Text(text = service.formattedPrice, fontWeight = FontWeight.Black, fontSize = 14.sp, color = DarcorpBrown700)
                    }
                }

                // Payment Category Tab Bar
                TabRow(
                    selectedTabIndex = channelTypeTab,
                    containerColor = Color(0xFFF8FAFC),
                    contentColor = DarcorpNavy900
                ) {
                    Tab(
                        selected = channelTypeTab == 0,
                        onClick = {
                            channelTypeTab = 0
                            onSelectChannel(vaChannels[0])
                        },
                        text = { Text(if (isId) "Rekening / VA" else "Virtual Account", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = channelTypeTab == 1,
                        onClick = {
                            channelTypeTab = 1
                            onSelectChannel(ewalletChannels[0])
                        },
                        text = { Text(if (isId) "E-Wallet" else "E-Wallet", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = channelTypeTab == 2,
                        onClick = {
                            channelTypeTab = 2
                            onSelectChannel(qrisChannel)
                        },
                        text = { Text("QRIS", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                // Channel Selector Chips
                if (channelTypeTab != 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        activeList.forEach { ch ->
                            FilterChip(
                                selected = selectedChannel.code == ch.code,
                                onClick = { onSelectChannel(ch) },
                                label = {
                                    Text(
                                        text = ch.name.replace(" Virtual Account", "").replace(" Rekening Penerima", "").replace(" Bisnis", "").replace(" Merchant", ""),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Account Number & Direct Deposit Details Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(selectedChannel.badgeColor).copy(alpha = 0.08f),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(selectedChannel.badgeColor).copy(alpha = 0.4f))
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedChannel.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(selectedChannel.badgeColor)
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Text(
                                    text = "OTOMATIS / BEBAS BIAYA",
                                    color = Color(0xFF15803D),
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        if (channelTypeTab == 2) {
                            // QRIS Dynamic Mock Barcode Display
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White,
                                    shadowElevation = 2.dp,
                                    modifier = Modifier.size(130.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        MockQrisBarcodeCanvas(modifier = Modifier.size(110.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "NMID: ID1020268899001 • DAR CORPORATE OFFICIAL",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarcorpNavy900
                                )
                            }
                        } else {
                            // Virtual Account / E-Wallet Number Box
                            Text(
                                text = if (channelTypeTab == 0) "Nomor Rekening / Virtual Account Korporasi:" else "Nomor Akun E-Wallet Penerima:",
                                fontSize = 9.5.sp,
                                color = DarcorpSlate600
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedChannel.accountNumber,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = DarcorpNavy900
                                )
                                OutlinedButton(
                                    onClick = {
                                        val clip = ClipData.newPlainText("Account Number", selectedChannel.accountNumber.replace("-", "").replace(" ", ""))
                                        clipboardManager.setPrimaryClip(clip)
                                        Toast.makeText(context, "Nomor ${selectedChannel.name} disalin!", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(30.dp).testTag("copy_account_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Salin", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = "Atas Nama: ${selectedChannel.accountHolder}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarcorpEmeraldDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(6.dp))

                        // Transfer Instructions
                        Text(
                            text = if (isId) "Panduan Pembayaran:" else "Payment Instructions:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarcorpNavy900
                        )
                        val instructions = if (isId) selectedChannel.instructionsId else selectedChannel.instructionsEn
                        instructions.forEachIndexed { idx, ins ->
                            Text(
                                text = "${idx + 1}. $ins",
                                fontSize = 9.sp,
                                color = DarcorpSlate700,
                                lineHeight = 12.sp
                            )
                        }
                    }
                }

                // FORM & TOMBOL UPLOAD BUKTI PEMBAYARAN
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFCBD5E1))
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("upload_proof_section")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = null,
                                    tint = DarcorpBrown700,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isId) "Form Upload Bukti Pembayaran" else "Proof of Payment Upload",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = DarcorpNavy900
                                )
                            }
                            if (proofBitmap != null || proofUri != null) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = "TERLAMPIR ✓",
                                        color = Color(0xFF15803D),
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (isId) "Unggah struk atau bukti transfer via kamera langsung atau pilih dari album galeri penyimpanan perangkat:"
                                   else "Upload payment proof via direct device camera or select from photo gallery:",
                            fontSize = 9.sp,
                            color = DarcorpSlate600,
                            lineHeight = 12.sp
                        )

                        // 2 Tombol Upload: Kamera Langsung & Galeri Album
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { cameraLauncher.launch(null) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = DarcorpBrown700
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("upload_proof_camera_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Kamera Langsung",
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isId) "Kamera Langsung" else "Camera",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    galleryLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = DarcorpNavy900
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("upload_proof_gallery_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "Pilih Galeri",
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isId) "Album / Galeri HP" else "Gallery / Album",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Preview Box if uploaded
                        if (proofBitmap != null || proofUri != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White,
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF86EFAC))
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        if (proofBitmap != null) {
                                            Image(
                                                bitmap = proofBitmap!!.asImageBitmap(),
                                                contentDescription = "Pratinjau Bukti Transfer",
                                                modifier = Modifier
                                                    .size(46.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(46.dp)
                                                    .background(Color(0xFFE2E8F0), RoundedCornerShape(6.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Image,
                                                    contentDescription = null,
                                                    tint = DarcorpSlate600,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Bukti: ${proofUploadType ?: "Lampiran Foto"}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.5.sp,
                                                color = DarcorpNavy900
                                            )
                                            Text(
                                                text = "File siap diverifikasi kasir korporasi",
                                                fontSize = 9.sp,
                                                color = Color(0xFF15803D)
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            proofBitmap = null
                                            proofUri = null
                                            proofUploadType = null
                                        },
                                        modifier = Modifier.size(28.dp).testTag("delete_proof_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Hapus Bukti",
                                            tint = Color.Red,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            // Optional Reference / Notes Input
                            OutlinedTextField(
                                value = proofNotes,
                                onValueChange = { proofNotes = it },
                                label = { Text(if (isId) "No. Referensi / Catatan Struk (Opsional)" else "Ref Number / Notes (Optional)", fontSize = 9.sp) },
                                placeholder = { Text("Contoh: Transfer via Dana / Struk No. 129", fontSize = 9.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("proof_notes_input"),
                                shape = RoundedCornerShape(8.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 10.5.sp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val customerTypeStr = if (currentUser.role == UserRole.PARTNER_BUYER || currentUser.role == UserRole.PARTNER_SUPPLIER) {
                        "Badan Usaha Non-Mitra"
                    } else {
                        "Pribadi Perorangan"
                    }
                    val invoice = repository.processDirectPayment(
                        service = service,
                        channel = selectedChannel,
                        customerName = currentUser.name,
                        customerType = customerTypeStr,
                        proofType = proofUploadType ?: (if (proofBitmap != null || proofUri != null) "Kamera Langsung" else null),
                        proofUri = proofUri?.toString() ?: (if (proofBitmap != null) "camera_capture_raw" else null),
                        proofNotes = proofNotes.ifBlank { null }
                    )
                    onPaymentConfirmed(invoice)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarcorpEmeraldDark),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_direct_payment_btn")
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isId) "Konfirmasi Pembayaran Lunas" else "Confirm Direct Payment",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isId) "Batal" else "Cancel", color = DarcorpSlate600)
            }
        }
    )
}

/**
 * Receipt / Invoice Card in Transaction History tab.
 */
@Composable
fun PaidInvoiceCard(invoice: ServicePaymentInvoice, isId: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("invoice_card_${invoice.invoiceId}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = invoice.invoiceId, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarcorpNavy900)
                    Text(text = invoice.createdAt, fontSize = 9.5.sp, color = DarcorpSlate600)
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFDCFCE7)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isId) "LUNAS (PAID)" else "PAID",
                            color = Color(0xFF15803D),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = invoice.serviceTitle, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = DarcorpNavy900)
            Text(
                text = "Metode: ${invoice.channel.name} (${invoice.channel.accountNumber})",
                fontSize = 10.sp,
                color = DarcorpSlate700
            )
            Text(
                text = "Pelanggan: ${invoice.customerName} • ${invoice.customerType}",
                fontSize = 10.sp,
                color = DarcorpSlate600
            )

            Spacer(modifier = Modifier.height(6.dp))
            if (invoice.hasProofUploaded) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFEFF6FF),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBFDBFE)))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Attachment, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Bukti Terlampir (${invoice.proofAttachmentType ?: "Dokumen"}) ✓",
                            color = Color(0xFF1D4ED8),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ref: ${invoice.referenceNumber}",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = DarcorpSlate600
                )
                Text(
                    text = invoice.formattedAmount,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = DarcorpBrown700
                )
            }
        }
    }
}

/**
 * Modal Dialog displaying official Digital Receipt.
 */
@Composable
fun ReceiptDetailDialog(
    invoice: ServicePaymentInvoice,
    isId: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = DarcorpEmeraldDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isId) "Kuitansi Pembayaran Resmi" else "Official Payment Receipt",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF0FDF4),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarcorpEmerald))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "PT DAR CORPORATE INDONESIA", fontWeight = FontWeight.Black, fontSize = 12.sp, color = DarcorpNavy900)
                        Text(text = "SCBD Tower 18, Level 22, Jakarta Selatan", fontSize = 9.sp, color = DarcorpSlate600)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Invoice: ${invoice.invoiceId}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DarcorpNavy900)
                        Text(text = "Layanan: ${invoice.serviceTitle}", fontSize = 11.sp, color = DarcorpSlate700)
                        Text(text = "Total Lunas: ${invoice.formattedAmount}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = DarcorpEmeraldDark)
                        Text(text = "Saluran: ${invoice.channel.name}", fontSize = 10.sp, color = DarcorpSlate600)
                        Text(text = "Nomor Rekening Tujuan: ${invoice.channel.accountNumber}", fontSize = 10.sp, color = DarcorpSlate600)
                        Text(text = "Waktu Bayar: ${invoice.paidAt}", fontSize = 9.5.sp, color = DarcorpSlate600)
                        if (invoice.hasProofUploaded) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFEFF6FF)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Bukti Terverifikasi (${invoice.proofAttachmentType})",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D4ED8)
                                    )
                                }
                            }
                            if (!invoice.proofNotes.isNullOrBlank()) {
                                Text(text = "Catatan Struk: ${invoice.proofNotes}", fontSize = 9.sp, color = DarcorpSlate600)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Hash Kriptografi: ${invoice.receiptPdfHash}", fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = DarcorpSlate600)
                    }
                }
                Text(
                    text = if (isId) "Kuitansi ini sah diterbitkan oleh sistem korporasi DARcorporate dan dijamin kepatuhannya oleh Konsultan Hukum."
                           else "This receipt is officially issued by DARcorporate legal compliance ecosystem.",
                    fontSize = 9.5.sp,
                    color = DarcorpSlate600,
                    lineHeight = 13.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val clip = ClipData.newPlainText("Invoice Ref", "${invoice.invoiceId} - ${invoice.referenceNumber}")
                    clipboardManager.setPrimaryClip(clip)
                    Toast.makeText(context, "Nomor referensi invoice disalin!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarcorpNavy900)
            ) {
                Text(if (isId) "Salin & Tutup" else "Copy & Close", color = Color.White)
            }
        }
    )
}

/**
 * High-tech mock QRIS barcode visualization canvas.
 */
@Composable
fun MockQrisBarcodeCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Outer border
        drawRect(color = Color.Black, topLeft = Offset.Zero, size = size, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()))

        // 3 Corner QR finder squares
        val boxSize = w * 0.22f
        // Top-left
        drawRect(color = Color.Black, topLeft = Offset(4f, 4f), size = androidx.compose.ui.geometry.Size(boxSize, boxSize))
        drawRect(color = Color.White, topLeft = Offset(8f, 8f), size = androidx.compose.ui.geometry.Size(boxSize - 8f, boxSize - 8f))
        drawRect(color = Color.Black, topLeft = Offset(12f, 12f), size = androidx.compose.ui.geometry.Size(boxSize - 16f, boxSize - 16f))

        // Top-right
        drawRect(color = Color.Black, topLeft = Offset(w - boxSize - 4f, 4f), size = androidx.compose.ui.geometry.Size(boxSize, boxSize))
        drawRect(color = Color.White, topLeft = Offset(w - boxSize, 8f), size = androidx.compose.ui.geometry.Size(boxSize - 8f, boxSize - 8f))
        drawRect(color = Color.Black, topLeft = Offset(w - boxSize + 4f, 12f), size = androidx.compose.ui.geometry.Size(boxSize - 16f, boxSize - 16f))

        // Bottom-left
        drawRect(color = Color.Black, topLeft = Offset(4f, h - boxSize - 4f), size = androidx.compose.ui.geometry.Size(boxSize, boxSize))
        drawRect(color = Color.White, topLeft = Offset(8f, h - boxSize), size = androidx.compose.ui.geometry.Size(boxSize - 8f, boxSize - 8f))
        drawRect(color = Color.Black, topLeft = Offset(12f, h - boxSize + 4f), size = androidx.compose.ui.geometry.Size(boxSize - 16f, boxSize - 16f))

        // Center QRIS badge
        drawCircle(color = Color(0xFFE11D48), radius = 10.dp.toPx(), center = Offset(w / 2, h / 2))
    }
}
