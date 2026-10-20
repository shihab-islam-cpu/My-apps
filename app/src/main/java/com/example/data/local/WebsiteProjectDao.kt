package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.WebsiteProject
import kotlinx.coroutines.flow.Flow

@Dao
interface WebsiteProjectDao {
    @Query("SELECT * FROM website_projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<WebsiteProject>>

    @Query("SELECT * FROM website_projects")
    suspend fun getAllProjectsSync(): List<WebsiteProject>

    @Query("SELECT * FROM website_projects WHERE id = :id")
    suspend fun getProjectById(id: Long): WebsiteProject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: WebsiteProject): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<WebsiteProject>)

    @Update
    suspend fun updateProject(project: WebsiteProject)

    @Delete
    suspend fun deleteProject(project: WebsiteProject)

    @Query("DELETE FROM website_projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)

    @Query("DELETE FROM website_projects")
    suspend fun clearAllProjects()
}
