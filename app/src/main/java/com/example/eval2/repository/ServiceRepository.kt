package com.example.eval2.repository

import android.util.Log
import com.example.eval2.model.Service
import com.example.eval2.model.ServiceDao
import com.example.eval2.network.RetrofitClient

class ServiceRepository(private val dao: ServiceDao) {

    private val api = RetrofitClient.apiService

    suspend fun getAll(): List<Service> {
        return try {
            api.getAllServices()
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error al obtener servicios: ${e.message}")
            emptyList() // Si falla, devolvemos lista vacía para no romper la UI
        }
    }

    suspend fun upsert(service: Service) {
        try {
            if (service.id == 0) {
                api.createService(service)
            } else {
                api.updateService(service.id, service)
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error al guardar servicio: ${e.message}")
            // Evitamos que la app explote, solo logueamos el error xd
        }
    }

    suspend fun delete(service: Service) {
        try {
            api.deleteService(service.id)
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error al eliminar: ${e.message}")
        }
    }
}