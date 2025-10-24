package com.example.eval2.repository

import com.example.eval2.model.Service
import com.example.eval2.model.ServiceDao

class ServiceRepository(private val dao: ServiceDao) {
    suspend fun getAll() = dao.getAll()
    suspend fun upsert(service: Service) = dao.upsert(service)
    suspend fun delete(service: Service) = dao.delete(service)
}
