package com.example.eval2

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
// ---
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eval2.model.AppDatabase
import com.example.eval2.repository.OrderRepository
import com.example.eval2.repository.ServiceRepository
import com.example.eval2.view.screens.OrderFormScreen
import com.example.eval2.view.screens.OrderListScreen
import com.example.eval2.view.screens.ServiceCreateScreen
import com.example.eval2.view.screens.ServiceListScreen
import com.example.eval2.viewmodel.OrderViewModel
import com.example.eval2.viewmodel.ServiceViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userRole = intent.getStringExtra("USER_ROLE") ?: "cliente"
        val userId = intent.getIntExtra("USER_ID", -1)
        val userName = intent.getStringExtra("USER_NAME") ?: ""
        val modoTecnico = userRole == "tecnico"

        setContent {
            MaterialTheme {



                var isLoading by remember { mutableStateOf(true) }


                LaunchedEffect(Unit) {
                    delay(1500)
                    isLoading = false // Oculta la pantalla de carga.
                }


                Box(modifier = Modifier.fillMaxSize()) {
                    if (!isLoading) {
                        val nav = rememberNavController()

                        val db = remember { AppDatabase.getDatabase(this@MainActivity) }
                        val serviceRepo = remember { ServiceRepository(db.serviceDao()) }
                        val orderRepo = remember { OrderRepository(db.orderDao()) }

                        val serviceVM = remember { ServiceViewModel(serviceRepo) }
                        val orderVM = remember { OrderViewModel(orderRepo, db.userDao()) }
                        val context = LocalContext.current

                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = { Text("Servicios Técnicos - Bienvenido $userName") },
                                    // Slot "actions"
                                    actions = {

                                        IconButton(onClick = {

                                            val intent = Intent(context, LoginActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            context.startActivity(intent)

                                            // Cierra la MainActivity actual.
                                            (context as? MainActivity)?.finish()
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.ExitToApp,
                                                contentDescription = "Cerrar Sesión"
                                            )
                                        }
                                    }
                                )
                            }
                        ) { inner ->
                            NavHost(
                                navController = nav,
                                startDestination = "main_screen",
                                modifier = Modifier.padding(inner)
                            ) {
                                composable("main_screen") {
                                    ServiceListScreen(vm = serviceVM, modoTecnico = modoTecnico, nav = nav)
                                }
                                composable("service/new") {
                                    ServiceCreateScreen(vm = serviceVM, serviceId = null) { nav.popBackStack() }
                                }
                                composable(
                                    route = "service/edit/{serviceId}",
                                    arguments = listOf(navArgument("serviceId") { type = NavType.IntType })
                                ) {
                                    val id = it.arguments?.getInt("serviceId")
                                    ServiceCreateScreen(vm = serviceVM, serviceId = id) { nav.popBackStack() }
                                }
                                composable("order/new") {
                                    OrderFormScreen(vm = orderVM, serviceVM = serviceVM, userId = userId) { nav.popBackStack() }
                                }
                                composable("orders") {
                                    OrderListScreen(vm = orderVM, modoTecnico = modoTecnico, clientName = if(!modoTecnico) userName else null)
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = isLoading,
                        exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                }
            }
        }
    }
}
