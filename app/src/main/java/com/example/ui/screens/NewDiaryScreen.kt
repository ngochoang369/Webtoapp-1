package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiaryModel
import com.example.ui.theme.VaultDarkBg
import com.example.ui.theme.VaultPrimary
import com.example.ui.theme.VaultSurfaceDark
import com.example.ui.viewmodel.DiaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDiaryScreen(
    diaryViewModel: DiaryViewModel,
    editingDiary: DiaryModel? = null,
    onBackClick: () -> Unit
) {
    var titleInput by remember { mutableStateOf(editingDiary?.title ?: "") }
    var contentInput by remember { mutableStateOf(editingDiary?.content ?: "") }
    var selectedMood by remember { mutableStateOf(editingDiary?.mood ?: "Bình yên 🌿") }
    var selectedWeather by remember { mutableStateOf(editingDiary?.weather ?: "Nắng ☀️") }
    var tagInput by remember { mutableStateOf(editingDiary?.tags?.joinToString(", ") ?: "Cá nhân") }
    var isE2eeEncrypted by remember { mutableStateOf(editingDiary?.isE2eeEncrypted ?: true) }

    val moodList = listOf("Vui 😀", "Bình yên 🌿", "Sâu lắng 🌙", "Trăn trở 💭", "Hào hứng 🚀", "Căng thẳng ⚡")
    val weatherList = listOf("Nắng ☀️", "Mưa 🌧️", "Mây ☁️", "Đêm 🌌", "Gió 🍃")

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("new_diary_screen"),
        containerColor = VaultDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (editingDiary != null) "Chỉnh Sửa Nhật Ký" else "Viết Nhật Ký Mới",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VaultSurfaceDark)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Title Input
            OutlinedTextField(
                value = titleInput,
                onValueChange = { titleInput = it },
                label = { Text("Tiêu đề bài viết") },
                placeholder = { Text("VD: Một ngày tĩnh lặng trong lòng thành phố...", color = Color.Gray) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("diary_title_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VaultPrimary,
                    unfocusedBorderColor = Color(0xFF382C54),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mood Picker
            Text(
                text = "Cảm xúc hôm nay",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(moodList) { mood ->
                    val isSelected = mood == selectedMood
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) VaultPrimary else VaultSurfaceDark)
                            .clickable { selectedMood = mood }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = mood,
                            color = if (isSelected) Color.White else Color(0xFFDDD6FE),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weather Picker
            Text(
                text = "Thời tiết",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(weatherList) { weather ->
                    val isSelected = weather == selectedWeather
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color(0xFF10B981) else VaultSurfaceDark)
                            .clickable { selectedWeather = weather }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = weather,
                            color = if (isSelected) Color.White else Color(0xFFDDD6FE),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tags Input
            OutlinedTextField(
                value = tagInput,
                onValueChange = { tagInput = it },
                label = { Text("Thẻ / Phân loại (phân cách bằng dấu phẩy)") },
                leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, tint = Color.Gray) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("diary_tags_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VaultPrimary,
                    unfocusedBorderColor = Color(0xFF382C54),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content Area
            OutlinedTextField(
                value = contentInput,
                onValueChange = { contentInput = it },
                label = { Text("Nội dung nhật ký (Mã hóa riêng tư)") },
                placeholder = { Text("Viết tâm tư, suy nghĩ của bạn ở đây...", color = Color.Gray) },
                minLines = 6,
                maxLines = 12,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("diary_content_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VaultPrimary,
                    unfocusedBorderColor = Color(0xFF382C54),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Security Options Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382C54))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Quyền Riêng Tư & Phân Quyền Access Control",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // E2EE Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = VaultPrimary, modifier = Modifier.padding(end = 4.dp))
                                Text(
                                    text = "Mã hóa đầu cuối E2EE AES-256",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Mã hóa trực tiếp tại thiết bị trước khi lưu DB",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = isE2eeEncrypted,
                            onCheckedChange = { isE2eeEncrypted = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = VaultPrimary
                            ),
                            modifier = Modifier.testTag("e2ee_switch")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    if (contentInput.isNotBlank()) {
                        val parsedTags = tagInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        diaryViewModel.saveDiary(
                            id = editingDiary?.id,
                            title = titleInput.ifBlank { "Nhật ký ngày ${System.currentTimeMillis()}" },
                            content = contentInput,
                            mood = selectedMood,
                            tags = parsedTags,
                            weather = selectedWeather,
                            isE2eeEncrypted = isE2eeEncrypted,
                            allowAdminAudit = true
                        )
                        onBackClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_diary_submit_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = VaultPrimary),
                shape = RoundedCornerShape(12.dp),
                enabled = contentInput.isNotBlank()
            ) {
                Icon(Icons.Default.Save, contentDescription = "Lưu")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (editingDiary != null) "Cập Nhật Nhật Ký" else "Mã Hóa & Lưu Bài Viết",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
