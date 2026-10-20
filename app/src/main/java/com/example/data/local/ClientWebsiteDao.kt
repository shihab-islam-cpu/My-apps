package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ClientWebsite
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientWebsiteDao {
    @Query("SELECT * FROM client_websites ORDER BY lastUpdateTime DESC")
    fun getAllClientWebsites(): Flow<List<ClientWebsite>>

    @Query("SELECT * FROM client_websites WHERE id = :id")
    suspend fun getWebsiteById(id: Long): ClientWebsite?

    @Query("SELECT * FROM client_websites")
    suspend fun getAllWebsitesSync(): List<ClientWebsite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWebsite(website: ClientWebsite): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWebsites(websites: List<ClientWebsite>)

    @Update
    suspend fun updateWebsite(website: ClientWebsite)

    @Delete
    suspend fun deleteWebsite(website: ClientWebsite)

    @Query("DELETE FROM client_websites WHERE id = :id")
    suspend fun deleteWebsiteById(id: Long)

    @Query("UPDATE client_websites SET status = :newStatus, lastUpdateTime = :updateTime WHERE id = :id")
    suspend fun updateStatus(id: Long, newStatus: String, updateTime: Long = System.currentTimeMillis())

    @Query("DELETE FROM client_websites")
    suspend fun clearAllWebsites()
}
