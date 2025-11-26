package com.example.eval2.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.eval2.model.ServiceOrder
import com.example.eval2.view.components.SmartImage
import com.example.eval2.viewmodel.OrderViewModel
import com.example.eval2.viewmodel.ServiceViewModel
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    vm: OrderViewModel,
    serviceVM: ServiceViewModel,
    modoTecnico: Boolean,
    clientName: String? = null
) {
    val orders by vm.orders.collectAsState()
    val services by serviceVM.services.collectAsState()
    var filtro by remember { mutableStateOf("") }

    LaunchedEffect(clientName) {
        serviceVM.cargar() // Cargar los servicios
        if (clientName != null) {
            vm.buscarPorCliente(clientName)
        } else {
            vm.cargarOrdenes()
        }
    }

    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val title = if (modoTecnico) "Órdenes de clientes:" else "Mis órdenes:"
        Text(title, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.align(Alignment.CenterHorizontally))

        if (modoTecnico) {
            OutlinedTextField(
                value = filtro, onValueChange = {
                    filtro = it
                    if (it.isBlank()) vm.cargarOrdenes() else vm.buscarPorCliente(it)
                },
                label = { Text("Filtrar por cliente") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(orders) { o ->
                val serviceName = services.firstOrNull { it.id == o.serviceId }?.name ?: "Servicio no encontrado"
                OrderRow(o, serviceName, modoTecnico = modoTecnico, onEstado = { est -> vm.actualizarEstado(o, est) })
            }
        }
    }
}

private fun formatStatusForDisplay(status: String): String {
    return status.replace('_', ' ')
}

@Composable
private fun LineaDeInformacion(etiqueta: String, valor: String) {
    Row {
        Text(etiqueta, fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp))
        Text(valor)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderRow(o: ServiceOrder, serviceName: String, modoTecnico: Boolean, onEstado: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (modoTecnico) {
                LineaDeInformacion(etiqueta = "Nombre cliente:", valor = o.clientName)
                LineaDeInformacion(etiqueta = "Fecha solicitud:", valor = o.scheduleDate)
                LineaDeInformacion(etiqueta = "Correo:", valor = o.correoElectronico)
                LineaDeInformacion(etiqueta = "Celular:", valor = o.numeroCelular)
                LineaDeInformacion(etiqueta = "Notas:", valor = o.notes)
                LineaDeInformacion(etiqueta = "Estado:", valor = formatStatusForDisplay(o.status))
            } else {
                Text("Servicio: $serviceName", style = MaterialTheme.typography.titleMedium)
                Text("Fecha: ${o.scheduleDate}")
                Text("Estado: ${formatStatusForDisplay(o.status)}")
                Text("Notas: ${o.notes}")
            }

            o.photoUri?.let {
                Spacer(Modifier.height(4.dp))
                SmartImage(it, onUri = {}, readOnly = true)
                Spacer(Modifier.height(4.dp))
            }

            if (modoTecnico) {
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = formatStatusForDisplay(o.status),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Cambiar estado") },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf("PENDIENTE","EN_PROCESO","FINALIZADO").forEach { st ->
                            DropdownMenuItem(
                                text = { Text(formatStatusForDisplay(st)) },
                                onClick = {
                                    onEstado(st)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
