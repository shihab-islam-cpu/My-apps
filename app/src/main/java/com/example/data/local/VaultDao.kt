package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.VaultDocument
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault_documents ORDER BY updatedAt DESC")
    fun getAllDocuments(): Flow<List<VaultDocument>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: VaultDocument): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(docs: List<VaultDocument>)

    @Update
    suspend fun updateDocument(doc: VaultDocument)

    @Delete
    suspend fun deleteDocument(doc: VaultDocument)
}
