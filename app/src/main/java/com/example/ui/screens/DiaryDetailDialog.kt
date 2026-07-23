package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.data.model.DiaryModel
import com.example.ui.components.AdminAuditBadge
import com.example.ui.components.E2eeBadge
import com.example.ui.theme.VaultPrimary
import com.example.ui.theme.VaultSurfaceDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DiaryDetailDialog(
    diary: DiaryModel,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleAdminAudit: (Boolean) -> Unit = {}
) {
    var showRawCiphertext by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("EEEE, dd MMMM yyyy - HH:mm", Locale("vi", "VN")) }
    val formattedDate = remember(diary.createdAt) { dateFormat.format(Date(diary.createdAt)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("diary_detail_dialog"),
        containerColor = VaultSurfaceDark,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = diary.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formattedDate,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.LightGray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                // Badges Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (diary.isE2eeEncrypted) {
                        E2eeBadge()
                    }
                    AdminAuditBadge(isAllowed = diary.allowAdminAudit)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mood and Weather bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF261D3B))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cảm xúc: ${diary.mood}",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Thời tiết: ${diary.weather}",
                        color = Color(0xFFDDD6FE),
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tags
                if (diary.tags.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        diary.tags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF382C54))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = "#$tag", color = Color(0xFFA78BFA), fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Decrypted Content Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF130E20)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382C54))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (showRawCiphertext) "CHUỖI MÃ HÓA CIPHERTEXT (BASE64):" else "NỘI DUNG NHẬT KÝ ĐÃ GIẢI MÃ:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showRawCiphertext) Color(0xFFF59E0B) else VaultPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (showRawCiphertext) diary.rawContentEncrypted else diary.content,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp,
                            fontFamily = if (showRawCiphertext) FontFamily.Monospace else FontFamily.Default,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle raw encrypted payload button
                OutlinedButton(
                    onClick = { showRawCiphertext = !showRawCiphertext },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("toggle_raw_encrypted_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "Kiểm tra mã hóa",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showRawCiphertext) "Xem Nội Dung Giải Mã" else "Kiểm Tra Chuỗi Mã Hóa AES-256",
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        onDelete()
                        onDismiss()
                    },
                    modifier = Modifier.testTag("delete_diary_btn")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Xóa nhật ký", tint = Color(0xFFEF4444))
                }
                Row {
                    TextButton(onClick = onDismiss) {
                        Text("Đóng", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onEdit()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VaultPrimary),
                        modifier = Modifier.testTag("edit_diary_btn")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Sửa", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Chỉnh Sửa")
                    }
                }
            }
        }
    )
}
