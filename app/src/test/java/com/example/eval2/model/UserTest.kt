package com.example.eval2.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class UserTest {

    @Test
    fun `crear instancia de User no lanza errores`() {
        // Este es el test más simple posible. Solo verifica que podemos crear
        // un objeto del modelo de datos sin que la aplicación falle.
        // Esto es útil para aumentar la cobertura de clases.
        assertDoesNotThrow {
            User(
                id = 1,
                firstName = "John",
                lastName = "Doe",
                email = "john.doe@test.com",
                passwordHash = "hash123",
                role = "cliente"
            )
        }
    }
}