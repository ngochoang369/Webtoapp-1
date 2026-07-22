package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.WebConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface WebConfigDao {
    @Query("SELECT * FROM web_configs ORDER BY timestamp DESC")
    fun getAllConfigs(): Flow<List<WebConfig>>

    @Query("SELECT * FROM web_configs WHERE id = :id LIMIT 1")
    fun getConfigById(id: Int): Flow<WebConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: WebConfig): Long

    @Update
    suspend fun updateConfig(config: WebConfig)

    @Delete
    suspend fun deleteConfig(config: WebConfig)
}
