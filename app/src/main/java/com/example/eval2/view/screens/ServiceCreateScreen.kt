package com.example.eval2.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eval2.viewmodel.ServiceViewModel

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
