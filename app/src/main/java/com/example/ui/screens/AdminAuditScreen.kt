package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiaryModel
import com.example.data.supabase.UserSession
import com.example.ui.components.AdminAuditBadge
import com.example.ui.components.E2eeBadge
import com.example.ui.components.PrivacyPolicyAdminNoticeCard
import com.example.ui.theme.VaultDarkBg
import com.example.ui.theme.VaultPrimary
import com.example.ui.theme.VaultSurfaceDark
import com.example.ui.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminAuditScreen(adminViewModel: AdminViewModel) {
    val allDiaries by adminViewModel.allDiariesForAdmin.collectAsState()
    val auditLogs by adminViewModel.auditLogs.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    var inspectingDiary by remember { mutableStateOf<DiaryModel?>(null) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_audit_screen"),
        containerColor = VaultDarkBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VaultSurfaceDark)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4C1D95)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Bảng Thẩm Định Security Admin",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Admin: ${UserSession.ADMIN_EMAIL}",
                                fontSize = 12.sp,
                                color = Color(0xFFA78BFA)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mandatory Privacy Notice Card
                PrivacyPolicyAdminNoticeCard()

                Spacer(modifier = Modifier.height(16.dp))

                // Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = VaultPrimary,
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = VaultPrimary
                            )
                        }
                    }
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = {
                            Text(
                                text = "Thẩm Định Nhật Ký (${allDiaries.size})",
                                color = if (selectedTabIndex == 0) VaultPrimary else Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = {
                            Text(
                                text = "Log Kiểm Toán (${auditLogs.size})",
                                color = if (selectedTabIndex == 1) VaultPrimary else Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
            }

            // Tab 0: User Diary Audit List
            if (selectedTabIndex == 0) {
                if (allDiaries.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Chưa có dữ liệu nhật ký người dùng trong cơ sở dữ liệu.",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(allDiaries) { entry ->
                            AdminDiaryAuditCard(
                                diary = entry,
                                onInspect = {
                                    adminViewModel.logAuditAction(
                                        targetUserEmail = entry.userEmail,
                                        actionType = "AUDIT_VIEW_ENTRY",
                                        reason = "Kiểm toán bảo mật và tính toàn vẹn E2EE bài viết",
                                        entryId = entry.id
                                    )
                                    inspectingDiary = entry
                                }
                            )
                        }
                    }
                }
            }

            // Tab 1: Audit Log History
            if (selectedTabIndex == 1) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(auditLogs) { log ->
                        val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()) }
                        val formattedTime = remember(log.timestamp) { dateFormat.format(Date(log.timestamp)) }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382C54))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = log.actionType,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFA78BFA),
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = formattedTime,
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Admin: ${log.adminEmail} -> User: ${log.targetUserEmail}",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Lý do: ${log.reason}",
                                    color = Color.LightGray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Inspect Dialog
    inspectingDiary?.let { diary ->
        DiaryDetailDialog(
            diary = diary,
            onDismiss = { inspectingDiary = null },
            onEdit = { },
            onDelete = { },
            onToggleAdminAudit = { }
        )
    }
}

@Composable
fun AdminDiaryAuditCard(
    diary: DiaryModel,
    onInspect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382C54))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "User: ${diary.userEmail}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA78BFA),
                        fontSize = 13.sp
                    )
                    Text(
                        text = diary.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                AdminAuditBadge(isAllowed = diary.allowAdminAudit)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = diary.content,
                color = Color(0xFFDDD6FE),
                maxLines = 2,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                E2eeBadge()

                Button(
                    onClick = onInspect,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Thẩm định",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Kiểm Toán Bài Viết", fontSize = 12.sp)
                }
            }
        }
    }
}
