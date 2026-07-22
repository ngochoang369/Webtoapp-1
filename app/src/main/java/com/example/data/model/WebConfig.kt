package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "web_configs")
data class WebConfig(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val url: String,
    val primaryColorHex: String = "#2196F3",
    val secondaryColorHex: String = "#0D47A1",
    val iconName: String = "language", // Default globe icon
    val iconColorHex: String = "#2196F3",
    val enableSwipeToRefresh: Boolean = true,
    val enableProgressBar: Boolean = true,
    val enablePushNotifications: Boolean = true,
    val splashText: String = "Đang tải...",
    val splashDurationMs: Int = 2000,
    val userAgentType: String = "MOBILE", // MOBILE, DESKTOP, CUSTOM
    val customUserAgent: String = "",
    val customCss: String = "",
    val customJs: String = "",
    val isFullscreen: Boolean = false,
    val enableZoom: Boolean = false,
    val packageName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
