package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_documents")
data class VaultDocument(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // CV, Certificates, ID documents, Passport information, Contracts, Project documents, Important PDFs, Receipts
    val title: String,
    val identifierOrNumber: String = "",
    val issueDate: String = "",
    val expiryDate: String = "",
    val fileReferenceOrLink: String = "",
    val isSensitiveLocked: Boolean = false,
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
