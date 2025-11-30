package com.example.eval2.viewmodel

import com.example.eval2.model.Service
import com.example.eval2.repository.ServiceRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
class ServiceViewModelTest {

    @RelaxedMockK
    private lateinit var serviceRepository: ServiceRepository

    private lateinit var viewModel: ServiceViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ServiceViewModel(serviceRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargar refresca la lista de servicios desde el repositorio`() = runTest {
        // Preparación
        val fakeServices = listOf(Service(1, "Test Service", "Description", 100.0))
        coEvery { serviceRepository.getAll() } returns fakeServices

        // Actuación y Espera
        viewModel.cargar().join()

        // Afirmación
        assertEquals(fakeServices, viewModel.services.value)
    }

    @Test
    fun `crear llama a upsert en el repositorio y refresca la lista`() = runTest {
        // Preparación
        val slot = slot<Service>()
        coEvery { serviceRepository.getAll() } returns emptyList()

        // Actuación y Espera
        viewModel.crear("Nuevo Servicio", "Nueva Desc", 50.0).join()

        // Afirmación
        coVerify(exactly = 1) { serviceRepository.upsert(capture(slot)) }
        assertEquals("Nuevo Servicio", slot.captured.name)
        coVerify(exactly = 1) { serviceRepository.getAll() } // Solo se llama una vez dentro de crear
    }

    @Test
    fun `actualizar llama a upsert con el id correcto y refresca la lista`() = runTest {
        // Preparación
        val slot = slot<Service>()
        coEvery { serviceRepository.getAll() } returns emptyList()

        // Actuación y Espera
        viewModel.actualizar(123, "Servicio Actualizado", "Desc Actualizada", 150.0).join()

        // Afirmación
        coVerify(exactly = 1) { serviceRepository.upsert(capture(slot)) }
        assertEquals(123, slot.captured.id)
        coVerify(exactly = 1) { serviceRepository.getAll() } // Solo se llama una vez dentro de actualizar
    }

    @Test
    fun `eliminar llama a delete en el repositorio y refresca la lista`() = runTest {
        // Preparación
        val serviceToDelete = Service(1, "Para Borrar", "Desc", 10.0)
        coEvery { serviceRepository.getAll() } returns emptyList()

        // Actuación y Espera
        viewModel.eliminar(serviceToDelete).join()

        // Afirmación
        coVerify(exactly = 1) { serviceRepository.delete(serviceToDelete) }
        coVerify(exactly = 1) { serviceRepository.getAll() } // Solo se llama una vez dentro de eliminar
    }
}
