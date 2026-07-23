package com.example.data.model

data class DiaryModel(
    val id: String,
    val userId: String,
    val userEmail: String,
    val title: String,
    val content: String,
    val mood: String,
    val tags: List<String>,
    val weather: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isE2eeEncrypted: Boolean = true,
    val allowAdminAudit: Boolean = true,
    val rawTitleEncrypted: String = "",
    val rawContentEncrypted: String = ""
)
