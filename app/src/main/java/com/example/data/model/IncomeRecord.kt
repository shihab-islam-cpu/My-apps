package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income_records")
data class IncomeRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientName: String,
    val projectName: String,
    val amount: Double,
    val currency: String = "৳",
    val paymentStatus: String = "Paid", // Paid, Due, Partial
    val paymentDate: String,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
