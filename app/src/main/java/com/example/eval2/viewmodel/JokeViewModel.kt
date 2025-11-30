package com.example.eval2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eval2.model.Joke
import com.example.eval2.model.JokeApiResponse
import com.example.eval2.network.JokeApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel para gestionar los chistes.
class JokeViewModel : ViewModel() {

    // Lista observable para la UI
    private val _listaChistes = MutableStateFlow<List<Joke>>(emptyList())
    val listaChistes: StateFlow<List<Joke>> = _listaChistes

    // Estado para saber si se están cargando los datos
    private val _estaCargando = MutableStateFlow(false)
    val estaCargando: StateFlow<Boolean> = _estaCargando

    // Estado para el mensaje de error de la API
    private val _errorApi = MutableStateFlow<String?>(null)
    val errorApi: StateFlow<String?> = _errorApi

    // Función para conectar a internet y bajar los chistes
    fun cargarChistes() {
        viewModelScope.launch {
            _estaCargando.value = true
            _errorApi.value = null

            try {
                val respuesta = JokeApiClient.servicio.obtenerChistes()
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