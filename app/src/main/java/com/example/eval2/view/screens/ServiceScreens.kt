package com.example.eval2.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eval2.model.Service
import com.example.eval2.viewmodel.ServiceViewModel

@Composable
fun ServiceListScreen(vm: ServiceViewModel, onGoNewOrder: () -> Unit) {
    val services by vm.services.collectAsState()
    LaunchedEffect(Unit) { vm.cargar() }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onGoNewOrder) { Text("Nueva Orden") }
        }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            Text("Servicios", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            ServiceCreateRow(vm)
            Spacer(Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(services) { s ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth().clickable { /* editar opcional */ }
                    ) {
                        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text(s.name, style = MaterialTheme.typography.titleMedium)
                                Text("$${s.price}  ·  ${s.description}")
                            }
                            TextButton(onClick = { vm.eliminar(s) }) { Text("Borrar") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceCreateRow(vm: ServiceViewModel) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            val p = price.toDoubleOrNull() ?: 0.0
            if (name.isNotBlank() && p > 0) {
                vm.crear(name, desc, p)
                name = ""; price = ""; desc = ""
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("Crear servicio") }
    }
}
