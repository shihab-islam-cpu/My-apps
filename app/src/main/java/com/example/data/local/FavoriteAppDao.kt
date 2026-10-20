package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FavoriteApp
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteAppDao {
    @Query("SELECT * FROM favorite_apps ORDER BY isPinned DESC, launchCount DESC, id ASC")
    fun getAllFavoriteApps(): Flow<List<FavoriteApp>>

    @Query("SELECT * FROM favorite_apps")
    suspend fun getAllFavoriteAppsSync(): List<FavoriteApp>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: FavoriteApp): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<FavoriteApp>)

    @Update
    suspend fun updateApp(app: FavoriteApp)

    @Delete
    suspend fun deleteApp(app: FavoriteApp)

    @Query("DELETE FROM favorite_apps WHERE id = :id")
    suspend fun deleteAppById(id: Long)

    @Query("UPDATE favorite_apps SET launchCount = launchCount + 1 WHERE id = :id")
    suspend fun incrementLaunchCount(id: Long)

    @Query("DELETE FROM favorite_apps")
    suspend fun clearAllApps()
}
