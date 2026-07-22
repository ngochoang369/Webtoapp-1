package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WebConfig
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.PurpleTech
import com.example.ui.theme.DeepOceanDark
import com.example.ui.viewmodel.WebConfigViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: WebConfigViewModel,
    onCreateNew: () -> Unit,
    onEditConfig: (WebConfig) -> Unit,
    onBuildConfig: (WebConfig) -> Unit,
    onLaunchSandbox: (WebConfig) -> Unit,
    standaloneConfigId: Int,
    onToggleStandalone: (WebConfig) -> Unit
) {
    val context = LocalContext.current
    val configs by viewModel.allConfigs.collectAsState()

    var configToDelete by remember { mutableStateOf<WebConfig?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepOceanDark,
                        Color(0xFF0F1424),
                        Color(0xFF040814)
                    )
                )
            )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "WebToApp Console",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    "Trình Chuyển Đổi Web Thành App APK",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onCreateNew,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .size(56.dp)
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(colors = listOf(AccentCyan, PurpleTech)),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(
                            brush = Brush.linearGradient(colors = listOf(AccentBlue, PurpleTech)),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tạo mới WebApp",
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Hero Banner Card
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(AccentCyan.copy(alpha = 0.5f), PurpleTech.copy(alpha = 0.5f))
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF141F3D).copy(alpha = 0.6f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            AccentBlue.copy(alpha = 0.15f),
                                            PurpleTech.copy(alpha = 0.1f)
                                        )
                                    )
                                )
                                .padding(24.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = AccentCyan),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    ) {
                                        Text(
                                            text = "CONSOLE V2.0",
                                            color = DeepOceanDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "⚡ Premium Studio",
                                        color = AccentCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Biến Website thành App đẳng cấp",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Thiết kế giao diện riêng biệt, tùy chỉnh màu sắc chủ đạo, splash screen, chặn header/footer thừa và đóng gói thành file cài đặt APK mượt mà như app gốc.",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 8.dp),
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }
                }

                // WebApps Title
                item {
                    Text(
                        "Danh Sách Ứng Dụng Đã Tạo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // List of WebConfigs
                if (configs.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp)
                                .border(
                                    width = 1.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.02f))
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF131929).copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(AccentCyan.copy(alpha = 0.1f), CircleShape)
                                        .border(1.dp, AccentCyan.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = AccentCyan
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Chưa có ứng dụng nào",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Ấn nút '+' phát sáng bên dưới màn hình để bắt đầu tạo ứng dụng WebView đầu tiên của bạn!",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    color = Color.White.copy(alpha = 0.6f),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                } else {
                    items(configs, key = { it.id }) { config ->
                    WebConfigCard(
                        config = config,
                        isPinnedStandalone = (config.id == standaloneConfigId),
                        onToggleStandalone = { onToggleStandalone(config) },
                        onLaunch = { onLaunchSandbox(config) },
                        onBuild = { onBuildConfig(config) },
                        onEdit = { onEditConfig(config) },
                        onDelete = { configToDelete = config }
                    )
                }
            }

            // Developer Pro Tips / Guide section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Mẹo Tối Ưu Từ Chuyên Gia",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            "• Ghép nối Độc lập: Nhấn nút Android (Ghim) trên thẻ ứng dụng để đặt làm App chính. Khi đó, file APK tải xuống hoặc khi mở app này lên sẽ boot thẳng vào WebApp đó mà không qua trình quản lý!\n" +
                            "• Inject CSS: Sử dụng CSS tùy chỉnh để ẩn đi header, footer của website, giúp WebView trông thoáng hơn và giống app thực thụ.\n" +
                            "• User-Agent: Chọn chế độ 'Điện thoại' cho giao diện tối ưu nhất, hoặc chế độ 'Máy tính' nếu website không hỗ trợ responsive tốt.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            lineHeight = 18.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(64.dp)) // Safe padding for FAB
            }
        }
    }

    // Confirmation delete dialog
    if (configToDelete != null) {
        val config = configToDelete!!
        AlertDialog(
            onDismissRequest = { configToDelete = null },
            title = { Text("Xóa ứng dụng?") },
            text = { Text("Bạn có chắc chắn muốn xóa cấu hình ứng dụng '${config.name}'? Thao tác này không thể hoàn tác.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteConfig(config)
                        configToDelete = null
                        Toast.makeText(context, "Đã xóa ứng dụng thành công!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("XÓA", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { configToDelete = null }) {
                    Text("HỦY")
                }
            }
        )
    }
}
}

@Composable
fun WebConfigCard(
    config: WebConfig,
    isPinnedStandalone: Boolean,
    onToggleStandalone: () -> Unit,
    onLaunch: () -> Unit,
    onBuild: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    // Parse Primary theme Color Safely
    val themeColor = remember(config.primaryColorHex) {
        try {
            Color(android.graphics.Color.parseColor(config.primaryColorHex))
        } catch (e: Exception) {
            Color(0xFF2196F3)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLaunch() }
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        themeColor.copy(alpha = 0.4f),
                        themeColor.copy(alpha = 0.05f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131929).copy(alpha = 0.75f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Circular Dynamic App Icon preset
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(themeColor, themeColor.copy(alpha = 0.7f))
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getIconByName(config.iconName),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // App info text
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = config.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White,
                            maxLines = 1
                        )
                        if (isPinnedStandalone) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SuccessGreen),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = "ĐỘC LẬP",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = config.url,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }

                // Android Standalone Lock button
                IconButton(onClick = onToggleStandalone) {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = "Đặt làm app độc lập chính",
                        tint = if (isPinnedStandalone) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Delete button
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lower controls (Launch, Compile, Edit)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Test Sandbox Run Button
                Button(
                    onClick = onLaunch,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColor.copy(alpha = 0.12f),
                        contentColor = themeColor
                    ),
                    modifier = Modifier
                        .weight(1.2f)
                        .border(
                            width = 1.dp,
                            color = themeColor.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = themeColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("CHẠY THỬ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = themeColor)
                }

                // Compile/Build APK Button
                val buttonGradient = remember(themeColor) {
                    Brush.linearGradient(
                        colors = listOf(themeColor, themeColor.copy(alpha = 0.75f))
                    )
                }
                Button(
                    onClick = onBuild,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1.5f)
                        .background(buttonGradient, RoundedCornerShape(10.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("XUẤT APK", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                }

                // Edit configuration
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Sửa",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// Icon mapper helper
fun getIconByName(name: String): ImageVector {
    return when (name) {
        "search" -> Icons.Default.Search
        "menu_book" -> Icons.Default.MenuBook
        "code" -> Icons.Default.Code
        "public" -> Icons.Default.Public
        "shopping_cart" -> Icons.Default.ShoppingCart
        "play_circle" -> Icons.Default.PlayCircle
        "chat" -> Icons.Default.Chat
        "home" -> Icons.Default.Home
        else -> Icons.Default.Language
    }
}
