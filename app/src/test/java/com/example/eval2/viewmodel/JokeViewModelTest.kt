package com.example.eval2.viewmodel

import com.example.eval2.model.Joke
import com.example.eval2.model.JokeApiResponse
import com.example.eval2.network.JokeApiService
import io.mockk.coEvery
import io.mockk.mockk
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

@ExperimentalCoroutinesApi
class JokeViewModelTest {

    private lateinit var viewModel: JokeViewModel
    private val mockJokeApiService: JokeApiService = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = JokeViewModel(jokeApiService = mockJokeApiService)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargarChistes actualiza la lista de chistes cuando la API responde con éxito`() = runTest {
        // Preparación
        val chistesFalsos = listOf(Joke(pregunta = "¿Qué es un terapeuta?", respuesta = "1024 Gigapeutas."))
        val respuestaFalsa = JokeApiResponse(chistes = chistesFalsos)
        coEvery { mockJokeApiService.obtenerChistes(any(), any(), any()) } returns respuestaFalsa

        // Actuación y Espera
        viewModel.cargarChistes().join()

        // Afirmación
        assertEquals(chistesFalsos, viewModel.listaChistes.value)
        assertEquals(false, viewModel.estaCargando.value)
        assertEquals(null, viewModel.errorApi.value)
    }

    @Test
    fun `cargarChistes actualiza el mensaje de error cuando la API falla`() = runTest {
        // Preparación
        val mensajeError = "Error de red"
        coEvery { mockJokeApiService.obtenerChistes(any(), any(), any()) } throws RuntimeException(mensajeError)

        // Actuación y Espera
        viewModel.cargarChistes().join()

        // Afirmación
        assertEquals(true, viewModel.listaChistes.value.isEmpty())
        assertEquals(false, viewModel.estaCargando.value)
        assertEquals("No se pueden cargar los chistes: $mensajeError", viewModel.errorApi.value)
    }

    @Test
    fun `cargarChistes actualiza la lista a vacía si la API responde sin chistes`() = runTest {
        // Preparación
        val respuestaVacia = JokeApiResponse(chistes = emptyList())
        coEvery { mockJokeApiService.obtenerChistes(any(), any(), any()) } returns respuestaVacia

        // Actuación y Espera
        viewModel.cargarChistes().join()

        // Afirmación
        assertEquals(true, viewModel.listaChistes.value.isEmpty())
        assertEquals(false, viewModel.estaCargando.value)
        assertEquals(null, viewModel.errorApi.value)
    }

    @Test
    fun `cargarChistes limpia un error anterior si la nueva carga tiene éxito`() = runTest {
        // Arrange: Forzamos un error inicial.
        val mensajeErrorInicial = "Error inicial"
        coEvery { mockJokeApiService.obtenerChistes(any(), any(), any()) } throws RuntimeException(mensajeErrorInicial)
        viewModel.cargarChistes().join()
        assertEquals("No se pueden cargar los chistes: $mensajeErrorInicial", viewModel.errorApi.value)

        // Arrange: Preparamos una respuesta exitosa.
        val chistesFalsos = listOf(Joke("Pregunta", "Respuesta"))
        val respuestaExitosa = JokeApiResponse(chistes = chistesFalsos)
        coEvery { mockJokeApiService.obtenerChistes(any(), any(), any()) } returns respuestaExitosa

        // Act: Volvemos a cargar y esperamos.
        viewModel.cargarChistes().join()

        // Assert: El error debe haber desaparecido.
        assertEquals(null, viewModel.errorApi.value)
        assertEquals(chistesFalsos, viewModel.listaChistes.value)
    }
}