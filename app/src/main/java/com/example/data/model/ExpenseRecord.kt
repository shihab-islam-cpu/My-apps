package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_records")
data class ExpenseRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // Hosting, Domain, Software, API, Ads, Hardware, অন্যান্য
    val title: String,
    val amount: Double,
    val currency: String = "৳",
    val date: String,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
