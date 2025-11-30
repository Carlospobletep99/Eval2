package com.example.eval2.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class JokeModelTest {

    @Test
    fun `crear instancia de Joke no lanza errores`() {
        assertDoesNotThrow {
            Joke(
                pregunta = "¿Qué le dice un GIF a un JPG?",
                respuesta = "¡Anímate, hombre!"
            )
        }
    }

    @Test
    fun `crear instancia de JokeApiResponse no lanza errores`() {
        assertDoesNotThrow {
            val joke = Joke("pregunta", "respuesta")
            JokeApiResponse(chistes = listOf(joke))
        }
    }
}