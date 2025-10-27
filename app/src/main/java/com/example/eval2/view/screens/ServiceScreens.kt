package com.example.eval2.view.screens

import androidx.compose.foundation.clickable
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
        ClientServiceList(vm = vm, nav = nav)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechnicianDashboard(vm: ServiceViewModel, nav: NavController) { // Accept the ViewModel as a parameter
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientServiceList(vm: ServiceViewModel, nav: NavController) {
    val services by vm.services.collectAsState()
    LaunchedEffect(Unit) { vm.cargar() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Servicios Disponibles") },
                actions = {
                    TextButton(onClick = { nav.navigate("orders") }) { Text("Mis Órdenes") }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { nav.navigate("order/new") }) { Text("Nueva Orden") }
        }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
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
}

@Composable
fun ServiceCreateScreen(vm: ServiceViewModel, serviceId: Int?, onSaved: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    
    val services by vm.services.collectAsState()
    

    val serviceToEdit = if(serviceId != null) services.firstOrNull { it.id == serviceId } else null

    LaunchedEffect(serviceToEdit) {
        if(services.isEmpty()) vm.cargar()


        if(serviceToEdit != null) {
            name = serviceToEdit.name
            price = serviceToEdit.price.toString()
            desc = serviceToEdit.description
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(if (serviceId == null) "Nuevo Servicio" else "Editar Servicio", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del Servicio") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val p = price.toDoubleOrNull() ?: 0.0
            if (name.isNotBlank() && p > 0) {
                if(serviceId == null) {
                    vm.crear(name, desc, p)
                } else {
                    vm.actualizar(serviceId, name, desc, p)
                }
                onSaved()
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("Guardar Servicio") }
    }
}