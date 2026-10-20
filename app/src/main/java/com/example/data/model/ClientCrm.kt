package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "client_crm_records")
data class ClientCrm(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientName: String,
    val contact: String,
    val project: String,
    val budget: String,
    val paymentStatus: String, // Paid, Due, In Discussion
    val deadline: String,
    val notes: String = "",
    val previousProjects: String = "",
    val communicationHistory: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
