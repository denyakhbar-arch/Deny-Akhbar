package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.Strings
import com.example.ui.theme.*

@Composable
fun GoogleSitesHeader(
    language: AppLanguage,
    onLanguageToggle: () -> Unit,
    onDownloadClick: () -> Unit,
    onLaunchPortalClick: () -> Unit,
    onGoogleLoginClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onCompanyProfileClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DarcorpNavy900,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Google Sites Top Site Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarcorpNavy800)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = "Official Site",
                        tint = DarcorpGold400,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "sites.google.com/view/darcorp-official",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Google Login button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(DarcorpNavy900)
                            .border(1.dp, DarcorpGold500.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .clickable { onGoogleLoginClick() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("nav_google_login_btn"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Google",
                            tint = DarcorpGold400,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == AppLanguage.INDONESIAN) "Login Google" else "Google Sign-In",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Language switcher pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(DarcorpNavy900)
                            .border(1.dp, DarcorpGold600.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .clickable { onLanguageToggle() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("language_toggle_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = language.flag,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (language == AppLanguage.INDONESIAN) "ID|EN" else "EN|ID",
                            color = DarcorpGold400,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Main Navbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Emblem & Logo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Reset to top */ }
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarcorpGold500),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "DC",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = DarcorpNavy900
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "DARcorporate",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = DarcorpGold600.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "OFFICIAL",
                                    color = DarcorpGold400,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (language == AppLanguage.INDONESIAN) "Ekosistem Multi-Perusahaan" else "Multi-Enterprise Ecosystem",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }

                // Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Unduh APK Button (Bar Tombol Berwarna Cokelat)
                    Button(
                        onClick = onDownloadClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarcorpBrown700,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("nav_download_apk_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download APK",
                            tint = DarcorpBrownCream,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == AppLanguage.INDONESIAN) "Unduh APK" else "Download APK",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }

                    // Launch App Button
                    Button(
                        onClick = onLaunchPortalClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarcorpBrown600,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("nav_launch_portal_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Launch Portal",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == AppLanguage.INDONESIAN) "Buka App" else "Open App",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Primary Navigation Menu Bar (HOME, COMPANY PROFILE, LOG IN)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarcorpBrown800)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Button 1: HOME
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarcorpBrown700,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onHomeClick() }
                        .testTag("menu_home_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = "Home", tint = DarcorpBrownCream, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "HOME",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Button 2: COMPANY PROFILE
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarcorpBrown700,
                    modifier = Modifier
                        .weight(1.35f)
                        .clickable { onCompanyProfileClick() }
                        .testTag("menu_company_profile_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Business, contentDescription = "Company Profile", tint = DarcorpBrownCream, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "COMPANY PROFILE",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }

                // Button 3: LOG IN
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarcorpGold500,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onLoginClick() }
                        .testTag("menu_login_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Login, contentDescription = "Log In", tint = DarcorpBrown900, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LOG IN",
                            color = DarcorpBrown900,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}
