package com.example.ui.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MainActivity
import com.example.data.local.AppDatabase
import com.example.data.model.WebConfig
import com.example.data.repository.WebConfigRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface BuildState {
    object Idle : BuildState
    object Building : BuildState
    data class Success(val apkName: String, val config: WebConfig) : BuildState
    data class Failed(val error: String) : BuildState
}

class WebConfigViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WebConfigRepository
    val allConfigs: StateFlow<List<WebConfig>>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = WebConfigRepository(db.webConfigDao())
        
        allConfigs = repository.allConfigs
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        // Pre-populate database with default templates if empty
        viewModelScope.launch {
            repository.populatePresetsIfNeeded()
        }
    }

    private val _selectedConfig = MutableStateFlow<WebConfig?>(null)
    val selectedConfig: StateFlow<WebConfig?> = _selectedConfig.asStateFlow()

    private val _buildState = MutableStateFlow<BuildState>(BuildState.Idle)
    val buildState: StateFlow<BuildState> = _buildState.asStateFlow()

    private val _buildProgress = MutableStateFlow(0f)
    val buildProgress: StateFlow<Float> = _buildProgress.asStateFlow()

    private val _buildLogs = MutableStateFlow<List<String>>(emptyList())
    val buildLogs: StateFlow<List<String>> = _buildLogs.asStateFlow()

    fun selectConfig(config: WebConfig?) {
        _selectedConfig.value = config
    }

    fun saveConfig(config: WebConfig, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (config.id == 0) {
                repository.insert(config)
            } else {
                repository.update(config)
            }
            onSuccess()
        }
    }

    fun deleteConfig(config: WebConfig) {
        viewModelScope.launch {
            repository.delete(config)
            if (_selectedConfig.value?.id == config.id) {
                _selectedConfig.value = null
            }
        }
    }

    // Interactive APK compiler sequence simulation
    fun startApkBuild(config: WebConfig) {
        viewModelScope.launch {
            _buildState.value = BuildState.Building
            _buildProgress.value = 0f
            _buildLogs.value = emptyList()

            val pName = config.packageName.ifEmpty { "com.webtoapp." + config.name.lowercase().replace("[^a-z0-9]".toRegex(), "") }

            val steps = listOf(
                "Initializing Gradle daemon...",
                "Configuring project structure with Package Name: $pName...",
                "Resolving remote dependencies...",
                "Validating Web URL (${config.url}) and AndroidManifest.xml...",
                "Processing assets & launcher icons...",
                "Compiling Kotlin source files package $pName...",
                "Running Kotlin Symbol Processing (KSP) and dependency mapping...",
                "Injecting Custom CSS & Custom UserAgent configurations...",
                "Applying Proguard/R8 optimization rules on Application ID: $pName...",
                "Packaging APK resources (AndroidManifest, strings.xml, custom styles)...",
                "Signing APK with generated release key for $pName...",
                "Zipaligning final APK archive..."
            )

            for (i in steps.indices) {
                val step = steps[i]
                addLog("> Task :app:${step.substringBefore(" ").lowercase().replace(":", "")}")
                addLog("  [WebToApp] $step")
                
                // Simulate variable time for tasks
                val delayTime = when (i) {
                    2, 5, 8 -> 800L // dependencies, compiling, proguard take longer
                    else -> 400L
                }
                delay(delayTime)
                _buildProgress.value = (i + 1).toFloat() / steps.size
            }

            addLog("")
            addLog("BUILD SUCCESSFUL in ${String.format("%.2f", 5.0 + Math.random())}s")
            addLog("Generated 1 standalone APK file with Application ID: $pName")
            addLog("File: ${config.name.lowercase().replace(" ", "_")}_release.apk (${String.format("%.1f", 3.2 + Math.random() * 2.1)} MB)")
            
            _buildState.value = BuildState.Success(
                apkName = "${config.name.lowercase().replace(" ", "_")}_v1.0.apk",
                config = config
            )
        }
    }

    private fun addLog(message: String) {
        _buildLogs.value = _buildLogs.value + message
    }

    fun resetBuild() {
        _buildState.value = BuildState.Idle
        _buildProgress.value = 0f
        _buildLogs.value = emptyList()
    }

    // Trigger local push notification test
    fun sendLocalTestPushNotification(config: WebConfig, title: String, content: String) {
        val context = getApplication<Application>().applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "webtoapp_push_test"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Push Notifications từ WebToApp",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kênh thông báo giả lập cho ứng dụng chuyển đổi"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            config.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("[${config.name}] $title")
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(config.id, builder.build())
    }
}
