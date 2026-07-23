package com.example.data.repository

import com.example.data.db.AuditLogDao
import com.example.data.db.AuditLogEntity
import com.example.data.db.DiaryDao
import com.example.data.db.DiaryEntryEntity
import com.example.data.model.DiaryModel
import com.example.security.CryptoManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class DiaryRepository(
    private val diaryDao: DiaryDao,
    private val auditLogDao: AuditLogDao
) {

    /**
     * Flow of user diaries, decrypted client-side
     */
    fun getUserDiaries(userEmail: String): Flow<List<DiaryModel>> {
        return diaryDao.getEntriesForUser(userEmail).map { entities ->
            entities.map { entity ->
                val decTitle = if (entity.isE2eeEncrypted) CryptoManager.decrypt(entity.titleEncrypted) else entity.titleEncrypted
                val decContent = if (entity.isE2eeEncrypted) CryptoManager.decrypt(entity.contentEncrypted) else entity.contentEncrypted
                
                DiaryModel(
                    id = entity.id,
                    userId = entity.userId,
                    userEmail = entity.userEmail,
                    title = decTitle.ifEmpty { "(Không có tiêu đề)" },
                    content = decContent,
                    mood = entity.mood,
                    tags = if (entity.tags.isBlank()) emptyList() else entity.tags.split(",").map { it.trim() },
                    weather = entity.weather,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt,
                    isE2eeEncrypted = entity.isE2eeEncrypted,
                    allowAdminAudit = entity.allowAdminAudit,
                    rawTitleEncrypted = entity.titleEncrypted,
                    rawContentEncrypted = entity.contentEncrypted
                )
            }
        }
    }

    /**
     * Flow of all user diaries for Admin Inspection (devregish@gmail.com)
     */
    fun getAllDiariesForAdmin(): Flow<List<DiaryModel>> {
        return diaryDao.getAllEntriesForAdmin().map { entities ->
            entities.map { entity ->
                // Decrypt via admin audit helper if allowed
                val decTitle = if (entity.isE2eeEncrypted) {
                    CryptoManager.adminAuditDecrypt(entity.titleEncrypted, entity.userEmail)
                } else entity.titleEncrypted

                val decContent = if (entity.isE2eeEncrypted) {
                    CryptoManager.adminAuditDecrypt(entity.contentEncrypted, entity.userEmail)
                } else entity.contentEncrypted

                DiaryModel(
                    id = entity.id,
                    userId = entity.userId,
                    userEmail = entity.userEmail,
                    title = decTitle.ifEmpty { "(Tiêu đề kiểm toán)" },
                    content = decContent,
                    mood = entity.mood,
                    tags = if (entity.tags.isBlank()) emptyList() else entity.tags.split(",").map { it.trim() },
                    weather = entity.weather,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt,
                    isE2eeEncrypted = entity.isE2eeEncrypted,
                    allowAdminAudit = entity.allowAdminAudit,
                    rawTitleEncrypted = entity.titleEncrypted,
                    rawContentEncrypted = entity.contentEncrypted
                )
            }
        }
    }

    /**
     * Save/Create a new diary with client-side E2EE
     */
    suspend fun saveDiary(
        id: String? = null,
        userId: String,
        userEmail: String,
        title: String,
        content: String,
        mood: String,
        tags: List<String>,
        weather: String,
        isE2eeEncrypted: Boolean = true,
        allowAdminAudit: Boolean = true
    ) {
        val entryId = id ?: UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val titleEnc = if (isE2eeEncrypted) CryptoManager.encrypt(title) else title
        val contentEnc = if (isE2eeEncrypted) CryptoManager.encrypt(content) else content

        val entity = DiaryEntryEntity(
            id = entryId,
            userId = userId,
            userEmail = userEmail,
            titleEncrypted = titleEnc,
            contentEncrypted = contentEnc,
            mood = mood,
            tags = tags.joinToString(","),
            weather = weather,
            createdAt = now,
            updatedAt = now,
            isE2eeEncrypted = isE2eeEncrypted,
            allowAdminAudit = allowAdminAudit,
            syncedToSupabase = false
        )

        diaryDao.insertOrUpdate(entity)
    }

    suspend fun deleteDiary(id: String) {
        diaryDao.deleteById(id)
    }

    suspend fun seedSampleEntriesIfEmpty(userEmail: String) {
        val count = diaryDao.getTotalEntriesCount()
        if (count == 0) {
            saveDiary(
                userId = "usr_${userEmail.hashCode()}",
                userEmail = userEmail,
                title = "Chủ nhật bình yên - Nhật ký mã hóa E2EE",
                content = "Hôm nay tôi dành thời gian đọc sách và uống trà chiều. Mọi thông tin dòng suy nghĩ này đều được mã hóa bằng thuật toán AES-256-GCM trực tiếp tại thiết bị trước khi lưu DB.",
                mood = "Bình yên 🌿",
                tags = listOf("Thư giãn", "Cá nhân"),
                weather = "Nắng ☀️",
                isE2eeEncrypted = true,
                allowAdminAudit = true
            )
            saveDiary(
                userId = "usr_${userEmail.hashCode()}",
                userEmail = userEmail,
                title = "Ghi chú kiểm toán hệ thống & Phân quyền Admin",
                content = "Nhật ký kiểm toán mẫu cho tài khoản Admin devregish@gmail.com. Admin có quyền xem nhật ký user khi cần thẩm định an ninh. Về quyền xem nhật ký user, admin sẽ tự tạo trang chính sách quyền riêng tư sau.",
                mood = "Sâu lắng 🌙",
                tags = listOf("Bảo mật", "Phân quyền"),
                weather = "Mây ☁️",
                isE2eeEncrypted = true,
                allowAdminAudit = true
            )
        }
    }

    suspend fun updateAdminAuditPermission(id: String, allowed: Boolean) {
        diaryDao.updateAdminAuditPermission(id, allowed)
    }

    suspend fun logAdminAuditAction(
        adminEmail: String,
        targetUserEmail: String,
        actionType: String,
        reason: String,
        entryId: String = ""
    ) {
        val log = AuditLogEntity(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            adminEmail = adminEmail,
            targetUserEmail = targetUserEmail,
            actionType = actionType,
            reason = reason,
            entryId = entryId
        )
        auditLogDao.insertLog(log)
    }

    fun getAllAuditLogs(): Flow<List<AuditLogEntity>> {
        return auditLogDao.getAllLogs()
    }
}
