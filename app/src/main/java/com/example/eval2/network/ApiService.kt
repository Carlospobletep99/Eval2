package com.example.eval2.network

import com.example.eval2.model.Service
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("api/services")
    suspend fun getAllServices(): List<Service>

    @POST("api/services")
    suspend fun createService(@Body service: Service): Service

    @PUT("api/services/{id}")
    suspend fun updateService(@Path("id") id: Int, @Body service: Service): Service

    @DELETE("api/services/{id}")
    suspend fun deleteService(@Path("id") id: Int)
}