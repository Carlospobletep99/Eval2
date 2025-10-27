package com.example.eval2

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
                val roles = listOf("cliente", "tecnico")
                var selectedRole by remember { mutableStateOf(roles[0]) }
                
                var firstNameError by remember { mutableStateOf(false) }
                var lastNameError by remember { mutableStateOf(false) }
                var emailError by remember { mutableStateOf(false) }
                var passwordError by remember { mutableStateOf(false) }
                var confirmPasswordError by remember { mutableStateOf(false) }

                val context = LocalContext.current

                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
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


                    OutlinedTextField(value = firstName, onValueChange = { firstName = it; firstNameError = false }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), isError = firstNameError)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = lastName, onValueChange = { lastName = it; lastNameError = false }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth(), isError = lastNameError)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it; emailError = false }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), isError = emailError, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = password, onValueChange = { password = it; passwordError = false }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), isError = passwordError, visualTransformation = PasswordVisualTransformation())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it; confirmPasswordError = false }, label = { Text("Confirmar Contraseña") }, modifier = Modifier.fillMaxWidth(), isError = confirmPasswordError, visualTransformation = PasswordVisualTransformation())
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        roles.forEach { role ->
                            Row(Modifier.selectable(selected = (selectedRole == role), onClick = { selectedRole = role }).padding(horizontal = 16.dp)) {
                                RadioButton(selected = (selectedRole == role), onClick = { selectedRole = role })
                                Text(text = role.replaceFirstChar { it.uppercase() }, modifier = Modifier.align(Alignment.CenterVertically))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            firstNameError = firstName.isBlank()
                            lastNameError = lastName.isBlank()
                            emailError = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                            passwordError = password.length < 6
                            confirmPasswordError = password != confirmPassword

                            val hasError = firstNameError || lastNameError || emailError || passwordError || confirmPasswordError
                            if (!hasError) {
                                lifecycleScope.launch {
                                    if (userDao.findByEmail(email) == null) {
                                        val user = User(firstName = firstName, lastName = lastName, email = email, passwordHash = password, role = selectedRole)
                                        userDao.insert(user)
                                        Toast.makeText(context, "Registro Exitoso.", Toast.LENGTH_SHORT).show()
                                        finish()
                                    } else {
                                        Toast.makeText(context, "El Correo ya esta registrado.", Toast.LENGTH_SHORT).show()
                                        emailError = true
                                    }
                                }
                            }
                            else {
                                if (firstNameError){
                                    Toast.makeText(context, "El nombre es requerido.", Toast.LENGTH_SHORT).show()
                                }
                                else if (lastNameError){
                                    Toast.makeText(context, "El apellido es requerido.", Toast.LENGTH_SHORT).show()
                                }
                                else if (emailError){
                                    Toast.makeText(context, "El email no es valido.", Toast.LENGTH_SHORT).show()
                                }
                                else if (passwordError){
                                    Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show()
                                }
                                else if (confirmPasswordError){
                                    Toast.makeText(context, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    Toast.makeText(context, "Por favor, corrija la informacion en los campos resaltados.", Toast.LENGTH_SHORT).show()

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
