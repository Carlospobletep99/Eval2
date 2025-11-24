package com.example.eval2.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eval2.viewmodel.OrderViewModel
import com.example.eval2.viewmodel.ServiceViewModel
import com.example.eval2.view.components.SmartImage
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderFormScreen(vm: OrderViewModel, serviceVM: ServiceViewModel, userId: Int, onSaved: () -> Unit) {
    val st by vm.state.collectAsState()
    val services by serviceVM.services.collectAsState()

    LaunchedEffect(userId) {
        serviceVM.cargar()
        if(userId != -1) {
            vm.loadUser(userId)
        }
    }

    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Text("Nueva orden de servicio:", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.align(Alignment.CenterHorizontally))

        OutlinedTextField(
            value = st.clientName, onValueChange = {},
            readOnly = true, // Block editing
            label = { Text("Nombre cliente") }, isError = st.errors.clientName != null,
            supportingText = { st.errors.clientName?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            modifier = Modifier.fillMaxWidth()
        )

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = services.firstOrNull { it.id == st.serviceId }?.name ?: "",
                onValueChange = {}, readOnly = true,
                label = { Text("Servicio") }, isError = st.errors.serviceId != null,
                supportingText = { st.errors.serviceId?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                services.forEach { s ->
                    DropdownMenuItem(text = { Text(s.name) }, onClick = {
                        vm.onServiceChange(s.id); expanded = false
                    })
                }
            }
        }

        OutlinedTextField(
            value = st.scheduleDate, onValueChange = vm::onDateChange,
            label = { Text("Fecha (AAAA-MM-DD)") }, isError = st.errors.scheduleDate != null,
            supportingText = { st.errors.scheduleDate?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = st.notes, onValueChange = vm::onNotesChange,
            label = { Text("Notas") }, modifier = Modifier.fillMaxWidth()
        )

        Text("Adjuntar imagen (opcional)", style = MaterialTheme.typography.bodyLarge)
        SmartImage(current = st.photoUri, onUri = vm::onPhotoChange)

        Button(onClick = {
            if (vm.validar()) {
                vm.guardarOrden()
                onSaved()
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Agendar servicio")
        }
    }
}
