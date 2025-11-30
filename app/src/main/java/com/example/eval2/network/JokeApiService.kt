package com.example.eval2.network

import com.example.eval2.model.Joke
import com.example.eval2.model.JokeApiResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Interfaz para la API de Chistes (JokeAPI)
interface JokeApiService {
    // Pedimos 10 chistes de la categoría "Programming" en español
    @GET("joke/Programming")
    suspend fun obtenerChistes(
        @Query("type") tipo: String = "twopart", // Chistes de pregunta y respuesta
        @Query("amount") cantidad: Int = 10,
        @Query("lang") idioma: String = "es" // Pedimos los chistes en español
    ): JokeApiResponse
}

// Cliente Retrofit para la API de Chistes
object JokeApiClient {
    private const val URL_BASE = "https://v2.jokeapi.dev/"

    val servicio: JokeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JokeApiService::class.java)
    }
}