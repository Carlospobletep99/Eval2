package com.example.eval2.repository

import com.example.eval2.model.OrderDao
import com.example.eval2.model.ServiceOrder

class OrderRepository(private val dao: OrderDao) {
    suspend fun getAll() = dao.getAll()
    suspend fun findByClient(q: String) = dao.findByClient("%$q%")
    suspend fun upsert(order: ServiceOrder) = dao.upsert(order)
    suspend fun delete(order: ServiceOrder) = dao.delete(order)
}
