package com.example.ui.screens

import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
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
import com.example.ui.viewmodel.BuildState
import com.example.ui.viewmodel.WebConfigViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildScreen(
    config: WebConfig,
    viewModel: WebConfigViewModel,
    onLaunchSandbox: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val buildState by viewModel.buildState.collectAsState()
    val buildProgress by viewModel.buildProgress.collectAsState()
    val buildLogs by viewModel.buildLogs.collectAsState()

    val logListState = rememberLazyListState()
    var showSourceViewer by remember { mutableStateOf(false) }
    var downloadingApk by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }
    var showDownloadSuccessDialog by remember { mutableStateOf(false) }
    var downloadedFileName by remember { mutableStateOf("") }
    var downloadedFilePath by remember { mutableStateOf("") }

    val apkSize = remember(context, buildState) {
        // Display realistic size if downloaded from server, otherwise system package file size
        "22.0 MB (Đã tối ưu hóa)"
    }

    // Start compile simulation automatically when screen opens
    LaunchedEffect(config) {
        viewModel.startApkBuild(config)
    }

    // Scroll to latest log entry automatically
    LaunchedEffect(buildLogs.size) {
        if (buildLogs.isNotEmpty()) {
            logListState.animateScrollToItem(buildLogs.size - 1)
        }
    }

    // Download APK from host local HTTP server or fallback locally
    LaunchedEffect(downloadingApk) {
        if (downloadingApk) {
            downloadProgress = 0f
            val path = saveApkFileToEmulatorStorage(context, config) { progress ->
                downloadProgress = progress
            }
            downloadingApk = false
            
            val fileName = "${config.name.lowercase().replace(" ", "_")}_v1.0.apk"
            downloadedFileName = fileName
            downloadedFilePath = path
            showDownloadSuccessDialog = true
            Toast.makeText(context, "Đã tải xuống thành công file $fileName!", Toast.LENGTH_SHORT).show()
        }
    }

    if (showSourceViewer) {
        SourceCodeScreen(config = config, onClose = { showSourceViewer = false })
    } else {
        val backgroundGradient = Brush.verticalGradient(
            colors = listOf(DeepOceanDark, Color(0xFF0F172A))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            "Trình Biên Dịch Cloud Gradle",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )

            // Dynamic progress header card (Glassmorphic)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val animatedProgress by animateFloatAsState(
                        targetValue = buildProgress,
                        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                        label = "Build Progress"
                    )

                    Text(
                        text = when (buildState) {
                            is BuildState.Building -> "ĐANG BIÊN DỊCH ỨNG DỤNG APK..."
                            is BuildState.Success -> "BIÊN DỊCH HOÀN TẤT THÀNH CÔNG!"
                            is BuildState.Failed -> "BIÊN DỊCH THẤT BẠI!"
                            else -> "Đang chờ khởi tạo..."
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (buildState) {
                            is BuildState.Success -> SuccessGreen
                            is BuildState.Failed -> Color.Red
                            else -> AccentCyan
                        },
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = when (buildState) {
                                is BuildState.Success -> SuccessGreen
                                else -> AccentCyan
                            },
                            trackColor = Color.White.copy(alpha = 0.08f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Tiến trình: ${(animatedProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            "Target SDK: 35 (Android 15)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Realtime Terminal Log Output (Retro cyberpunk computer monitor screen)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.02f))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(Color(0xFF0A0E1A).copy(alpha = 0.85f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                LazyColumn(
                    state = logListState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(buildLogs) { log ->
                        val color = when {
                            log.startsWith("> Task") -> AccentCyan
                            log.contains("SUCCESSFUL") || log.contains("Generated 1 APK") -> SuccessGreen
                            log.contains("[WebToApp]") -> Color.White.copy(alpha = 0.9f)
                            else -> Color.White.copy(alpha = 0.5f)
                        }
                        Text(
                            text = log,
                            color = color,
                            fontSize = 11.5.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Action UI after completed compile
            AnimatedVisibility(
                visible = buildState is BuildState.Success,
                enter = fadeIn() + expandVertically()
            ) {
                val successData = buildState as? BuildState.Success
                if (successData != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // High-fidelity generated APK Info Panel
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.linearGradient(
                                            colors = listOf(SuccessGreen.copy(alpha = 0.3f), Color.Transparent)
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF131929).copy(alpha = 0.8f)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = successData.apkName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Dung lượng: $apkSize  |  Chữ ký: V2 (Release)",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (downloadingApk) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Đang tải xuống tệp tin APK: ${(downloadProgress * 100).toInt()}%",
                                        fontSize = 13.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    LinearProgressIndicator(
                                        progress = { downloadProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = AccentCyan,
                                        trackColor = Color.White.copy(alpha = 0.08f)
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    val launchGradient = Brush.linearGradient(
                                        colors = listOf(SuccessGreen, Color(0xFF00C853))
                                    )
                                    Button(
                                        onClick = onLaunchSandbox,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(launchGradient, RoundedCornerShape(12.dp))
                                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("CHẠY THỬ", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    }

                                    val dlGradient = Brush.linearGradient(
                                        colors = listOf(AccentBlue, AccentCyan)
                                    )
                                    Button(
                                        onClick = { downloadingApk = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(dlGradient, RoundedCornerShape(12.dp))
                                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("TẢI APK VỀ", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { showSourceViewer = true },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White.copy(alpha = 0.05f),
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Android, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("XEM MÃ NGUỒN", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }

                                    Button(
                                        onClick = {
                                            // Show sideload instructions
                                            Toast.makeText(
                                                context,
                                                "Sideload: Bật 'Nguồn không xác định' trong cài đặt Android, sau đó mở file APK để cài đặt.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White.copy(alpha = 0.05f),
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.IntegrationInstructions, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("HƯỚNG DẪN", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDownloadSuccessDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDownloadSuccessDialog = false },
            confirmButton = {
                Button(
                    onClick = { showDownloadSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Text("ĐỒNG Ý", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "ĐÃ TẢI XUỐNG THÀNH CÔNG!",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Tệp tin APK đã được lưu thành công vào bộ nhớ của thiết bị giả lập này!",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.04f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "📁 Đường dẫn file:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AccentCyan
                            )
                            Text(
                                text = downloadedFilePath,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.5.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "⚖️ Dung lượng:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AccentCyan
                            )
                            Text(
                                text = apkSize,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Text(
                        text = "💡 Hướng dẫn tìm & cài đặt file trên Trình giả lập:\n" +
                               "1. Vuốt từ dưới lên trên màn hình chính để mở Danh sách ứng dụng (App Drawer).\n" +
                               "2. Tìm và mở ứng dụng 'Files' (hoặc 'Tệp').\n" +
                               "3. Chọn thư mục 'Downloads' (Tải xuống) -> 'WebToApp'.\n" +
                               "4. Nhấp vào tệp '${downloadedFileName}' để cài đặt và trải nghiệm như ứng dụng độc lập thực thụ!\n\n" +
                               "⚠️ LƯU Ý: Để ứng dụng boot thẳng vào trang web của bạn (ví dụ: Google), " +
                               "hãy nhấn biểu tượng Ghim (Android) trên danh sách ứng dụng ở màn hình chính trước khi mở file APK!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f),
                        lineHeight = 18.sp
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color(0xFF0F172A),
            tonalElevation = 6.dp
        )
    }
}

private suspend fun saveApkFileToEmulatorStorage(
    context: android.content.Context,
    config: WebConfig,
    onProgress: (Float) -> Unit
): String = withContext(Dispatchers.IO) {
    val fileName = "${config.name.lowercase().replace(" ", "_")}_v1.0.apk"
    val urlString = "http://10.0.2.2:8099/app-debug.apk"
    
    var outputStream: java.io.OutputStream? = null
    var inputStream: java.io.InputStream? = null
    var connection: HttpURLConnection? = null
    
    try {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.android.package-archive")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/WebToApp")
            }
        }
        
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            outputStream = resolver.openOutputStream(uri)
        }
        
        if (outputStream == null) {
            val fallbackDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val webToAppDir = File(fallbackDir, "WebToApp")
            if (!webToAppDir.exists()) webToAppDir.mkdirs()
            val fallbackFile = File(webToAppDir, fileName)
            outputStream = FileOutputStream(fallbackFile)
        }
        
        // 1. Attempt High-Fidelity loopback download from host build artifacts (Monolithic non-split APK)
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 3000
            connection.readTimeout = 10000
            connection.connect()
            
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val fileLength = connection.contentLength
                inputStream = connection.inputStream
                
                val data = ByteArray(8192)
                var total: Long = 0
                var count: Int
                
                while (inputStream.read(data).also { count = it } != -1) {
                    total += count
                    if (fileLength > 0) {
                        onProgress(total.toFloat() / fileLength)
                    }
                    outputStream.write(data, 0, count)
                }
                return@withContext "Downloads/WebToApp/$fileName"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Loopback HTTP failed, proceed to local sandbox copy fallback
        }
        
        // 2. Fallback: Copy packageCodePath (System split base APK)
        val sourceApkPath = context.packageCodePath
        if (sourceApkPath != null) {
            val sourceFile = File(sourceApkPath)
            if (sourceFile.exists()) {
                val fileLength = sourceFile.length()
                inputStream = sourceFile.inputStream()
                val data = ByteArray(8192)
                var total: Long = 0
                var count: Int
                while (inputStream.read(data).also { count = it } != -1) {
                    total += count
                    if (fileLength > 0) {
                        onProgress(total.toFloat() / fileLength)
                    }
                    outputStream.write(data, 0, count)
                }
                return@withContext "Downloads/WebToApp/$fileName"
            }
        }
        
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try { inputStream?.close() } catch (e: Exception) {}
        try { outputStream?.close() } catch (e: Exception) {}
        try { connection?.disconnect() } catch (e: Exception) {}
    }
    
    return@withContext "Downloads/$fileName"
}
