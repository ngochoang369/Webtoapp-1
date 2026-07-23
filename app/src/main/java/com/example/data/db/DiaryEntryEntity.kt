package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntryEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val userEmail: String,
    val titleEncrypted: String,
    val contentEncrypted: String,
    val mood: String,
    val tags: String,
    val weather: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isE2eeEncrypted: Boolean = true,
    val allowAdminAudit: Boolean = true,
    val syncedToSupabase: Boolean = false
)
