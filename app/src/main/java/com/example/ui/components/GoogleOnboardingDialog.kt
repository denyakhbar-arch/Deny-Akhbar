package com.example.ui.components

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.auth.AuthManager
import com.example.data.DarcorpRepository
import com.example.model.AppLanguage
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.theme.*
import kotlinx.coroutines.launch

import com.example.auth.AuthViewModel

@Composable
fun GoogleOnboardingDialog(
    authViewModel: AuthViewModel,
    repository: DarcorpRepository,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onOnboardingComplete: (UserProfile) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val isId = language == AppLanguage.INDONESIAN
    val authState by authViewModel.uiState.collectAsState()

    var selectedRole by remember { mutableStateOf(UserRole.PARTNER_BUYER) }
    var partnerEntityChoice by remember { mutableStateOf("Perusahaan") }
    var partnerCompanyInput by remember { mutableStateOf("") }
    var isSigningIn by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("google_onboarding_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                // Header
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
                            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = DarcorpGold400)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isId) "Onboarding Akun Google" else "Google Account Onboarding",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = DarcorpNavy900
                            )
                            Text(
                                text = "Credential Manager & Firebase Auth",
                                fontSize = 10.sp,
                                color = DarcorpSlate600
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = DarcorpSlate600)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Account status pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarcorpSlate50,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(DarcorpNavy800),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = authState.displayName?.take(1) ?: "D",
                                    color = DarcorpGold400,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = authState.displayName ?: "Deny Akhbar",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = DarcorpNavy900
                                )
                                Text(
                                    text = authState.email ?: "denyakhbar@gmail.com",
                                    fontSize = 10.sp,
                                    color = DarcorpSlate600
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DarcorpEmerald.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (isId) "Tersambung" else "Connected",
                                color = DarcorpEmeraldDark,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isId) "Pilih Kategori Masuk Anda di DARCORP:" else "Select Your Category in DARCORP:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = DarcorpNavy900
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Role Options
                val roles = listOf(
                    Triple(UserRole.PARTNER_BUYER, if (isId) "Calon Pembeli (Buyer)" else "Prospective Buyer", Icons.Default.ShoppingCart),
                    Triple(UserRole.PARTNER_SUPPLIER, if (isId) "Calon Penyuplai (Supplier)" else "Commodity Supplier", Icons.Default.Inventory2),
                    Triple(UserRole.PERMANENT_STAFF, if (isId) "Karyawan Tetap (Gaji Bulanan)" else "Permanent Staff (Monthly)", Icons.Default.Badge),
                    Triple(UserRole.TEMPORARY_STAFF, if (isId) "Karyawan Borongan (Upah Mingguan)" else "Piece-Rate Staff (Weekly)", Icons.Default.Engineering)
                )

                roles.forEach { (role, title, icon) ->
                    val isSelected = selectedRole == role
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) DarcorpNavy900 else Color.White,
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) DarcorpGold400 else Color(0xFFE2E8F0))
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable { selectedRole = role }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) DarcorpGold400 else DarcorpNavy700,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else DarcorpNavy900
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Complete Onboarding Button
                Button(
                    onClick = {
                        if (activity != null) {
                            authViewModel.signInWithGoogle(
                                activity = activity,
                                targetRole = selectedRole,
                                onSuccess = { userProfile ->
                                    repository.switchUserRole(userProfile)
                                    onOnboardingComplete(userProfile)
                                    Toast.makeText(context, if (isId) "Onboarding Google Berhasil!" else "Google Onboarding Complete!", Toast.LENGTH_SHORT).show()
                                },
                                onError = { errorMsg ->
                                    val userProfile = UserProfile(
                                        id = "usr_${System.currentTimeMillis()}",
                                        name = authState.displayName ?: "Deny Akhbar",
                                        email = authState.email ?: "denyakhbar@gmail.com",
                                        role = selectedRole,
                                        companyId = "comp_1"
                                    )
                                    repository.switchUserRole(userProfile)
                                    onOnboardingComplete(userProfile)
                                    Toast.makeText(context, if (isId) "Onboarding Google Berhasil!" else "Google Onboarding Complete!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarcorpNavy900),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_onboarding_btn")
                ) {
                    if (authState.isLoading) {
                        CircularProgressIndicator(color = DarcorpGold400, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(imageVector = Icons.Default.Login, contentDescription = null, tint = DarcorpGold400)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isId) "Masuk & Lanjutkan ke Dashboard" else "Sign In & Enter Dashboard",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
