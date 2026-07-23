package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.security.VaultSecurityManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VaultViewModel(private val vaultSecurityManager: VaultSecurityManager) : ViewModel() {

    private val _isLocked = MutableStateFlow(vaultSecurityManager.isVaultLocked)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private val _isPinSet = MutableStateFlow(vaultSecurityManager.isPinEnabled())
    val isPinSet: StateFlow<Boolean> = _isPinSet.asStateFlow()

    private val _allowAdminAuditDefault = MutableStateFlow(vaultSecurityManager.isAllowAdminAuditDefault())
    val allowAdminAuditDefault: StateFlow<Boolean> = _allowAdminAuditDefault.asStateFlow()

    fun verifyPin(pin: String): Boolean {
        val success = vaultSecurityManager.verifyPin(pin)
        if (success) {
            _isLocked.value = false
        }
        return success
    }

    fun setPin(pin: String) {
        vaultSecurityManager.setPin(pin)
        _isPinSet.value = true
        _isLocked.value = false
    }

    fun disablePin() {
        vaultSecurityManager.disablePin()
        _isPinSet.value = false
        _isLocked.value = false
    }

    fun setAllowAdminAuditDefault(allowed: Boolean) {
        vaultSecurityManager.setAllowAdminAuditDefault(allowed)
        _allowAdminAuditDefault.value = allowed
    }

    fun lockVault() {
        if (vaultSecurityManager.isPinEnabled()) {
            vaultSecurityManager.isVaultLocked = true
            _isLocked.value = true
        }
    }

    fun getSupabaseUrl(): String = vaultSecurityManager.getSupabaseUrl()
    fun getSupabaseAnonKey(): String = vaultSecurityManager.getSupabaseAnonKey()

    fun saveSupabaseConfig(url: String, key: String) {
        vaultSecurityManager.saveSupabaseConfig(url, key)
    }
}
