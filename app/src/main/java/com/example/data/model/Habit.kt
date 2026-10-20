package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String = "Wellness", // Wellness, Productivity, Fitness, Learning
    val iconType: String = "fire", // "fire", "water", "book", "fitness", "code", "meditation"
    val colorHex: String = "#4F46E5",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastCompletedDate: String = "", // "YYYY-MM-DD"
    val completedDatesCsv: String = "", // comma-separated YYYY-MM-DD
    val targetDaysPerWeek: Int = 7,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun isCompletedToday(): Boolean {
        val todayStr = getTodayDateString()
        return lastCompletedDate == todayStr
    }

    fun isCompletedOnDate(dateStr: String): Boolean {
        return completedDatesCsv.split(",").contains(dateStr)
    }

    companion object {
        fun getTodayDateString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }

        fun getYesterdayDateString(): String {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(cal.time)
        }
    }
}
