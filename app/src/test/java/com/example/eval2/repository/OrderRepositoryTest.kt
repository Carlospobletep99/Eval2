package com.example.eval2.repository

import com.example.eval2.model.OrderDao
import com.example.eval2.model.ServiceOrder
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
class OrderRepositoryTest {

    @RelaxedMockK
    private lateinit var mockOrderDao: OrderDao

    private lateinit var repository: OrderRepository

    @BeforeEach
    fun setUp() {
        repository = OrderRepository(mockOrderDao)
    }

    @Test
    fun `getAll llama al método getAll del DAO`() = runTest {
        coEvery { mockOrderDao.getAll() } returns emptyList()
        repository.getAll()
        coVerify(exactly = 1) { mockOrderDao.getAll() }
    }

    @Test
    fun `findByClient llama al método findByClient del DAO con los comodines correctos`() = runTest {
        val query = "Test"
        coEvery { mockOrderDao.findByClient(any()) } returns emptyList()
        repository.findByClient(query)
        coVerify(exactly = 1) { mockOrderDao.findByClient("%Test%") }
    }

    @Test
    fun `upsert llama al método upsert del DAO`() = runTest {
        val order = ServiceOrder(1, "Cliente", 1, "e@mail.com", "123", "OK", "2024-01-01", "notas")
        repository.upsert(order)
        coVerify(exactly = 1) { mockOrderDao.upsert(order) }
    }

    @Test
    fun `delete llama al método delete del DAO`() = runTest {
        val order = ServiceOrder(1, "Cliente", 1, "e@mail.com", "123", "OK", "2024-01-01", "notas")
        repository.delete(order)
        // Usamos any() para evitar problemas de comparación de objetos en el mock
        coVerify(exactly = 1) { mockOrderDao.delete(any()) }
    }
}
