package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Priority {
    HIGH,
    MEDIUM,
    LOW
}

@Entity(tableName = "goal_tasks")
data class GoalTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "General", // Work, Personal, Study, Health, Urgent
    val priority: Priority = Priority.MEDIUM,
    val dueDateMillis: Long = System.currentTimeMillis(),
    val reminderTimeMillis: Long? = null,
    val isCompleted: Boolean = false,
    val completionDateMillis: Long? = null,
    val progress: Int = 0, // 0 to 100
    val isStarred: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
