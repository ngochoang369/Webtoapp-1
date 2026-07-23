package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AdminBadgeBg
import com.example.ui.theme.AdminBadgeText
import com.example.ui.theme.E2eeBadgeBg
import com.example.ui.theme.E2eeBadgeText

@Composable
fun E2eeBadge(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(E2eeBadgeBg)
            .border(1.dp, E2eeBadgeText.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Mã hóa E2EE",
            tint = E2eeBadgeText,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "E2EE AES-256",
            color = E2eeBadgeText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AdminAuditBadge(modifier: Modifier = Modifier, isAllowed: Boolean = true) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isAllowed) AdminBadgeBg else Color(0xFF374151))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AdminPanelSettings,
            contentDescription = "Admin Audit",
            tint = if (isAllowed) AdminBadgeText else Color.LightGray,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Admin Audit Compliance",
            color = AdminBadgeText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PrivacyPolicyAdminNoticeCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF231838)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6D28D9))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Chính sách quyền riêng tư",
                tint = Color(0xFFA78BFA),
                modifier = Modifier
                    .size(28.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Thông báo phân quyền & Bảo mật Admin",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(top = 4.dp))
                Text(
                    text = "Tài khoản Admin (devregish@gmail.com) có quyền kiểm toán bảo mật nhật ký người dùng. Về quyền xem nhật ký user, Admin sẽ tự tạo trang chính sách quyền riêng tư chi tiết sau.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFDDD6FE),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
