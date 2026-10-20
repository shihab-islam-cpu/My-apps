package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ClientCrm
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientCrmDao {
    @Query("SELECT * FROM client_crm_records ORDER BY updatedAt DESC")
    fun getAllClients(): Flow<List<ClientCrm>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientCrm): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClients(clients: List<ClientCrm>)

    @Update
    suspend fun updateClient(client: ClientCrm)

    @Delete
    suspend fun deleteClient(client: ClientCrm)
}
