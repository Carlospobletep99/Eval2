package com.example.eval2

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.eval2.model.AppDatabase
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        setContent {
            MaterialTheme {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var emailError by remember { mutableStateOf(false) }
                var passwordError by remember { mutableStateOf(false) }
                val context = LocalContext.current

                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image (
                        painter = painterResource(id = R.drawable.mi_icono),
                        contentDescription = "Logo de prueba para nuestro proyecto",
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; emailError = false },
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = emailError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; passwordError = false },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = passwordError,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            emailError = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                            passwordError = password.isBlank()

                            if (!emailError && !passwordError) {
                                lifecycleScope.launch {
                                    val user = userDao.findByEmail(email)
                                    if (user != null && user.passwordHash == password) {
                                        Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(context, MainActivity::class.java)
                                        intent.putExtra("USER_ROLE", user.role)
                                        intent.putExtra("USER_ID", user.id)
                                        intent.putExtra("USER_NAME", "${user.firstName} ${user.lastName}") // Pass user's full name
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(context, "Correo Electronico o contraseña, Incorrectos.", Toast.LENGTH_SHORT).show()
                                        emailError = true
                                        passwordError = true
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Porfavor ingrese un correo electronico y contraseña", Toast.LENGTH_SHORT).show()
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
                        Text("¿No tienes una cuenta? Registrate")
                    }
                }
            }
        }
    }
}
