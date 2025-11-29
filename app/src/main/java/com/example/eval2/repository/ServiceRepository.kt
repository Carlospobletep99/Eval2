package com.example.eval2.repository

import com.example.eval2.model.Service
import com.example.eval2.model.ServiceDao // Ya no lo usaremos, pero lo dejo por si acaso
import com.example.eval2.network.RetrofitClient

class ServiceRepository(private val dao: ServiceDao) {

    private val api = RetrofitClient.apiService

    suspend fun getAll(): List<Service> {
        return try {
            api.getAllServices()
        } catch (e: Exception) {
            emptyList() // O manejar el error (ej. mostrar toast)
        }
    }

    suspend fun upsert(service: Service) {
        if (service.id == 0) {
            // Si el ID es 0, es nuevo -> Usamos POST (Crear)
            api.createService(service)
        } else {
            // Si hay ID, ya existe -> Usamos PUT (Actualizar)
            api.updateService(service.id, service)
        }
    }

    suspend fun delete(service: Service) {
        api.deleteService(service.id)
    }
}