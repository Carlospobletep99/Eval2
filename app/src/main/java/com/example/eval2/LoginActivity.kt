package com.example.eval2

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.eval2.model.AppDatabase
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        setContent {
            MaterialTheme {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var emailError by remember { mutableStateOf<String?>(null) }
                var passwordError by remember { mutableStateOf<String?>(null) }
                val context = LocalContext.current

                Scaffold {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(it).padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mi_icono),
                            contentDescription = "Logo de ServiTech",
                            modifier = Modifier.size(130.dp).clip(CircleShape)
                        )
                        Text("ServiTech", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(top = 16.dp))
                        Text("¡Reparaciones tecnológicas a tu alcance!", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(32.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; emailError = null },
                            label = { Text("Correo electrónico") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = emailError != null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                        emailError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; passwordError = null },
                            label = { Text("Contraseña") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = passwordError != null,
                            visualTransformation = PasswordVisualTransformation()
                        )
                        passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val emailRecortado = email.trim()
                                val passwordRecortada = password.trim()

                                emailError = if (!Patterns.EMAIL_ADDRESS.matcher(emailRecortado).matches()) "El email no es válido." else null
                                passwordError = if (passwordRecortada.isBlank()) "La contraseña es requerida." else null

                                val emailEnMinusculas = emailRecortado.lowercase()

                                if (emailEnMinusculas == "tecnico@gmail.com" && passwordRecortada == "123456") {
                                    Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.putExtra("USER_ROLE", "tecnico")
                                    startActivity(intent)
                                    finish()
                                } else if (emailError == null && passwordError == null) {
                                    lifecycleScope.launch {
                                        val user = userDao.findByEmail(emailEnMinusculas)
                                        if (user != null && user.passwordHash == passwordRecortada) {
                                            Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(context, MainActivity::class.java)
                                            intent.putExtra("USER_ROLE", user.role)
                                            intent.putExtra("USER_ID", user.id)
                                            intent.putExtra("USER_NAME", "${user.firstName} ${user.lastName}")
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            emailError = "Correo o contraseña incorrectos."
                                            passwordError = "Correo o contraseña incorrectos."
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Iniciar Sesión")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = {
                            startActivity(Intent(context, RegisterActivity::class.java))
                        }) {
                            Text("¿No tienes una cuenta? Regístrate")
                        }
                    }
                }
            }
        }
    }
}
