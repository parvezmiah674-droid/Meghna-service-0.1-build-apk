package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "technicians")
data class Technician(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val phone: String,
    val address: String = "মেঘনা, কুমিল্লা",
    val rating: Double = 4.8,
    val completedJobs: Int = 18,
    val isVerified: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val technicianId: Long,
    val sender: String, // "user" or "technician"
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPayment: Boolean = false,
    val paymentAmount: Double? = null,
    val transactionId: String? = null,
    val paymentMethod: String? = null
)

@Entity(tableName = "payment_records")
data class PaymentRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val technicianId: Long,
    val technicianName: String,
    val amount: Double,
    val paymentMethod: String,
    val transactionId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = "সেবা বিল পরিশোধ"
)
