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

data class OrderErrors(
    val clientName: String? = null,
    val serviceId: String? = null,
    val scheduleDate: String? = null,
    val numeroCelular: String? = null,
    val correoElectronico: String? = null
)

data class OrderUIState(
    val clientName: String = "",
    val serviceId: Int? = null,
    val scheduleDate: String = "",
    val numeroCelular: String = "",
    val correoElectronico: String = "",
    val notes: String = "",
    val photoUri: String? = null,
    val errors: OrderErrors = OrderErrors()
)

class OrderViewModel(private val repo: OrderRepository, private val userDao: UserDao) : ViewModel() {

    private val _state = MutableStateFlow(OrderUIState())
    val state: StateFlow<OrderUIState> = _state

    private val _orders = MutableStateFlow<List<ServiceOrder>>(emptyList())
    val orders: StateFlow<List<ServiceOrder>> = _orders

    fun loadUser(userId: Int) {
        viewModelScope.launch {
            val user = userDao.findById(userId)
            if (user != null) {
                _state.update {
                    it.copy(
                        clientName = "${user.firstName} ${user.lastName}",
                        correoElectronico = user.email
                    )
                }
            }
        }
    }

    fun onServiceChange(id: Int) =
        _state.update { it.copy(serviceId = id, errors = it.errors.copy(serviceId = null)) }

    fun onDateChange(v: String) =
        _state.update { it.copy(scheduleDate = v, errors = it.errors.copy(scheduleDate = null)) }

    fun onNumeroCelularChange(v: String) =
        _state.update { it.copy(numeroCelular = v, errors = it.errors.copy(numeroCelular = null)) }

    fun onNotesChange(v: String) = _state.update { it.copy(notes = v) }

    fun onPhotoChange(uri: String?) = _state.update { it.copy(photoUri = uri) }

    private fun obtenerErrorDeFecha(fecha: String): String? {
        if (fecha.isBlank()) {
            return "Obligatorio"
        }
        val patronDeFecha = """^\d{4}-\d{2}-\d{2}$""".toRegex()
        if (!patronDeFecha.matches(fecha)) {
            return "Formato incorrecto (AAAA-MM-DD)"
        }
        return null
    }

    fun validar(): Boolean {
        val s = _state.value
        val errs = OrderErrors(
            clientName = if (s.clientName.isBlank()) "Obligatorio" else null,
            serviceId = if (s.serviceId == null) "Selecciona un servicio" else null,
            scheduleDate = obtenerErrorDeFecha(s.scheduleDate),
            numeroCelular = if (s.numeroCelular.isBlank()) "Obligatorio" else null,
            correoElectronico = if (s.correoElectronico.isBlank()) "Obligatorio" else null
        )
        val hayErrores = listOfNotNull(
            errs.clientName, errs.serviceId, errs.scheduleDate, errs.numeroCelular, errs.correoElectronico
        ).isNotEmpty()
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
                    correoElectronico = s.correoElectronico,
                    numeroCelular = s.numeroCelular,
                    status = "PENDIENTE",
                    scheduleDate = s.scheduleDate,
                    notes = s.notes,
                    photoUri = s.photoUri
                )
            )
            cargarOrdenes()
            limpiar()
        }
    }

    fun limpiar() {
        _state.update { OrderUIState() }
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
