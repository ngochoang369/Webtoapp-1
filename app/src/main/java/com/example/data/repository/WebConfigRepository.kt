package com.example.data.repository

import com.example.data.local.WebConfigDao
import com.example.data.model.WebConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class WebConfigRepository(private val webConfigDao: WebConfigDao) {

    val allConfigs: Flow<List<WebConfig>> = webConfigDao.getAllConfigs()

    fun getConfigById(id: Int): Flow<WebConfig?> = webConfigDao.getConfigById(id)

    suspend fun insert(config: WebConfig): Long = webConfigDao.insertConfig(config)

    suspend fun update(config: WebConfig) = webConfigDao.updateConfig(config)

    suspend fun delete(config: WebConfig) = webConfigDao.deleteConfig(config)

    suspend fun populatePresetsIfNeeded() {
        val currentConfigs = allConfigs.first()
        if (currentConfigs.isEmpty()) {
            val presets = listOf(
                WebConfig(
                    name = "Google",
                    url = "https://www.google.com",
                    primaryColorHex = "#4285F4",
                    iconName = "search",
                    iconColorHex = "#EA4335",
                    enableSwipeToRefresh = true,
                    enableProgressBar = true,
                    enablePushNotifications = false,
                    splashText = "Google Search Engine",
                    splashDurationMs = 1500,
                    userAgentType = "MOBILE",
                    packageName = "com.google.android.search"
                ),
                WebConfig(
                    name = "Facebook Lite",
                    url = "https://m.facebook.com",
                    primaryColorHex = "#1877F2",
                    iconName = "public",
                    iconColorHex = "#1877F2",
                    enableSwipeToRefresh = true,
                    enableProgressBar = true,
                    enablePushNotifications = true,
                    splashText = "Facebook Mobile Wrapper",
                    splashDurationMs = 2000,
                    userAgentType = "MOBILE",
                    customCss = "/* Custom Mobile Layout cleanups */",
                    packageName = "com.facebook.lite.wrapper"
                ),
                WebConfig(
                    name = "Wikipedia",
                    url = "https://vi.m.wikipedia.org",
                    primaryColorHex = "#333333",
                    iconName = "menu_book",
                    iconColorHex = "#666666",
                    enableSwipeToRefresh = true,
                    enableProgressBar = true,
                    enablePushNotifications = false,
                    splashText = "Wikipedia - Bách khoa toàn thư mở",
                    splashDurationMs = 2000,
                    userAgentType = "MOBILE",
                    packageName = "org.wikipedia.mobile.vi"
                ),
                WebConfig(
                    name = "AI Studio Build",
                    url = "https://ai.studio/build",
                    primaryColorHex = "#00E676",
                    iconName = "code",
                    iconColorHex = "#00E676",
                    enableSwipeToRefresh = true,
                    enableProgressBar = true,
                    enablePushNotifications = true,
                    splashText = "AI Studio Build Console",
                    splashDurationMs = 2500,
                    userAgentType = "DESKTOP",
                    packageName = "com.aistudio.build.console"
                )
            )
            for (preset in presets) {
                webConfigDao.insertConfig(preset)
            }
        }
    }
}
