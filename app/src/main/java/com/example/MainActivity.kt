package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.WebConfig
import com.example.ui.screens.BuildScreen
import com.example.ui.screens.ConfigScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.WebViewScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.WebConfigViewModel

sealed interface AppScreen {
    object Home : AppScreen
    data class Config(val config: WebConfig?) : AppScreen
    data class Build(val config: WebConfig) : AppScreen
    data class WebView(val config: WebConfig) : AppScreen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ensureWebViewCacheDirs()
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: WebConfigViewModel = viewModel()
                val context = LocalContext.current
                val configs by viewModel.allConfigs.collectAsState()
                
                val sharedPrefs = remember { context.getSharedPreferences("webtoapp_prefs", Context.MODE_PRIVATE) }
                var standaloneConfigId by remember { mutableStateOf(sharedPrefs.getInt("standalone_config_id", -1)) }
                
                var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }

                // Auto-route on app startup if single app mode is locked
                LaunchedEffect(configs, standaloneConfigId) {
                    if (standaloneConfigId != -1 && configs.isNotEmpty() && currentScreen == AppScreen.Home) {
                        val target = configs.find { it.id == standaloneConfigId }
                        if (target != null) {
                            currentScreen = AppScreen.WebView(target)
                        }
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) togetherWith
                            fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
                        },
                        label = "ScreenTransition"
                    ) { screen ->
                        when (screen) {
                            is AppScreen.Home -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onCreateNew = { currentScreen = AppScreen.Config(null) },
                                    onEditConfig = { config -> currentScreen = AppScreen.Config(config) },
                                    onBuildConfig = { config -> currentScreen = AppScreen.Build(config) },
                                    onLaunchSandbox = { config -> currentScreen = AppScreen.WebView(config) },
                                    standaloneConfigId = standaloneConfigId,
                                    onToggleStandalone = { config ->
                                        val newId = if (standaloneConfigId == config.id) -1 else config.id
                                        sharedPrefs.edit().putInt("standalone_config_id", newId).apply()
                                        standaloneConfigId = newId
                                        if (newId != -1) {
                                            android.widget.Toast.makeText(
                                                context,
                                                "Đã ghim ${config.name} làm App Độc lập mặc định!",
                                                android.widget.Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            android.widget.Toast.makeText(
                                                context,
                                                "Đã tắt Chế độ Độc lập. Quay lại Console!",
                                                android.widget.Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                )
                            }
                            is AppScreen.Config -> {
                                ConfigScreen(
                                    config = screen.config,
                                    viewModel = viewModel,
                                    onBack = { currentScreen = AppScreen.Home }
                                )
                            }
                            is AppScreen.Build -> {
                                BuildScreen(
                                    config = screen.config,
                                    viewModel = viewModel,
                                    onLaunchSandbox = { currentScreen = AppScreen.WebView(screen.config) },
                                    onClose = {
                                        viewModel.resetBuild()
                                        currentScreen = AppScreen.Home
                                    }
                                )
                            }
                            is AppScreen.WebView -> {
                                WebViewScreen(
                                    config = screen.config,
                                    isStandaloneMode = (screen.config.id == standaloneConfigId),
                                    onExitStandalone = {
                                        sharedPrefs.edit().putInt("standalone_config_id", -1).apply()
                                        standaloneConfigId = -1
                                        currentScreen = AppScreen.Home
                                    },
                                    onClose = { currentScreen = AppScreen.Home }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun ensureWebViewCacheDirs() {
        try {
            val cacheDir = cacheDir
            val jsDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/js")
            val wasmDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/wasm")
            if (!jsDir.exists()) {
                jsDir.mkdirs()
            }
            if (!wasmDir.exists()) {
                wasmDir.mkdirs()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
