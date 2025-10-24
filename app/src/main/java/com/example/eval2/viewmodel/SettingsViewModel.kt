package com.example.eval2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eval2.model.SettingsDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val ds = SettingsDataStore(app)

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _modoTecnico = MutableStateFlow(false)
    val modoTecnico: StateFlow<Boolean> = _modoTecnico

    fun cargar() = viewModelScope.launch {
        delay(1200) // animación de carga
        _modoTecnico.value = ds.obtenerModoTecnico().first() ?: false
        _loading.value = false
    }

    fun alternar() = viewModelScope.launch {
        val nuevo = !_modoTecnico.value
        ds.guardarModoTecnico(nuevo)
        _modoTecnico.value = nuevo
    }
}
