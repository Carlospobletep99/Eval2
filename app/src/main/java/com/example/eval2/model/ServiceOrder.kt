package com.example.eval2.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_orders")
data class ServiceOrder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientName: String,
    val serviceId: Int,
    val status: String, // PENDIENTE | EN_PROCESO | FINALIZADO
    val scheduleDate: String, // yyyy-MM-dd
    val notes: String,
    val photoUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
