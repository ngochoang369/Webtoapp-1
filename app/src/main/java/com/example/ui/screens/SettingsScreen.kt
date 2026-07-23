package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.supabase.UserSession
import com.example.ui.components.PrivacyPolicyAdminNoticeCard
import com.example.ui.theme.VaultDarkBg
import com.example.ui.theme.VaultPrimary
import com.example.ui.theme.VaultSurfaceDark
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.VaultViewModel

@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    vaultViewModel: VaultViewModel,
    session: UserSession?,
    onSetNewPinRequested: () -> Unit
) {
    val isPinSet by vaultViewModel.isPinSet.collectAsState()

    var showExportDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        containerColor = VaultDarkBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382C54))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(VaultPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (session?.isAdmin == true) Icons.Default.AdminPanelSettings else Icons.Default.Lock,
                            contentDescription = "Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = session?.fullName.orEmpty().ifEmpty { "Người Dùng Vault" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = session?.email ?: "user@privadiary.app",
                            fontSize = 13.sp,
                            color = Color(0xFFA78BFA)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (session?.isAdmin == true) Color(0xFF4C1D95) else Color(0xFF064E3B))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (session?.isAdmin == true) "Quyền: ADMIN SECURITY AUDIT" else "Chế độ: User E2EE Standard",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Security & Vault Lock Settings Card
            Text(
                text = "BẢO MẬT & MÃ HÓA VAULT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382C54))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // PIN Switch / Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Pin, contentDescription = null, tint = VaultPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Mã PIN Mở Khóa Vault",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (isPinSet) "Đã bật mã PIN 4 chữ số" else "Chưa cài đặt mã PIN Vault",
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (isPinSet) {
                            Button(
                                onClick = { vaultViewModel.disablePin() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Tắt PIN", fontSize = 12.sp)
                            }
                        } else {
                            Button(
                                onClick = onSetNewPinRequested,
                                colors = ButtonDefaults.buttonColors(containerColor = VaultPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Thiết Lập PIN", fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lock Vault Now Button
                    if (isPinSet) {
                        OutlinedButton(
                            onClick = { vaultViewModel.lockVault() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Khóa Màn Hình Vault Ngay Thức Thì")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Backup Export Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382C54))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SAO LƯU & XUẤT DỮ LIỆU",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedButton(
                        onClick = { showExportDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Xuất Đĩnh Dạng Backup Đã Mã Hóa (.json)")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Mandatory Privacy Policy Admin Disclaimer Notice
            PrivacyPolicyAdminNoticeCard()

            Spacer(modifier = Modifier.height(28.dp))

            // Sign Out Button
            Button(
                onClick = { authViewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("logout_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Đăng xuất")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đăng Xuất Tài Khoản", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Export Backup Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Xuất Dữ Liệu Chế Độ Mã Hóa E2EE", color = Color.White) },
            text = {
                Column {
                    Text(
                        "Dữ liệu được xuất dưới dạng mã hóa AES-256 (Base64 Ciphertext). Chỉ những ai có Master Key hoặc người dùng chính chủ mới có thể giải mã.",
                        color = Color.LightGray,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF130E20))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "{\n  \"app\": \"PrivaDiary\",\n  \"encryption\": \"AES-256-GCM\",\n  \"user\": \"${session?.email}\",\n  \"master_key_hash\": \"PBKDF2_SHA256_64BIT\"\n}",
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF34D399),
                            fontSize = 11.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showExportDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = VaultPrimary)
                ) {
                    Text("Hoàn Tất Xuất Backup")
                }
            },
            containerColor = VaultSurfaceDark
        )
    }
}
