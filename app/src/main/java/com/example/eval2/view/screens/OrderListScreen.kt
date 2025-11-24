package com.example.eval2.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eval2.model.ServiceOrder
import com.example.eval2.view.components.SmartImage
import com.example.eval2.viewmodel.OrderViewModel
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(vm: OrderViewModel, modoTecnico: Boolean, clientName: String? = null) {
    val orders by vm.orders.collectAsState()
    var filtro by remember { mutableStateOf("") }

    LaunchedEffect(clientName) {
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
                OrderRow(o, modoTecnico = modoTecnico, onEstado = { est -> vm.actualizarEstado(o, est) })
            }
        }
    }
}

private fun formatStatusForDisplay(status: String): String {
    return status.replace('_', ' ')
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderRow(o: ServiceOrder, modoTecnico: Boolean, onEstado: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("${o.clientName} · ${o.scheduleDate}", style = MaterialTheme.typography.titleMedium)
            Text("Estado: ${formatStatusForDisplay(o.status)}")
            Text("Notas: ${o.notes}")
            o.photoUri?.let { uri ->
                SmartImage(uri, onUri = {}, readOnly = true)
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
