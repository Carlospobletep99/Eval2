package com.example.eval2.network

import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class JokeApiClientTest {

    @Test
    fun `el objeto se inicializa sin errores`() {
        // Este test es muy simple: solo verifica que el objeto JokeApiClient
        // se puede inicializar sin lanzar una excepción. 
        // Como 'servicio' es un 'lazy delegate', al acceder a él por primera vez,
        // se ejecutará el código de inicialización de Retrofit.
        assertDoesNotThrow {
            val service = JokeApiClient.servicio
        }
    }
}