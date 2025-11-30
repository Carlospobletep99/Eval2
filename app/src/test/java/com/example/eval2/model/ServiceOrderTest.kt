package com.example.eval2.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class ServiceOrderTest {

    @Test
    fun `crear instancia de ServiceOrder no lanza errores`() {
        assertDoesNotThrow {
            ServiceOrder(
                id = 1,
                clientName = "Cliente de Prueba",
                serviceId = 101,
                correoElectronico = "cliente@test.com",
                numeroCelular = "555-1234",
                status = "PENDIENTE",
                scheduleDate = "2024-12-25",
                notes = "Sin notas",
                photoUri = null
            )
        }
    }
}