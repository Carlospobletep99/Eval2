package com.example.eval2.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {

    private val _modoTecnico = MutableStateFlow(false)
    val modoTecnico: StateFlow<Boolean> = _modoTecnico

    fun setModoTecnico(isTecnico: Boolean) {
        _modoTecnico.value = isTecnico
    }
}
