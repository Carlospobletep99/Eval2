package com.example.eval2

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
                var showLogoutDialog by remember { mutableStateOf(false) }
                val context = LocalContext.current

                val logoutAction = {
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                    (context as? ComponentActivity)?.finish()
                }

                if (showLogoutDialog) {
                    AlertDialog(
                        onDismissRequest = { showLogoutDialog = false },
                        title = { Text("Confirmar Cierre de Sesión") },
                        text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
                        confirmButton = {
                            Button(onClick = { logoutAction() }) {
                                Text("Aceptar")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showLogoutDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }

                LaunchedEffect(Unit) {
                    delay(1500)
                    isLoading = false
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (!isLoading) {
                        val nav = rememberNavController()
                        val db = remember { AppDatabase.getDatabase(this@MainActivity) }
                        val serviceRepo = remember { ServiceRepository(db.serviceDao()) }
                        val orderRepo = remember { OrderRepository(db.orderDao()) }
                        val serviceVM = remember { ServiceViewModel(serviceRepo) }
                        val orderVM = remember { OrderViewModel(orderRepo, db.userDao()) }
                        val navBackStackEntry by nav.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route

                        if (currentRoute == "main_screen") {
                            BackHandler { showLogoutDialog = true }
                        }

                        Scaffold(
                            topBar = { TopBar(nav, modoTecnico) { showLogoutDialog = true } },
                            floatingActionButton = {
                                if (currentRoute == "main_screen" && !modoTecnico) {
                                    ExtendedFloatingActionButton(
                                        onClick = { nav.navigate("order/new") },
                                        icon = { Icon(Icons.Filled.Add, "Nueva orden") },
                                        text = { Text("Nueva Orden") }
                                    )
                                }
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
                                    OrderListScreen(vm = orderVM, modoTecnico = modoTecnico, clientName = if (!modoTecnico) userName else null)
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = isLoading,
                        exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(nav: NavController, modoTecnico: Boolean, onLogoutClick: () -> Unit) {
    val navBackStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBackButton = currentRoute != "main_screen"

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.mi_icono),
                    contentDescription = "Logo ServiTech",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("ServiTech")
            }
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { nav.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        },
        actions = {
            if (currentRoute == "main_screen" && !modoTecnico) {
                TextButton(onClick = { nav.navigate("orders") }) {
                    Text("Mis Órdenes")
                }
            }
            IconButton(onClick = onLogoutClick) {
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
            }
        }
    )
}
