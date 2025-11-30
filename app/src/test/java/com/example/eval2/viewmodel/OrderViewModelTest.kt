package com.example.eval2.viewmodel

import com.example.eval2.model.ServiceOrder
import com.example.eval2.model.User
import com.example.eval2.model.UserDao
import com.example.eval2.repository.OrderRepository
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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
class OrderViewModelTest {

    @RelaxedMockK
    private lateinit var orderRepository: OrderRepository

    @RelaxedMockK
    private lateinit var userDao: UserDao

    private lateinit var viewModel: OrderViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OrderViewModel(orderRepository, userDao)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUser actualiza el estado con la información del usuario`() = runTest {
        val fakeUser = User(1, "John", "Doe", "john.doe@example.com", "hash123", "cliente")
        coEvery { userDao.findById(1) } returns fakeUser
        viewModel.loadUser(1)
        val currentState = viewModel.state.value
        assertEquals("John Doe", currentState.clientName)
        assertEquals("john.doe@example.com", currentState.correoElectronico)
    }

    @Test
    fun `validar devuelve false y muestra errores si los campos obligatorios están vacíos`() {
        val esValido = viewModel.validar()
        assertFalse(esValido)
        val errors = viewModel.state.value.errors
        assertNotNull(errors.clientName)
        assertNotNull(errors.serviceId)
        assertNotNull(errors.scheduleDate)
        assertNotNull(errors.numeroCelular)
        assertNotNull(errors.correoElectronico)
    }

    @Test
    fun `validar devuelve false si el formato de fecha es incorrecto`() {
        viewModel.onDateChange("20-03-2024") // Formato DD-MM-AAAA
        val esValido = viewModel.validar()
        assertFalse(esValido)
        assertEquals("Formato incorrecto (AAAA-MM-DD)", viewModel.state.value.errors.scheduleDate)
    }

    @Test
    fun `validar devuelve true si todos los campos son correctos`() = runTest {
        val fakeUser = User(1, "Jane", "Doe", "jane@test.com", "hash456", "cliente")
        coEvery { userDao.findById(1) } returns fakeUser
        viewModel.loadUser(1)

        viewModel.onServiceChange(1)
        viewModel.onDateChange("2024-12-31")
        viewModel.onNumeroCelularChange("123456789")

        val esValido = viewModel.validar()

        assertTrue(esValido)
        val errors = viewModel.state.value.errors
        assertNull(errors.clientName)
        assertNull(errors.serviceId)
        assertNull(errors.scheduleDate)
        assertNull(errors.numeroCelular)
        assertNull(errors.correoElectronico)
    }

    @Test
    fun `guardarOrden no llama al repositorio si la validación falla`() = runTest {
        viewModel.guardarOrden()
        coVerify(exactly = 0) { orderRepository.upsert(any()) }
    }

    @Test
    fun `guardarOrden llama al repositorio y limpia el estado si la validación tiene éxito`() = runTest {
        val fakeUser = User(1, "Test", "User", "test@user.com", "hash789", "cliente")
        coEvery { userDao.findById(1) } returns fakeUser
        viewModel.loadUser(1)

        viewModel.onServiceChange(1)
        viewModel.onDateChange("2025-01-01")
        viewModel.onNumeroCelularChange("987654321")
        val slot = slot<ServiceOrder>()
        coEvery { orderRepository.upsert(capture(slot)) } returns Unit

        viewModel.guardarOrden()

        coVerify(exactly = 1) { orderRepository.upsert(any()) }
        assertEquals("Test User", slot.captured.clientName)
        assertEquals("", viewModel.state.value.clientName)
    }
    
    @Test
    fun `limpiar reinicia el estado de la UI`() {
        viewModel.onNotesChange("Estas son notas de prueba.")
        assertNotEquals(OrderUIState(), viewModel.state.value)
        viewModel.limpiar()
        assertEquals(OrderUIState(), viewModel.state.value)
    }

    @Test
    fun `cargarOrdenes actualiza la lista de ordenes`() = runTest {
        val fakeOrders = listOf(ServiceOrder(1, "Cliente 1", 1, "a@a.com", "123", "OK", "2024-01-01", notes = ""))
        coEvery { orderRepository.getAll() } returns fakeOrders
        viewModel.cargarOrdenes()
        assertEquals(fakeOrders, viewModel.orders.value)
    }

    @Test
    fun `buscarPorCliente llama al repositorio con la consulta correcta`() = runTest {
        val query = "John"
        val fakeOrders = listOf(ServiceOrder(1, "John Doe", 1, "j@d.com", "456", "PENDING", "2024-02-02", notes = ""))
        coEvery { orderRepository.findByClient(query) } returns fakeOrders
        viewModel.buscarPorCliente(query)
        coVerify { orderRepository.findByClient(query) }
        assertEquals(fakeOrders, viewModel.orders.value)
    }

    @Test
    fun `actualizarEstado guarda la orden con el nuevo estado y recarga`() = runTest {
        val order = ServiceOrder(1, "Test Client", 1, "t@c.com", "789", "PENDIENTE", "2024-03-03", notes = "Nota original")
        val nuevoEstado = "COMPLETADO"
        val slot = slot<ServiceOrder>()
        coEvery { orderRepository.upsert(capture(slot)) } returns Unit

        viewModel.actualizarEstado(order, nuevoEstado)

        coVerify { orderRepository.upsert(any()) }
        assertEquals(nuevoEstado, slot.captured.status)
        assertEquals(order.id, slot.captured.id)
        coVerify { orderRepository.getAll() } // Verifica que se recargan las órdenes
    }

    @Test
    fun `loadUser con usuario no válido no actualiza el estado`() = runTest {
        coEvery { userDao.findById(any()) } returns null
        val initialState = viewModel.state.value
        viewModel.loadUser(999)
        assertEquals(initialState, viewModel.state.value)
    }

    // --- TESTS PARA on...Change --- 

    @Test
    fun `onServiceChange actualiza el serviceId en el estado`() {
        viewModel.onServiceChange(99)
        assertEquals(99, viewModel.state.value.serviceId)
    }

    @Test
    fun `onDateChange actualiza la fecha en el estado`() {
        val newDate = "2025-10-20"
        viewModel.onDateChange(newDate)
        assertEquals(newDate, viewModel.state.value.scheduleDate)
    }

    @Test
    fun `onNumeroCelularChange actualiza el celular en el estado`() {
        val newPhone = "555-1234"
        viewModel.onNumeroCelularChange(newPhone)
        assertEquals(newPhone, viewModel.state.value.numeroCelular)
    }

    @Test
    fun `onNotesChange actualiza las notas en el estado`() {
        val newNotes = "Notas importantes"
        viewModel.onNotesChange(newNotes)
        assertEquals(newNotes, viewModel.state.value.notes)
    }

    @Test
    fun `onPhotoChange actualiza la URI de la foto en el estado`() {
        val newUri = "content://pictures/1"
        viewModel.onPhotoChange(newUri)
        assertEquals(newUri, viewModel.state.value.photoUri)
    }
}