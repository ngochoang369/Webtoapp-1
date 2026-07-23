package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DiaryModel
import com.example.data.supabase.UserSession
import com.example.ui.components.AdminAuditBadge
import com.example.ui.components.E2eeBadge
import com.example.ui.theme.VaultDarkBg
import com.example.ui.theme.VaultPrimary
import com.example.ui.theme.VaultSurfaceDark
import com.example.ui.viewmodel.DiaryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryListScreen(
    diaryViewModel: DiaryViewModel,
    session: UserSession?,
    onAddNewClick: () -> Unit,
    onEditClick: (DiaryModel) -> Unit
) {
    val diaries by diaryViewModel.userDiaries.collectAsState()
    val searchQuery by diaryViewModel.searchQuery.collectAsState()
    val selectedMood by diaryViewModel.selectedMoodFilter.collectAsState()

    var selectedDiaryForDetail by remember { mutableStateOf<DiaryModel?>(null) }

    val moodOptions = listOf(
        "Tất cả" to null,
        "Vui 😀" to "Vui 😀",
        "Bình yên 🌿" to "Bình yên 🌿",
        "Sâu lắng 🌙" to "Sâu lắng 🌙",
        "Trăn trở 💭" to "Trăn trở 💭",
        "Hào hứng 🚀" to "Hào hứng 🚀",
        "Căng thẳng ⚡" to "Căng thẳng ⚡"
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("diary_list_screen"),
        containerColor = VaultDarkBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNewClick,
                containerColor = VaultPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .testTag("add_diary_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Viết Nhật Ký Mới")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header Bar
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
                    Column {
                        Text(
                            text = "Nhật Ký Cá Nhân",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = session?.email ?: "user@privadiary.app",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFA78BFA)
                        )
                    }
                    E2eeBadge()
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { diaryViewModel.setSearchQuery(it) },
                    placeholder = { Text("Tìm kiếm bài viết, thẻ, từ khóa...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { diaryViewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Xóa tìm kiếm", tint = Color.Gray)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_diary_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VaultPrimary,
                        unfocusedBorderColor = Color(0xFF382C54),
                        focusedContainerColor = Color(0xFF130E20),
                        unfocusedContainerColor = Color(0xFF130E20),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Mood Filter Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(moodOptions) { (label, moodVal) ->
                        val isSelected = (selectedMood == moodVal && moodVal != null) || (selectedMood == null && moodVal == null)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) VaultPrimary else Color(0xFF261D3B))
                                .clickable { diaryViewModel.setMoodFilter(moodVal) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else Color(0xFFDDD6FE),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Entries List or Empty State
            if (diaries.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(VaultSurfaceDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Chưa có nhật ký",
                            tint = VaultPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Chưa Có Bài Viết Nào",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nhấn nút (+) bên dưới để tạo bài viết nhật ký riêng tư mã hóa đầu cuối đầu tiên.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(diaries, key = { it.id }) { item ->
                        DiaryCardItem(
                            diary = item,
                            onClick = { selectedDiaryForDetail = item }
                        )
                    }
                }
            }
        }
    }

    // Detail Dialog
    selectedDiaryForDetail?.let { diary ->
        DiaryDetailDialog(
            diary = diary,
            onDismiss = { selectedDiaryForDetail = null },
            onEdit = { onEditClick(diary) },
            onDelete = { diaryViewModel.deleteDiary(diary.id) },
            onToggleAdminAudit = { allowed ->
                diaryViewModel.toggleAdminAudit(diary.id, diary.allowAdminAudit)
            }
        )
    }
}

@Composable
fun DiaryCardItem(
    diary: DiaryModel,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("vi", "VN")) }
    val formattedDate = remember(diary.createdAt) { dateFormat.format(Date(diary.createdAt)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("diary_card_${diary.id}"),
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
                Text(
                    text = diary.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (diary.isE2eeEncrypted) {
                        E2eeBadge()
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = diary.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFDDD6FE),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata row (Date, Mood, Weather, Tags)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = diary.mood, fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "•", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = diary.weather, fontSize = 12.sp, color = Color.LightGray)
                }

                Text(
                    text = formattedDate,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
