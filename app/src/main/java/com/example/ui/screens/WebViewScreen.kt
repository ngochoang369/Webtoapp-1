package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.WebConfig
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun WebViewScreen(
    config: WebConfig,
    isStandaloneMode: Boolean = false,
    onExitStandalone: () -> Unit = {},
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var progress by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var showSplash by remember { mutableStateOf(true) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }

    // Parse primary color
    val parsedPrimaryColor = remember(config.primaryColorHex) {
        try {
            Color(android.graphics.Color.parseColor(config.primaryColorHex))
        } catch (e: Exception) {
            Color(0xFF2196F3)
        }
    }

    // Handle splash timeout
    LaunchedEffect(Unit) {
        delay(config.splashDurationMs.toLong())
        showSplash = false
    }

    // Handle back press inside the WebView
    BackHandler(enabled = canGoBack) {
        webViewRef?.goBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Native Custom Header / App Bar (resembles a premium browser-app hybrid)
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = config.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = config.url.replace("https://", "").replace("http://", ""),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    if (isStandaloneMode) {
                        IconButton(onClick = onExitStandalone) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Trình quản lý",
                                tint = Color.White
                            )
                        }
                    } else {
                        IconButton(onClick = onClose) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Thoát",
                                tint = Color.White
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { webViewRef?.reload() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Tải lại",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = parsedPrimaryColor
                )
            )

            // Linear Progress Indicator
            if (config.enableProgressBar && isLoading && !showSplash) {
                LinearProgressIndicator(
                    progress = { progress.toFloat() / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = parsedPrimaryColor,
                    trackColor = parsedPrimaryColor.copy(alpha = 0.2f)
                )
            }

            // WebView integration
            Box(modifier = Modifier.weight(1f)) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            
                            // WebSettings configuration
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                databaseEnabled = true
                                useWideViewPort = true
                                loadWithOverviewMode = true
                                builtInZoomControls = config.enableZoom
                                displayZoomControls = false
                                allowFileAccess = true
                                mediaPlaybackRequiresUserGesture = false
                                cacheMode = WebSettings.LOAD_DEFAULT
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            }

                            // User-Agent customizer
                            val originalUA = settings.userAgentString
                            settings.userAgentString = when (config.userAgentType) {
                                "DESKTOP" -> "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                                "CUSTOM" -> if (config.customUserAgent.isNotEmpty()) config.customUserAgent else originalUA
                                else -> "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    isLoading = true
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                    canGoBack = view?.canGoBack() == true
                                    canGoForward = view?.canGoForward() == true

                                    // Inject custom CSS styling
                                    if (config.customCss.isNotEmpty()) {
                                        val cssMinified = config.customCss.replace("\n", "").replace("'", "\\'")
                                        val cssInjectScript = """
                                            (function() {
                                                var parent = document.getElementsByTagName('head').item(0);
                                                var style = document.createElement('style');
                                                style.type = 'text/css';
                                                style.innerHTML = '$cssMinified';
                                                parent.appendChild(style);
                                            })()
                                        """.trimIndent()
                                        view?.evaluateJavascript(cssInjectScript, null)
                                    }

                                    // Inject custom JS scripts
                                    if (config.customJs.isNotEmpty()) {
                                        view?.evaluateJavascript(config.customJs, null)
                                    }
                                }
                            }

                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    super.onProgressChanged(view, newProgress)
                                    progress = newProgress
                                }
                            }

                            loadUrl(config.url)
                            webViewRef = this
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { view ->
                        webViewRef = view
                    }
                )
            }

            // High fidelity bottom control bar (prevents dead ends, mimics full browser app)
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { webViewRef?.goBack() },
                        enabled = canGoBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Trở về",
                            tint = if (canGoBack) parsedPrimaryColor else Color.Gray.copy(alpha = 0.5f)
                        )
                    }

                    IconButton(
                        onClick = { webViewRef?.goForward() },
                        enabled = canGoForward
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Tiến tới",
                            tint = if (canGoForward) parsedPrimaryColor else Color.Gray.copy(alpha = 0.5f)
                        )
                    }

                    IconButton(
                        onClick = { webViewRef?.loadUrl(config.url) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Trang chủ",
                            tint = parsedPrimaryColor
                        )
                    }

                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, webViewRef?.url ?: config.url)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ liên kết"))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Chia sẻ",
                            tint = parsedPrimaryColor
                        )
                    }
                }
            }
        }

        // Custom Splash Screen Overlay (animates beautifully to match user choices)
        AnimatedVisibility(
            visible = showSplash,
            exit = fadeOut(animationSpec = tween(durationMillis = 600))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(parsedPrimaryColor),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App letter emblem in splash
                    Surface(
                        modifier = Modifier.size(96.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = config.name.take(1).uppercase(),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Text(
                        text = config.name,
                        modifier = Modifier.padding(top = 24.dp),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = config.splashText,
                        modifier = Modifier.padding(top = 12.dp, start = 32.dp, end = 32.dp),
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )

                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 48.dp)
                            .size(32.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                }
            }
        }
    }
}
