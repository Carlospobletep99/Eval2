package com.example.eval2.model

import com.google.gson.annotations.SerializedName

// Estructura para la respuesta de la API de chistes
data class JokeApiResponse(
    @SerializedName("jokes") val chistes: List<Joke>
)

// Estructura para un chiste individual
data class Joke(
    @SerializedName("setup") val pregunta: String,
    @SerializedName("delivery") val respuesta: String
)
