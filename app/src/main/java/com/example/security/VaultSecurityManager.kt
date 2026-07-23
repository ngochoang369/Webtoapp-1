package com.example.security

import android.content.Context
import android.content.SharedPreferences

class VaultSecurityManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("privadiary_vault_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PIN_HASH = "key_pin_hash"
        private const val KEY_IS_PIN_ENABLED = "key_is_pin_enabled"
        private const val KEY_BIOMETRIC_ENABLED = "key_biometric_enabled"
        private const val KEY_ALLOW_ADMIN_AUDIT_DEFAULT = "key_allow_admin_audit_default"
        private const val KEY_SUPABASE_URL = "key_supabase_url"
        private const val KEY_SUPABASE_ANON_KEY = "key_supabase_anon_key"
        private const val DEFAULT_SUPABASE_URL = "https://aistudio-privadiary-db.supabase.co"
        private const val DEFAULT_SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJvbGUiOiJhb24ifQ.demo"
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
        return prefs.getString(KEY_SUPABASE_URL, DEFAULT_SUPABASE_URL) ?: DEFAULT_SUPABASE_URL
    }

    fun getSupabaseAnonKey(): String {
        return prefs.getString(KEY_SUPABASE_ANON_KEY, DEFAULT_SUPABASE_ANON_KEY) ?: DEFAULT_SUPABASE_ANON_KEY
    }

    fun saveSupabaseConfig(url: String, key: String) {
        prefs.edit()
            .putString(KEY_SUPABASE_URL, url.ifBlank { DEFAULT_SUPABASE_URL })
            .putString(KEY_SUPABASE_ANON_KEY, key.ifBlank { DEFAULT_SUPABASE_ANON_KEY })
            .apply()
    }
}
