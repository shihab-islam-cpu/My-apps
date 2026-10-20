package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "client_requests")
data class ClientRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientName: String,
    val clientEmail: String = "",
    val clientPhone: String = "",
    val projectType: String = "Business Website", // "E-commerce", "Portfolio", "Landing Page", "Web App"
    val budget: String = "$300 - $500",
    val message: String,
    val status: String = "New Request", // "New Request", "In Discussion", "Accepted", "Completed"
    val isStarred: Boolean = false,
    val receivedAt: Long = System.currentTimeMillis()
)
