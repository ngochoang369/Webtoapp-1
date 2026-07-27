package com.example.security

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import com.example.data.supabase.UserSession

class VaultSecurityManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("privadiary_vault_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PIN_HASH = "key_pin_hash"
        private const val KEY_IS_PIN_ENABLED = "key_is_pin_enabled"
        private const val KEY_BIOMETRIC_ENABLED = "key_biometric_enabled"
        private const val KEY_ALLOW_ADMIN_AUDIT_DEFAULT = "key_allow_admin_audit_default"
        private const val KEY_SUPABASE_URL = "key_supabase_url"
        private const val KEY_SUPABASE_ANON_KEY = "key_supabase_anon_key"
        private const val DEFAULT_SUPABASE_URL = "https://xrkhhfbwcloinvykrvuc.supabase.co"
        private const val DEFAULT_SUPABASE_ANON_KEY = "sb_publishable_9bNaVsNEt0b9uaObGPMWzw_0QdbBN-S"

        private const val KEY_SESSION_USER_ID = "key_session_user_id"
        private const val KEY_SESSION_EMAIL = "key_session_email"
        private const val KEY_SESSION_FULL_NAME = "key_session_full_name"
        private const val KEY_SESSION_ACCESS_TOKEN = "key_session_access_token"
        private const val KEY_SESSION_IS_LOGGED_IN = "key_session_is_logged_in"
        private const val KEY_SESSION_IS_OFFLINE_MODE = "key_session_is_offline_mode"
    }

    var isVaultLocked: Boolean = isPinEnabled()

    fun isPinEnabled(): Boolean {
        return prefs.getBoolean(KEY_IS_PIN_ENABLED, false) && getPinHash().isNotEmpty()
    }

    fun getPinHash(): String {
        return prefs.getString(KEY_PIN_HASH, "") ?: ""
    }

    fun setPin(pin: String) {
        val hashed = CryptoManager.hashPin(pin)
        prefs.edit()
            .putString(KEY_PIN_HASH, hashed)
            .putBoolean(KEY_IS_PIN_ENABLED, true)
            .apply()
        isVaultLocked = false
    }

    fun verifyPin(inputPin: String): Boolean {
        val hashedInput = CryptoManager.hashPin(inputPin)
        val storedHash = getPinHash()
        val isValid = hashedInput == storedHash
        if (isValid) {
            isVaultLocked = false
        }
        return isValid
    }

    fun disablePin() {
        prefs.edit()
            .remove(KEY_PIN_HASH)
            .putBoolean(KEY_IS_PIN_ENABLED, false)
            .apply()
        isVaultLocked = false
    }

    fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, true)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun isAllowAdminAuditDefault(): Boolean {
        return prefs.getBoolean(KEY_ALLOW_ADMIN_AUDIT_DEFAULT, true)
    }

    fun setAllowAdminAuditDefault(allowed: Boolean) {
        prefs.edit().putBoolean(KEY_ALLOW_ADMIN_AUDIT_DEFAULT, allowed).apply()
    }

    fun getSupabaseUrl(): String {
        val buildConfigUrl = try { BuildConfig.SUPABASE_URL } catch (e: Throwable) { "" }
        if (buildConfigUrl.isNotBlank()) return buildConfigUrl
        return prefs.getString(KEY_SUPABASE_URL, DEFAULT_SUPABASE_URL) ?: DEFAULT_SUPABASE_URL
    }

    fun getSupabaseAnonKey(): String {
        val buildConfigKey = try { BuildConfig.SUPABASE_ANON_KEY } catch (e: Throwable) { "" }
        if (buildConfigKey.isNotBlank()) return buildConfigKey
        return prefs.getString(KEY_SUPABASE_ANON_KEY, DEFAULT_SUPABASE_ANON_KEY) ?: DEFAULT_SUPABASE_ANON_KEY
    }

    fun saveSupabaseConfig(url: String, key: String) {
        prefs.edit()
            .putString(KEY_SUPABASE_URL, url.ifBlank { DEFAULT_SUPABASE_URL })
            .putString(KEY_SUPABASE_ANON_KEY, key.ifBlank { DEFAULT_SUPABASE_ANON_KEY })
            .apply()
    }

    fun saveUserSession(session: UserSession) {
        prefs.edit()
            .putString(KEY_SESSION_USER_ID, session.userId)
            .putString(KEY_SESSION_EMAIL, session.email)
            .putString(KEY_SESSION_FULL_NAME, session.fullName)
            .putString(KEY_SESSION_ACCESS_TOKEN, session.accessToken)
            .putBoolean(KEY_SESSION_IS_LOGGED_IN, session.isLoggedIn)
            .putBoolean(KEY_SESSION_IS_OFFLINE_MODE, session.isOfflineMode)
            .apply()
    }

    fun getUserSession(): UserSession? {
        val isLoggedIn = prefs.getBoolean(KEY_SESSION_IS_LOGGED_IN, false)
        if (!isLoggedIn) return null
        val email = prefs.getString(KEY_SESSION_EMAIL, "") ?: ""
        if (email.isBlank()) return null

        return UserSession(
            userId = prefs.getString(KEY_SESSION_USER_ID, "usr_${email.hashCode()}") ?: "usr_${email.hashCode()}",
            email = email,
            fullName = prefs.getString(KEY_SESSION_FULL_NAME, email.substringBefore("@")) ?: email.substringBefore("@"),
            accessToken = prefs.getString(KEY_SESSION_ACCESS_TOKEN, "") ?: "",
            isLoggedIn = true,
            isOfflineMode = prefs.getBoolean(KEY_SESSION_IS_OFFLINE_MODE, false)
        )
    }

    fun clearUserSession() {
        prefs.edit()
            .remove(KEY_SESSION_USER_ID)
            .remove(KEY_SESSION_EMAIL)
            .remove(KEY_SESSION_FULL_NAME)
            .remove(KEY_SESSION_ACCESS_TOKEN)
            .remove(KEY_SESSION_IS_LOGGED_IN)
            .remove(KEY_SESSION_IS_OFFLINE_MODE)
            .apply()
    }
}
