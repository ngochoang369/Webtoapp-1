package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {

    @Query("SELECT * FROM diary_entries WHERE userEmail = :email ORDER BY createdAt DESC")
    fun getEntriesForUser(email: String): Flow<List<DiaryEntryEntity>>

    @Query("SELECT * FROM diary_entries ORDER BY createdAt DESC")
    fun getAllEntriesForAdmin(): Flow<List<DiaryEntryEntity>>

    @Query("SELECT * FROM diary_entries WHERE id = :id LIMIT 1")
    suspend fun getEntryById(id: String): DiaryEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entry: DiaryEntryEntity)

    @Query("DELETE FROM diary_entries WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE diary_entries SET allowAdminAudit = :allowed WHERE id = :id")
    suspend fun updateAdminAuditPermission(id: String, allowed: Boolean)

    @Query("SELECT COUNT(*) FROM diary_entries")
    suspend fun getTotalEntriesCount(): Int
}
