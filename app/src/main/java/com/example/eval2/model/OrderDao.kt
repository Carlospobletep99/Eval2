package com.example.eval2.model

import androidx.room.*

@Dao
interface OrderDao {
    @Query("SELECT * FROM service_orders ORDER BY id DESC")
    suspend fun getAll(): List<ServiceOrder>

    @Query("SELECT * FROM service_orders WHERE clientName LIKE :q ORDER BY id DESC")
    suspend fun findByClient(q: String): List<ServiceOrder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(order: ServiceOrder)

    @Delete
    suspend fun delete(order: ServiceOrder)
}
