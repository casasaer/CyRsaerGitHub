package com.saeo.cyrsaer.uiPantallas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegistroScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Inicializar Firestore
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Registro de Usuario", style = MaterialTheme.typography.labelSmall)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") }
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") }
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation()
        )
        TextField(
            value = rol,
            onValueChange = { rol = it },
            label = { Text("Rol (camarero, jefe_cocina, administrador)") }
        )
        Button(onClick = {
            if (nombre.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && rol.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser
                            val uid = user?.uid ?: ""

                            val newUser = mapOf(
                                "nombre" to nombre,
                                "rol" to rol,
                                "email" to email
                            )

                            // Agregar usuario a Firestore
                            db.collection("usuarios").document(uid).set(newUser)
                                .addOnSuccessListener {
                                    // Registro exitoso
                                    navController.navigate("login")
                                }
                                .addOnFailureListener { exception ->
                                    // Error al registrar el usuario
                                    showError = true
                                    errorMessage = "Error al registrar el usuario: ${exception.message}"
                                }
                        } else {
                            // Error al crear la cuenta de usuario
                            showError = true
                            errorMessage = "Error al crear la cuenta de usuario: ${task.exception?.message}"
                        }
                    }
            } else {
                showError = true
                errorMessage = "Por favor, completa todos los campos."
            }
        }) {
            Text("Registrarse")
        }

        if (showError) {
            Text(text = errorMessage, color = Color.Red)
        }
    }
}