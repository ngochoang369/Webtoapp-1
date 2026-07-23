package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AuditLogEntity
import com.example.data.model.DiaryModel
import com.example.data.repository.DiaryRepository
import com.example.data.supabase.UserSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: DiaryRepository,
    private val getSession: () -> UserSession?
) : ViewModel() {

    val adminEmail: String = UserSession.ADMIN_EMAIL

    val allDiariesForAdmin: StateFlow<List<DiaryModel>> = repository.getAllDiariesForAdmin()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.getAllAuditLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun logAuditAction(targetUserEmail: String, actionType: String, reason: String, entryId: String = "") {
        viewModelScope.launch {
            repository.logAdminAuditAction(
                adminEmail = adminEmail,
                targetUserEmail = targetUserEmail,
                actionType = actionType,
                reason = reason,
                entryId = entryId
            )
        }
    }
}
