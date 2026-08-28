package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    // Technician queries
    @Query("SELECT * FROM technicians ORDER BY id ASC")
    fun getAllTechnicians(): Flow<List<Technician>>

    @Query("SELECT * FROM technicians WHERE id = :id")
    suspend fun getTechnicianById(id: Long): Technician?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechnician(technician: Technician): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTechnicians(technicians: List<Technician>)

    @Delete
    suspend fun deleteTechnician(technician: Technician)

    @Query("SELECT COUNT(*) FROM technicians")
    suspend fun getTechnicianCount(): Int

    // Chat queries
    @Query("SELECT * FROM chat_messages WHERE technicianId = :techId ORDER BY timestamp ASC")
    fun getMessagesForTechnician(techId: Long): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    // Payment queries
    @Query("SELECT * FROM payment_records ORDER BY timestamp DESC")
    fun getAllPayments(): Flow<List<PaymentRecord>>

    @Query("SELECT * FROM payment_records WHERE technicianId = :techId ORDER BY timestamp DESC")
    fun getPaymentsForTechnician(techId: Long): Flow<List<PaymentRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentRecord): Long
}
