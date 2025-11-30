package com.example.eval2.repository

import android.util.Log
import com.example.eval2.model.Service
import com.example.eval2.model.ServiceDao
import com.example.eval2.network.ApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.Assertions.assertTrue

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
class ServiceRepositoryTest {

    @RelaxedMockK
    private lateinit var mockApiService: ApiService

    @RelaxedMockK
    private lateinit var mockServiceDao: ServiceDao

    private lateinit var repository: ServiceRepository

    @BeforeEach
    fun setUp() {
        // Simulamos la clase Log para que no falle en el entorno de test
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        
        repository = ServiceRepository(mockServiceDao, mockApiService)
    }

    @Test
    fun `getAll llama a getAllServices de la API`() = runTest {
        coEvery { mockApiService.getAllServices() } returns emptyList()
        repository.getAll()
        coVerify(exactly = 1) { mockApiService.getAllServices() }
    }

    @Test
    fun `upsert llama a createService de la API si el id del servicio es 0`() = runTest {
        val newService = Service(id = 0, name = "Nuevo", description = "Desc", price = 10.0)
        repository.upsert(newService)
        coVerify(exactly = 1) { mockApiService.createService(newService) }
        coVerify(exactly = 0) { mockApiService.updateService(any(), any()) }
    }

    @Test
    fun `upsert llama a updateService de la API si el id del servicio no es 0`() = runTest {
        val existingService = Service(id = 1, name = "Existente", description = "Desc", price = 20.0)
        repository.upsert(existingService)
        coVerify(exactly = 1) { mockApiService.updateService(existingService.id, existingService) }
        coVerify(exactly = 0) { mockApiService.createService(any()) }
    }

    @Test
    fun `delete llama a deleteService de la API`() = runTest {
        val serviceToDelete = Service(id = 5, name = "A borrar", description = "Desc", price = 50.0)
        repository.delete(serviceToDelete)
        coVerify(exactly = 1) { mockApiService.deleteService(serviceToDelete.id) }
    }

    // --- TESTS DE CASOS DE ERROR ---

    @Test
    fun `getAll devuelve lista vacía si la API falla`() = runTest {
        coEvery { mockApiService.getAllServices() } throws RuntimeException("Error de red")
        val result = repository.getAll()
        assertTrue(result.isEmpty())
        // Verificamos que se llamó al Log.e
        coVerify { Log.e(any(), any()) }
    }

    @Test
    fun `upsert no crashea si la API falla`() = runTest {
        val service = Service(id = 1, name = "Test", description = "Desc", price = 1.0)
        coEvery { mockApiService.updateService(any(), any()) } throws RuntimeException("Error de red")
        repository.upsert(service)
        coVerify { Log.e(any(), any()) }
    }

    @Test
    fun `delete no crashea si la API falla`() = runTest {
        val service = Service(id = 1, name = "Test", description = "Desc", price = 1.0)
        coEvery { mockApiService.deleteService(any()) } throws RuntimeException("Error de red")
        repository.delete(service)
        coVerify { Log.e(any(), any()) }
    }
}
