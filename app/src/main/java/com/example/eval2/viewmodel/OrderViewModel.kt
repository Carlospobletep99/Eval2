package com.example.eval2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eval2.model.ServiceOrder
import com.example.eval2.model.UserDao
import com.example.eval2.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderViewModel(private val repo: OrderRepository, private val userDao: UserDao) : ViewModel() {

    private val _state = MutableStateFlow(OrderUIState())
    val state: StateFlow<OrderUIState> = _state

    private val _orders = MutableStateFlow<List<ServiceOrder>>(emptyList())
    val orders: StateFlow<List<ServiceOrder>> = _orders
    
    fun loadUser(userId: Int) {
        viewModelScope.launch {
            val user = userDao.findById(userId)
            if(user != null) {
                _state.update { it.copy(clientName = "${user.firstName} ${user.lastName}") }
            }
        }
    }

    fun onClientChange(v: String) =
        _state.update { it.copy(clientName = v, errors = it.errors.copy(clientName = null)) }

    fun onServiceChange(id: Int) =
        _state.update { it.copy(serviceId = id, errors = it.errors.copy(serviceId = null)) }

    fun onDateChange(v: String) =
        _state.update { it.copy(scheduleDate = v, errors = it.errors.copy(scheduleDate = null)) }

    fun onNotesChange(v: String) = _state.update { it.copy(notes = v) }

    fun onPhotoChange(uri: String?) = _state.update { it.copy(photoUri = uri) }

    fun validar(): Boolean {
        val s = _state.value
        val errs = OrderErrors(
            clientName = if (s.clientName.isBlank()) "Obligatorio" else null,
            serviceId = if (s.serviceId == null) "Selecciona un servicio" else null,
            scheduleDate = if (s.scheduleDate.isBlank()) "Obligatorio" else null
        )
        val hayErrores = listOfNotNull(errs.clientName, errs.serviceId, errs.scheduleDate).isNotEmpty()
        _state.update { it.copy(errors = errs) }
        return !hayErrores
    }

    fun guardarOrden() {
        if (!validar()) return
        val s = _state.value
        viewModelScope.launch {
            repo.upsert(
                ServiceOrder(
                    clientName = s.clientName,
                    serviceId = s.serviceId!!,
                    status = "PENDIENTE",
                    scheduleDate = s.scheduleDate,
                    notes = s.notes,
                    photoUri = s.photoUri
                )
            )
            cargarOrdenes()
        }
    }

    fun cargarOrdenes() {
        viewModelScope.launch { _orders.value = repo.getAll() }
    }

    fun buscarPorCliente(q: String) {
        viewModelScope.launch { _orders.value = repo.findByClient(q) }
    }

    fun actualizarEstado(order: ServiceOrder, nuevo: String) {
        viewModelScope.launch {
            repo.upsert(order.copy(status = nuevo))
            cargarOrdenes()
        }
    }
}
