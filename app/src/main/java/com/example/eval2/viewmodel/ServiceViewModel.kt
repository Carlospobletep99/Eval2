package com.example.eval2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eval2.model.Service
import com.example.eval2.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ServiceViewModel(private val repo: ServiceRepository) : ViewModel() {
    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services

    fun cargar() { viewModelScope.launch { _services.value = repo.getAll() } }

    fun crear(name: String, desc: String, price: Double) {
        viewModelScope.launch {
            repo.upsert(Service(name = name, description = desc, price = price))
            cargar()
        }
    }

    fun actualizar(id: Int, name: String, desc: String, price: Double) {
        viewModelScope.launch {
            repo.upsert(Service(id = id, name = name, description = desc, price = price))
            cargar()
        }
    }

    fun eliminar(svc: Service) {
        viewModelScope.launch { repo.delete(svc); cargar() }
    }
}
