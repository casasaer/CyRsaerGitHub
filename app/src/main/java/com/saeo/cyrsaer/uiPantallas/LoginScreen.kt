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
import androidx.lifecycle.get
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

fun login(
    email: String,
    password: String,
    onLoginSuccess: (String) -> Unit, // Se modificó para recibir el uid del usuario
    onLoginError: (String) -> Unit
) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = FirebaseAuth.getInstance().currentUser
                onLoginSuccess(user?.uid ?: "") // Se pasa el uid del usuario al callback
            } else {
                val errorMessage = task.exception?.message ?: "Error al iniciar sesión"
                onLoginError(errorMessage)
            }
        }
}

@Composable
fun LoginScreen(navController: NavController) { // Se agregó el parámetro navController
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Iniciar Sesión", style = MaterialTheme.typography.labelSmall)
        Spacer(modifier = Modifier.height(16.dp))
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
        Button(onClick = {
            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password,
                    onLoginSuccess = { uid ->
                        val database = FirebaseDatabase.getInstance()
                        val userRef = database.reference.child("usuarios").child(uid)

                        userRef.get().addOnSuccessListener { snapshot ->
                            val rol = snapshot.child("rol").value.toString()

                            when (rol) {
                                "camarero" -> navController.navigate("camarero/$uid")
                                "jefe_cocina" -> navController.navigate("jefeCocina/$uid")
                                "administrador" -> navController.navigate("administrador/$uid")
                                else -> {
                                    showError = true
                                    errorMessage = "Rol de usuario no válido"
                                }
                            }
                        }.addOnFailureListener {
                            showError = true
                            errorMessage = "Error al obtener el rol del usuario"
                        }
                    },
                    onLoginError = { error ->
                        showError = true
                        errorMessage = error
                    }
                )
            } else {
                showError = true
                errorMessage = "Por favor, completa todos los campos."
            }
        }) {
            Text("Iniciar Sesión")
        }

        if (showError) {
            Text(text = errorMessage, color = Color.Red)
        }
    }
}