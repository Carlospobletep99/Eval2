package com.example.eval2.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eval2.model.Service
import com.example.eval2.viewmodel.ServiceViewModel

@Composable
fun ServiceListScreen(vm: ServiceViewModel, modoTecnico: Boolean, nav: NavController) {
    if (modoTecnico) {
        TechnicianDashboard(vm = vm, nav = nav)
    } else {
        ClientServiceList(vm = vm)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechnicianDashboard(vm: ServiceViewModel, nav: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var serviceToDelete by remember { mutableStateOf<Service?>(null) }

    val services by vm.services.collectAsState()
    LaunchedEffect(Unit) { vm.cargar() }

    if (showDialog && serviceToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Seguro que quieres eliminar el servicio '${serviceToDelete!!.name}'?") },
            confirmButton = {
                Button(onClick = {
                    vm.eliminar(serviceToDelete!!)
                    showDialog = false
                }) { Text("Eliminar") }
            },
            dismissButton = { Button(onClick = { showDialog = false }) { Text("Cancelar") } }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()){
             Button(onClick = { nav.navigate("service/new") }) {
                Text("Crear Servicio")
            }
            Button(onClick = { nav.navigate("orders") }) {
                Text("Ver Órdenes")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(services) { s ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(s.name, style = MaterialTheme.typography.titleMedium)
                            Text("$${s.price}")
                        }
                        Row {
                            TextButton(onClick = { nav.navigate("service/edit/${s.id}") }) { Text("Editar") }
                            TextButton(onClick = { 
                                serviceToDelete = s
                                showDialog = true 
                            }) { Text("Borrar") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClientServiceList(vm: ServiceViewModel) {
    val services by vm.services.collectAsState()
    LaunchedEffect(Unit) { vm.cargar() }

    Column(Modifier.padding(16.dp)) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(services) { s ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(s.name, style = MaterialTheme.typography.titleMedium)
                            Text("$${s.price}  ·  ${s.description}")
                        }
                    }
                }
            }
        }
    }
}
