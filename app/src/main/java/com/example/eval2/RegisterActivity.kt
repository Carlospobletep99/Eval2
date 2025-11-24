package com.example.eval2

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.eval2.model.AppDatabase
import com.example.eval2.model.User
import kotlinx.coroutines.launch

class RegisterActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        setContent {
            MaterialTheme {
                var firstName by remember { mutableStateOf("") }
                var lastName by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var confirmPassword by remember { mutableStateOf("") }

                var firstNameError by remember { mutableStateOf<String?>(null) }
                var lastNameError by remember { mutableStateOf<String?>(null) }
                var emailError by remember { mutableStateOf<String?>(null) }
                var passwordError by remember { mutableStateOf<String?>(null) }
                var confirmPasswordError by remember { mutableStateOf<String?>(null) }

                val context = LocalContext.current

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Registro") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                                }
                            }
                        )
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Login Icon",
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(32.dp))


                        OutlinedTextField(value = firstName, onValueChange = { firstName = it; firstNameError = null }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), isError = firstNameError != null)
                        firstNameError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = lastName, onValueChange = { lastName = it; lastNameError = null }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth(), isError = lastNameError != null)
                        lastNameError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = email, onValueChange = { email = it; emailError = null }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), isError = emailError != null, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                        emailError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = password, onValueChange = { password = it; passwordError = null }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), isError = passwordError != null, visualTransformation = PasswordVisualTransformation())
                        passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it; confirmPasswordError = null }, label = { Text("Confirmar contraseña") }, modifier = Modifier.fillMaxWidth(), isError = confirmPasswordError != null, visualTransformation = PasswordVisualTransformation())
                        confirmPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                firstNameError = if (firstName.isBlank()) "El nombre es requerido." else null
                                lastNameError = if (lastName.isBlank()) "El apellido es requerido." else null
                                emailError = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) "El email no es válido." else null
                                passwordError = if (password.length < 6) "La contraseña debe tener al menos 6 caracteres." else null
                                confirmPasswordError = if (password != confirmPassword) "Las contraseñas no coinciden." else null

                                val hasError = listOf(firstNameError, lastNameError, emailError, passwordError, confirmPasswordError).any { it != null }
                                if (!hasError) {
                                    lifecycleScope.launch {
                                        if (userDao.findByEmail(email) == null) {
                                            val user = User(firstName = firstName, lastName = lastName, email = email, passwordHash = password, role = "cliente")
                                            userDao.insert(user)
                                            Toast.makeText(context, "Registro exitoso.", Toast.LENGTH_SHORT).show()
                                            finish()
                                        } else {
                                            emailError = "El correo ya está registrado."
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Registrar") }
                    }
                }
            }
        }
    }
}
