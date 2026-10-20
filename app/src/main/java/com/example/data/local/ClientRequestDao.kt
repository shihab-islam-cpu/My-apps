package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ClientRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientRequestDao {
    @Query("SELECT * FROM client_requests ORDER BY receivedAt DESC")
    fun getAllRequests(): Flow<List<ClientRequest>>

    @Query("SELECT * FROM client_requests")
    suspend fun getAllRequestsSync(): List<ClientRequest>

    @Query("SELECT * FROM client_requests WHERE id = :id")
    suspend fun getRequestById(id: Long): ClientRequest?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: ClientRequest): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<ClientRequest>)

    @Update
    suspend fun updateRequest(request: ClientRequest)

    @Delete
    suspend fun deleteRequest(request: ClientRequest)

    @Query("DELETE FROM client_requests WHERE id = :id")
    suspend fun deleteRequestById(id: Long)

    @Query("DELETE FROM client_requests")
    suspend fun clearAllRequests()
}
