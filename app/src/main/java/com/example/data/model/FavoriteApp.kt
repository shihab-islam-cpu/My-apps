package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_apps")
data class FavoriteApp(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appName: String,
    val packageName: String,
    val webFallbackUrl: String,
    val iconCategory: String = "general", // "browser", "video", "mail", "notes", "calendar", "calculator", "music", "code", "chat"
    val launchCount: Int = 0,
    val isPinned: Boolean = true
)
