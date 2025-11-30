package com.example.eval2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eval2.model.Joke
import com.example.eval2.network.JokeApiClient
import com.example.eval2.network.JokeApiService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel para gestionar los chistes.
class JokeViewModel(
    // Inyectamos el servicio de la API para facilitar las pruebas.
    private val jokeApiService: JokeApiService = JokeApiClient.servicio
) : ViewModel() {

    // Lista observable para la UI
    private val _listaChistes = MutableStateFlow<List<Joke>>(emptyList())
    val listaChistes: StateFlow<List<Joke>> = _listaChistes

    // Estado para saber si se están cargando los datos
    private val _estaCargando = MutableStateFlow(false)
    val estaCargando: StateFlow<Boolean> = _estaCargando

    // Estado para el mensaje de error de la API
    private val _errorApi = MutableStateFlow<String?>(null)
    val errorApi: StateFlow<String?> = _errorApi

    // Función para conectar a internet y bajar los chistes. Devuelve el Job para poder esperarlo en los tests.
    fun cargarChistes(): Job {
        return viewModelScope.launch {
            _estaCargando.value = true
            _errorApi.value = null

            try {
                // Usamos el servicio inyectado
                val respuesta = jokeApiService.obtenerChistes()
                _listaChistes.value = respuesta.chistes
            } catch (e: Exception) {
                _errorApi.value = "No se pueden cargar los chistes: ${e.message}"
                println("Error cargando chistes: ${e.message}")
            } finally {
                _estaCargando.value = false
            }
        }
    }
}