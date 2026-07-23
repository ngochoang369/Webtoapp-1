package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.AppDatabase
import com.example.data.model.DiaryModel
import com.example.data.repository.DiaryRepository
import com.example.data.supabase.SupabaseClient
import com.example.data.supabase.UserSession
import com.example.security.VaultSecurityManager
import com.example.ui.screens.AdminAuditScreen
import com.example.ui.screens.DiaryListScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.NewDiaryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VaultLockScreen
import com.example.ui.theme.PrivaDiaryTheme
import com.example.ui.theme.VaultDarkBg
import com.example.ui.theme.VaultPrimary
import com.example.ui.theme.VaultSurfaceDark
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.DiaryViewModel
import com.example.ui.viewmodel.VaultViewModel

class MainActivity : ComponentActivity() {

    private lateinit var vaultSecurityManager: VaultSecurityManager
    private lateinit var supabaseClient: SupabaseClient
    private lateinit var diaryRepository: DiaryRepository

    private lateinit var authViewModel: AuthViewModel
    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var adminViewModel: AdminViewModel
    private lateinit var vaultViewModel: VaultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize DB and Dependencies
        val db = AppDatabase.getDatabase(applicationContext)
        vaultSecurityManager = VaultSecurityManager(applicationContext)
        supabaseClient = SupabaseClient(vaultSecurityManager)
        diaryRepository = DiaryRepository(db.diaryDao(), db.auditLogDao())

        authViewModel = AuthViewModel(supabaseClient)
        diaryViewModel = DiaryViewModel(diaryRepository) { authViewModel.session.value }
        adminViewModel = AdminViewModel(diaryRepository) { authViewModel.session.value }
        vaultViewModel = VaultViewModel(vaultSecurityManager)

        setContent {
            PrivaDiaryTheme(darkTheme = true) {
                MainAppHost(
                    authViewModel = authViewModel,
                    diaryViewModel = diaryViewModel,
                    adminViewModel = adminViewModel,
                    vaultViewModel = vaultViewModel
                )
            }
        }
    }
}

enum class MainTab {
    DIARY_LIST,
    NEW_DIARY,
    ADMIN_AUDIT,
    SETTINGS
}

@Composable
fun MainAppHost(
    authViewModel: AuthViewModel,
    diaryViewModel: DiaryViewModel,
    adminViewModel: AdminViewModel,
    vaultViewModel: VaultViewModel
) {
    val session by authViewModel.session.collectAsState()
    val isVaultLocked by vaultViewModel.isLocked.collectAsState()

    var activeTab by remember { mutableStateOf(MainTab.DIARY_LIST) }
    var editingDiary by remember { mutableStateOf<DiaryModel?>(null) }
    var isSettingNewPinFlow by remember { mutableStateOf(false) }

    // Check Vault Lock State first
    if (isVaultLocked || isSettingNewPinFlow) {
        VaultLockScreen(
            isSettingNewPin = isSettingNewPinFlow,
            onPinSuccess = {
                if (isSettingNewPinFlow) {
                    isSettingNewPinFlow = false
                } else {
                    vaultViewModel.verifyPin("")
                }
            },
            onSetNewPin = { newPin ->
                vaultViewModel.setPin(newPin)
                isSettingNewPinFlow = false
            }
        )
        return
    }

    // Check Authentication State
    if (session == null || !session!!.isLoggedIn) {
        LoginScreen(
            authViewModel = authViewModel
        )
        return
    }

    // Main Authenticated Application View with Bottom Navigation
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_app_host"),
        containerColor = VaultDarkBg,
        bottomBar = {
            NavigationBar(
                containerColor = VaultSurfaceDark,
                contentColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Tab 1: Nhật ký
                NavigationBarItem(
                    selected = activeTab == MainTab.DIARY_LIST,
                    onClick = {
                        editingDiary = null
                        activeTab = MainTab.DIARY_LIST
                    },
                    icon = { Icon(Icons.Default.Book, contentDescription = "Nhật ký") },
                    label = { Text("Nhật Ký", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = VaultPrimary,
                        indicatorColor = VaultPrimary,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("tab_diary_list")
                )

                // Tab 2: Viết mới
                NavigationBarItem(
                    selected = activeTab == MainTab.NEW_DIARY,
                    onClick = {
                        editingDiary = null
                        activeTab = MainTab.NEW_DIARY
                    },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Viết mới") },
                    label = { Text("Viết Bài", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = VaultPrimary,
                        indicatorColor = VaultPrimary,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("tab_new_diary")
                )

                // Tab 3: Admin Audit (Visible to devregish@gmail.com or admin mode)
                if (session?.isAdmin == true) {
                    NavigationBarItem(
                        selected = activeTab == MainTab.ADMIN_AUDIT,
                        onClick = { activeTab = MainTab.ADMIN_AUDIT },
                        icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                        label = { Text("Admin Audit", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color(0xFFA78BFA),
                            indicatorColor = Color(0xFF6D28D9),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("tab_admin_audit")
                    )
                }

                // Tab 4: Cài đặt
                NavigationBarItem(
                    selected = activeTab == MainTab.SETTINGS,
                    onClick = { activeTab = MainTab.SETTINGS },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Cài đặt") },
                    label = { Text("Cài Đặt", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = VaultPrimary,
                        indicatorColor = VaultPrimary,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("tab_settings")
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (activeTab) {
                MainTab.DIARY_LIST -> {
                    DiaryListScreen(
                        diaryViewModel = diaryViewModel,
                        session = session,
                        onAddNewClick = {
                            editingDiary = null
                            activeTab = MainTab.NEW_DIARY
                        },
                        onEditClick = { diary ->
                            editingDiary = diary
                            activeTab = MainTab.NEW_DIARY
                        }
                    )
                }

                MainTab.NEW_DIARY -> {
                    NewDiaryScreen(
                        diaryViewModel = diaryViewModel,
                        editingDiary = editingDiary,
                        onBackClick = {
                            editingDiary = null
                            activeTab = MainTab.DIARY_LIST
                        }
                    )
                }

                MainTab.ADMIN_AUDIT -> {
                    AdminAuditScreen(adminViewModel = adminViewModel)
                }

                MainTab.SETTINGS -> {
                    SettingsScreen(
                        authViewModel = authViewModel,
                        vaultViewModel = vaultViewModel,
                        session = session,
                        onSetNewPinRequested = {
                            isSettingNewPinFlow = true
                        }
                    )
                }
            }
        }
    }
}
