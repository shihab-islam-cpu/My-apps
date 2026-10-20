package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "website_projects")
data class WebsiteProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val clientName: String = "",
    val liveUrl: String,
    val previewUrl: String = "",
    val techStack: String = "React, Tailwind, Next.js",
    val status: String = "Live", // "Live", "In Development", "Under Review", "Completed"
    val description: String = "",
    val updatedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
