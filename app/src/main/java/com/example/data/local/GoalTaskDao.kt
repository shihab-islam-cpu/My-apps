package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.GoalTask
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalTaskDao {
    @Query("SELECT * FROM goal_tasks ORDER BY isCompleted ASC, dueDateMillis ASC")
    fun getAllTasks(): Flow<List<GoalTask>>

    @Query("SELECT * FROM goal_tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): GoalTask?

    @Query("SELECT * FROM goal_tasks WHERE reminderTimeMillis IS NOT NULL AND reminderTimeMillis > :nowMillis AND isCompleted = 0 ORDER BY reminderTimeMillis ASC")
    fun getUpcomingReminders(nowMillis: Long): Flow<List<GoalTask>>

    @Query("SELECT * FROM goal_tasks WHERE isCompleted = 0")
    suspend fun getPendingTasksSync(): List<GoalTask>

    @Query("SELECT * FROM goal_tasks WHERE isCompleted = 1")
    suspend fun getCompletedTasksSync(): List<GoalTask>

    @Query("SELECT * FROM goal_tasks")
    suspend fun getAllTasksSync(): List<GoalTask>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: GoalTask): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<GoalTask>)

    @Update
    suspend fun updateTask(task: GoalTask)

    @Delete
    suspend fun deleteTask(task: GoalTask)

    @Query("DELETE FROM goal_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("DELETE FROM goal_tasks")
    suspend fun clearAllTasks()
}
