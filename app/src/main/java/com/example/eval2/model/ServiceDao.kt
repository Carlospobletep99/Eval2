package com.example.eval2.model

import androidx.room.*

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services ORDER BY id DESC")
    suspend fun getAll(): List<Service>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(service: Service)

    @Delete
    suspend fun delete(service: Service)
}
