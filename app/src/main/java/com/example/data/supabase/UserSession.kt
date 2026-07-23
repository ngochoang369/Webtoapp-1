package com.example.data.supabase

data class UserSession(
    val userId: String,
    val email: String,
    val fullName: String = "",
    val accessToken: String = "",
    val isLoggedIn: Boolean = false,
    val isOfflineMode: Boolean = false
) {
    val isAdmin: Boolean
        get() = email.lowercase().trim() == ADMIN_EMAIL.lowercase()

    companion object {
        const val ADMIN_EMAIL = "devregish@gmail.com"

        fun guestUser() = UserSession(
            userId = "guest_user_local",
            email = "user@privadiary.app",
            fullName = "Người dùng Nhật ký",
            isLoggedIn = true,
            isOfflineMode = true
        )

        fun adminDemoSession() = UserSession(
            userId = "admin_devregish_id",
            email = ADMIN_EMAIL,
            fullName = "DevRegish Admin Audit",
            accessToken = "demo_admin_jwt_token",
            isLoggedIn = true,
            isOfflineMode = false
        )
    }
}
