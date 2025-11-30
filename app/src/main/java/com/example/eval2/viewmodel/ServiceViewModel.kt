package com.example.eval2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eval2.model.Service
import com.example.eval2.repository.ServiceRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Este ViewModel ahora solo se preocupa de los Servicios xd
class ServiceViewModel(private val repo: ServiceRepository) : ViewModel() {

    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services

    // Su única responsabilidad al cargar es obtener los servicios.
    fun cargar(): Job {
        return viewModelScope.launch {
            _services.value = repo.getAll()
        }
    }

    fun crear(name: String, desc: String, price: Double): Job {
        return viewModelScope.launch {
            repo.upsert(Service(name = name.trim(), description = desc.trim(), price = price))
            cargar().join() // Esperamos a que la carga interna termine
        }
    }

    fun actualizar(id: Int, name: String, desc: String, price: Double): Job {
        return viewModelScope.launch {
            repo.upsert(Service(id = id, name = name.trim(), description = desc.trim(), price = price))
            cargar().join() // Esperamos a que la carga interna termine
        }
    }

    fun eliminar(svc: Service): Job {
        return viewModelScope.launch { 
            repo.delete(svc)
            cargar().join() // Esperamos a que la carga interna termine
        }
    }
}