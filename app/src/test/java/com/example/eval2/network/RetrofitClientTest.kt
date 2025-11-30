package com.example.eval2.network

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class RetrofitClientTest {

    @Test
    fun `el objeto se inicializa sin errores`() {
        // Al igual que con JokeApiClient, este test solo verifica que el objeto
        // RetrofitClient se puede inicializar sin lanzar una excepción.
        // Al acceder a 'apiService', se ejecutará el código de inicialización.
        assertDoesNotThrow {
            val service = RetrofitClient.apiService
        }
    }
}