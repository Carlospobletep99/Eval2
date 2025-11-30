package com.example.eval2.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eval2.model.Joke
import com.example.eval2.model.Service
import com.example.eval2.viewmodel.JokeViewModel
import com.example.eval2.viewmodel.ServiceViewModel

@Composable
fun ServiceListScreen(
    serviceVM: ServiceViewModel,
    jokeVM: JokeViewModel,       // parámetro para el JokeViewModel
    modoTecnico: Boolean,
    nav: NavController,
    clientName: String? = null
) {
    if (modoTecnico) {
        // El dashboard del técnico solo necesita el ServiceViewModel
        TechnicianDashboard(vm = serviceVM, nav = nav)
    } else {
        ClientServiceList(serviceVM = serviceVM, jokeVM = jokeVM, clientName = clientName)
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Panel de técnico", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()){
            Button(onClick = { nav.navigate("service/new") }) {
                Text("Crear servicio")
            }
            Button(onClick = { nav.navigate("orders") }) {
                Text("Ver Órdenes")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Servicios a ofrecer:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
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
fun ClientServiceList(
    serviceVM: ServiceViewModel,
    jokeVM: JokeViewModel,
    clientName: String?
) {
    val services by serviceVM.services.collectAsState()
    val chistes by jokeVM.listaChistes.collectAsState()
    val estaCargando by jokeVM.estaCargando.collectAsState()
    val errorApi by jokeVM.errorApi.collectAsState()

    LaunchedEffect(Unit) {
        serviceVM.cargar() // Carga los servicios
        jokeVM.cargarChistes() // Carga los chistes
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        clientName?.let {
            Text(
                text = "¡Hola, $it!",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Sección superior: Servicios
        Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Text(
                text = "Servicios a contratar:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp)) {
                items(services) { s ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text(s.name, style = MaterialTheme.typography.titleMedium)
                                Text("$${s.price}  ·  ${s.description}")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Sección inferior: Chistes
        Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Text(
                text = "¡Alegra tu día! :D",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                when {
                    estaCargando -> CircularProgressIndicator(modifier = Modifier.testTag("loadingIndicator"))
                    errorApi != null -> Text(errorApi!!, color = Color.Red, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                    chistes.isNotEmpty() -> {
                        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp)) {
                            items(chistes) { chiste ->
                                TarjetaChiste(chiste)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                    else -> Text("No hay chistes disponibles.", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun TarjetaChiste(joke: Joke) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = joke.pregunta,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = joke.respuesta,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
