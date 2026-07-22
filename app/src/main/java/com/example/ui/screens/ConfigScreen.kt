package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WebConfig
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.PurpleTech
import com.example.ui.theme.DeepOceanDark
import com.example.ui.theme.SuccessGreen
import androidx.compose.ui.graphics.Brush
import com.example.ui.viewmodel.WebConfigViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreen(
    config: WebConfig?,
    viewModel: WebConfigViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Initialize states from existing config or defaults
    var name by remember { mutableStateOf(config?.name ?: "") }
    var url by remember { mutableStateOf(config?.url ?: "https://") }
    
    val cleanPackageName = remember {
        { appName: String ->
            val safeName = appName.lowercase()
                .replace("[^a-z0-9]".toRegex(), "")
            if (safeName.isEmpty()) "com.webtoapp.app" else "com.webtoapp.$safeName"
        }
    }
    
    var packageName by remember { mutableStateOf(config?.packageName ?: if (config != null) "" else cleanPackageName("")) }
    if (packageName.isEmpty() && config != null) {
        packageName = config.packageName.ifEmpty { cleanPackageName(config.name) }
    } else if (packageName.isEmpty()) {
        packageName = cleanPackageName(name)
    }

    var primaryColorHex by remember { mutableStateOf(config?.primaryColorHex ?: "#2196F3") }
    var iconName by remember { mutableStateOf(config?.iconName ?: "language") }
    var iconColorHex by remember { mutableStateOf(config?.iconColorHex ?: "#2196F3") }
    
    var enableSwipeToRefresh by remember { mutableStateOf(config?.enableSwipeToRefresh ?: true) }
    var enableProgressBar by remember { mutableStateOf(config?.enableProgressBar ?: true) }
    var enableZoom by remember { mutableStateOf(config?.enableZoom ?: false) }
    var isFullscreen by remember { mutableStateOf(config?.isFullscreen ?: false) }

    var enablePushNotifications by remember { mutableStateOf(config?.enablePushNotifications ?: true) }
    var testNotificationTitle by remember { mutableStateOf("Xin chào từ ${if (name.isNotEmpty()) name else "WebApp"}") }
    var testNotificationBody by remember { mutableStateOf("Đây là nội dung tin nhắn đẩy giả lập của bạn!") }

    var splashText by remember { mutableStateOf(config?.splashText ?: "Đang tải...") }
    var splashDurationMs by remember { mutableFloatStateOf((config?.splashDurationMs ?: 2000).toFloat()) }

    var userAgentType by remember { mutableStateOf(config?.userAgentType ?: "MOBILE") } // MOBILE, DESKTOP, CUSTOM
    var customUserAgent by remember { mutableStateOf(config?.customUserAgent ?: "") }
    var customCss by remember { mutableStateOf(config?.customCss ?: "") }
    var customJs by remember { mutableStateOf(config?.customJs ?: "") }

    val isEditMode = config != null

    // Presets for theme colors
    val colorPresets = listOf(
        "#2196F3" to "Royal Blue",
        "#00E5FF" to "Neon Cyan",
        "#E50914" to "Netflix Red",
        "#00E676" to "Green Accent",
        "#FF9100" to "Vivid Amber",
        "#0F172A" to "Slate Grey",
        "#8B5CF6" to "Tech Purple"
    )

    // Icons map mapping keys to actual vector icons and descriptors
    val iconPresets = listOf(
        "language" to Icons.Default.Language,
        "search" to Icons.Default.Search,
        "menu_book" to Icons.Default.MenuBook,
        "code" to Icons.Default.Code,
        "public" to Icons.Default.Public,
        "shopping_cart" to Icons.Default.ShoppingCart,
        "play_circle" to Icons.Default.PlayCircle,
        "chat" to Icons.Default.Chat,
        "home" to Icons.Default.Home
    )

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = AccentCyan,
        unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
        focusedBorderColor = AccentCyan,
        unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
        cursorColor = AccentCyan,
        focusedPlaceholderColor = Color.White.copy(alpha = 0.35f),
        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.35f)
    )

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
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Chỉnh sửa WebApp" else "Tạo WebApp Mới",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // SECTION 1: BASIC INFO
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.02f))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131929).copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Thông Tin Ứng Dụng",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { 
                            val prevAutoPkg = cleanPackageName(name)
                            val newName = it
                            name = newName
                            if (packageName == prevAutoPkg) {
                                packageName = cleanPackageName(newName)
                            }
                        },
                        label = { Text("Tên ứng dụng") },
                        placeholder = { Text("Ví dụ: Google Search, Facebook...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = packageName,
                        onValueChange = { packageName = it.lowercase().replace(" ", "") },
                        label = { Text("Package Name (Mã định danh duy nhất)") },
                        placeholder = { Text("Ví dụ: com.domain.myapp") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = AccentCyan.copy(alpha = 0.8f)
                            )
                        },
                        colors = textFieldColors
                    )
                    Text(
                        text = "Mã định danh duy nhất của ứng dụng Android trên hệ thống (ví dụ: com.example.myapp). Khi cài đặt, thiết bị sẽ nhận diện đây là một ứng dụng riêng biệt hoàn toàn.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        label = { Text("Đường dẫn website (URL)") },
                        placeholder = { Text("Ví dụ: https://www.google.com") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors
                    )
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.02f))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131929).copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Brush,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Giao Diện & Thẩm Mỹ",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Icon Chooser
                    Text(
                        "Chọn Biểu Tượng Ứng Dụng",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        iconPresets.take(5).forEach { (presetName, presetIcon) ->
                            val isSelected = iconName == presetName
                            val themeColor = try {
                                Color(android.graphics.Color.parseColor(primaryColorHex))
                            } catch (e: Exception) {
                                AccentCyan
                            }

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) themeColor else Color.White.copy(alpha = 0.06f))
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f),
                                        shape = CircleShape
                                    )
                                    .clickable { iconName = presetName },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = presetIcon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Color selection
                    Text(
                        "Tông màu chủ đạo (Primary Color)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        colorPresets.take(6).forEach { (hex, label) ->
                            val isSelected = primaryColorHex.lowercase() == hex.lowercase()
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(hex)))
                                    .clickable {
                                        primaryColorHex = hex
                                        iconColorHex = hex
                                    }
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.15f),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Splash screen setup
                    Text(
                        "Màn Hình Chờ (Splash Screen)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = splashText,
                        onValueChange = { splashText = it },
                        label = { Text("Thông điệp chào mừng") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Thời gian hiển thị: ${(splashDurationMs / 1000).toInt()} giây",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Slider(
                        value = splashDurationMs,
                        onValueChange = { splashDurationMs = it },
                        valueRange = 1000f..5000f,
                        steps = 3,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = AccentCyan,
                            activeTrackColor = AccentCyan,
                            inactiveTrackColor = Color.White.copy(alpha = 0.12f)
                        )
                    )
                }
            }

            // SECTION 3: WEBVIEW FEATURES
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.02f))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131929).copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Tính Năng WebView",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Toggle 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Hỗ trợ vuốt tải lại trang (Swipe Refresh)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
                            Text("Cho phép kéo từ trên xuống để cập nhật trang", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                        }
                        Switch(
                            checked = enableSwipeToRefresh,
                            onCheckedChange = { enableSwipeToRefresh = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AccentCyan,
                                checkedTrackColor = AccentBlue.copy(alpha = 0.5f),
                                uncheckedThumbColor = Color.White.copy(alpha = 0.4f),
                                uncheckedTrackColor = Color.White.copy(alpha = 0.12f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Toggle 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Hiển thị thanh tiến trình loading", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
                            Text("Chạy thanh progress mượt mà ở trên cùng", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                        }
                        Switch(
                            checked = enableProgressBar,
                            onCheckedChange = { enableProgressBar = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AccentCyan,
                                checkedTrackColor = AccentBlue.copy(alpha = 0.5f),
                                uncheckedThumbColor = Color.White.copy(alpha = 0.4f),
                                uncheckedTrackColor = Color.White.copy(alpha = 0.12f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Toggle 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Cho phép zoom trang (Zoom Controls)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
                            Text("Phóng to/thu nhỏ nội dung bằng 2 ngón tay", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                        }
                        Switch(
                            checked = enableZoom,
                            onCheckedChange = { enableZoom = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AccentCyan,
                                checkedTrackColor = AccentBlue.copy(alpha = 0.5f),
                                uncheckedThumbColor = Color.White.copy(alpha = 0.4f),
                                uncheckedTrackColor = Color.White.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }

            // SECTION 4: ADVANCED INJECTION & USER AGENTS
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.02f))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131929).copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Tùy Biến Nâng Cao (CSS & JS)",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("User-Agent (Trình giả lập)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("MOBILE", "DESKTOP").forEach { preset ->
                            val isSelected = userAgentType == preset
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) AccentCyan else Color.White.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .background(if (isSelected) AccentBlue.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.04f))
                                    .clickable { userAgentType = preset }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (preset == "MOBILE") "Điện Thoại" else "Máy Tính",
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customCss,
                        onValueChange = { customCss = it },
                        label = { Text("Nhúng Custom CSS") },
                        placeholder = { Text("Ví dụ: header { display: none !important; }") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5,
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = customJs,
                        onValueChange = { customJs = it },
                        label = { Text("Nhúng Custom JavaScript") },
                        placeholder = { Text("Ví dụ: alert('Đã khởi chạy web app!');") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5,
                        colors = textFieldColors
                    )
                }
            }

            // SECTION 5: PUSH NOTIFICATIONS
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.02f))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131929).copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Thông Báo Đẩy (Push Notifications)",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bật tính năng thông báo", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
                            Text("Hỗ trợ gửi tin nhắn FCM / Local Alerts", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                        }
                        Switch(
                            checked = enablePushNotifications,
                            onCheckedChange = { enablePushNotifications = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AccentCyan,
                                checkedTrackColor = AccentBlue.copy(alpha = 0.5f),
                                uncheckedThumbColor = Color.White.copy(alpha = 0.4f),
                                uncheckedTrackColor = Color.White.copy(alpha = 0.12f)
                            )
                        )
                    }

                    if (enablePushNotifications) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.04f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Kiểm tra giả lập thông báo đẩy", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AccentCyan)
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                OutlinedTextField(
                                    value = testNotificationTitle,
                                    onValueChange = { testNotificationTitle = it },
                                    label = { Text("Tiêu đề thông báo") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = textFieldColors
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = testNotificationBody,
                                    onValueChange = { testNotificationBody = it },
                                    label = { Text("Nội dung thông báo") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = textFieldColors
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        val dummyWebConfig = WebConfig(
                                            name = if (name.isNotEmpty()) name else "WebApp",
                                            url = url
                                        )
                                        viewModel.sendLocalTestPushNotification(
                                            dummyWebConfig,
                                            testNotificationTitle,
                                            testNotificationBody
                                        )
                                        Toast.makeText(context, "Đã gửi thông báo đẩy thử nghiệm!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AccentCyan,
                                        contentColor = DeepOceanDark
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.AddAlert, contentDescription = null, modifier = Modifier.size(16.dp), tint = DeepOceanDark)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("BẮN THỬ THÔNG BÁO PUSH", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepOceanDark)
                                }
                            }
                        }
                    }
                }
            }

            // Save Buttons
            val saveGradient = Brush.linearGradient(
                colors = listOf(AccentBlue, PurpleTech)
            )
            Button(
                onClick = {
                    if (name.isBlank() || url.isBlank() || url == "https://") {
                        Toast.makeText(context, "Vui lòng nhập đầy đủ tên ứng dụng và đường dẫn URL hợp lệ!", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    
                    val pkg = packageName.trim().lowercase()
                    val validPattern = "^[a-z_][a-z0-9_]*(\\.[a-z_][a-z0-9_]*)+$".toRegex()
                    if (pkg.isBlank()) {
                        Toast.makeText(context, "Vui lòng nhập Package Name!", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if (!pkg.matches(validPattern)) {
                        Toast.makeText(context, "Package Name không hợp lệ! Phải có ít nhất 2 phân đoạn phân cách bằng dấu chấm (ví dụ: com.example.app) và chỉ chứa chữ thường, số, dấu gạch dưới.", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    
                    var formattedUrl = url.trim()
                    if (!formattedUrl.startsWith("http://") && !formattedUrl.startsWith("https://")) {
                        formattedUrl = "https://$formattedUrl"
                    }

                    val webConfig = WebConfig(
                        id = config?.id ?: 0,
                        name = name,
                        url = formattedUrl,
                        primaryColorHex = primaryColorHex,
                        iconName = iconName,
                        iconColorHex = iconColorHex,
                        enableSwipeToRefresh = enableSwipeToRefresh,
                        enableProgressBar = enableProgressBar,
                        enableZoom = enableZoom,
                        isFullscreen = isFullscreen,
                        enablePushNotifications = enablePushNotifications,
                        splashText = splashText,
                        splashDurationMs = splashDurationMs.toInt(),
                        userAgentType = userAgentType,
                        customUserAgent = customUserAgent,
                        customCss = customCss,
                        customJs = customJs,
                        packageName = pkg,
                        timestamp = System.currentTimeMillis()
                    )

                    viewModel.saveConfig(webConfig) {
                        Toast.makeText(context, "Đã lưu cấu hình thành công!", Toast.LENGTH_SHORT).show()
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .background(saveGradient, RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("LƯU CẤU HÌNH & XUẤT NGAY", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
            }
        }
    }
}
}
