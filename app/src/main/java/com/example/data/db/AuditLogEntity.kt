package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey
    val id: String,
    val timestamp: Long,
    val adminEmail: String,
    val targetUserEmail: String,
    val actionType: String, // e.g. "VIEW_JOURNAL_AUDIT", "POLICY_VERIFICATION", "EXPORT_SECURITY_BACKUP"
    val reason: String,
    val entryId: String = ""
)
