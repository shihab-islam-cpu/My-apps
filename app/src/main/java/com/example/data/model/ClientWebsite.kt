package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "client_websites")
data class ClientWebsite(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val websiteName: String,
    val clientName: String,
    val url: String,
    val status: String = "Live", // "Live", "In Development", "Under Review", "Maintenance"
    val lastUpdateTime: Long = System.currentTimeMillis(),
    val techStack: String = "Next.js, Tailwind CSS",
    val description: String = "",
    val monthlyMaintenanceNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
