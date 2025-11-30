package com.example.eval2.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class ServiceTest {

    @Test
    fun `crear instancia de Service no lanza errores`() {
        assertDoesNotThrow {
            Service(
                id = 1,
                name = "Test Service",
                description = "Test Description",
                price = 99.99
            )
        }
    }
}