package com.example.eval2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eval2.model.AppDatabase
import com.example.eval2.repository.OrderRepository
import com.example.eval2.repository.ServiceRepository
import com.example.eval2.view.screens.OrderFormScreen
import com.example.eval2.view.screens.OrderListScreen
import com.example.eval2.view.screens.ServiceListScreen
import com.example.eval2.viewmodel.OrderViewModel
import com.example.eval2.viewmodel.ServiceViewModel
import com.example.eval2.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val nav = rememberNavController()

                // Instancias Room + Repos
                val db = remember { AppDatabase.getDatabase(this) }
                val serviceRepo = remember { ServiceRepository(db.serviceDao()) }
                val orderRepo = remember { OrderRepository(db.orderDao()) }

                // ViewModels (simple)
                val serviceVM = remember { ServiceViewModel(serviceRepo) }
                val orderVM = remember { OrderViewModel(orderRepo) }
                val settingsVM: SettingsViewModel = viewModel()

                val loading by settingsVM.loading.collectAsState()
                val modoTecnico by settingsVM.modoTecnico.collectAsState()

                if (loading) {
                    Scaffold(modifier = androidx.compose.ui.Modifier.fillMaxSize()) { inner ->
                        Box(modifier = androidx.compose.ui.Modifier.fillMaxSize().padding(inner)) {
                            CircularProgressIndicator()
                        }
                    }
                    LaunchedEffect(Unit) { settingsVM.cargar() }
                } else {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("Eval2 · Servicios Técnicos") },
                                actions = {
                                    TextButton(onClick = { settingsVM.alternar() }) {
                                        Text(if (modoTecnico) "Modo Técnico" else "Modo Cliente")
                                    }
                                }
                            )
                        }
                    ) { inner ->
                        NavHost(
                            navController = nav,
                            startDestination = "services",
                            modifier = androidx.compose.ui.Modifier.padding(inner)
                        ) {
                            composable("services") {
                                ServiceListScreen(
                                    vm = serviceVM,
                                    onGoNewOrder = { nav.navigate("order/new") }
                                )
                            }
                            composable("order/new") {
                                OrderFormScreen(
                                    vm = orderVM,
                                    serviceVM = serviceVM
                                ) { nav.navigate("orders") }
                            }
                            composable("orders") {
                                OrderListScreen(vm = orderVM)
                            }
                        }
                    }
                }
            }
        }
    }
}
