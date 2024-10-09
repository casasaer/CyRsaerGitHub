package com.saeo.cyrsaer.uiPantallas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore


fun login(
    email: String,
    password: String,
    onLoginSuccess: (String) -> Unit,
    onLoginError: (String) -> Unit
) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = FirebaseAuth.getInstance().currentUser
                onLoginSuccess(user?.uid ?: "")
            } else {
                val exception = task.exception
                val errorMessage = when (exception) {
                    is FirebaseAuthInvalidUserException -> "El usuario no existe o ha sido deshabilitado."
                    is FirebaseAuthInvalidCredentialsException -> "Correo electrónico o contraseña incorrectos."
                    else -> "Error al iniciar sesión: ${exception?.message}"
                }
                onLoginError(errorMessage)
            }
        }
}

fun redirectUser(navController: NavController, rol: String, uid: String) {
    when (rol) {
        "camarero" -> navController.navigate("camarero/$uid")
        "jefe_cocina" -> navController.navigate("jefeCocina/$uid")
        "administrador" -> navController.navigate("administrador/$uid")
        else -> {
            // Manejar el caso de un rol desconocido
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Inicializar Firestore
    val db = FirebaseFirestore.getInstance()

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
                        // Obtener el rol del usuario de Firestore
                        db.collection("usuarios").document(uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val rol = document.getString("rol") ?: ""

                                    // Almacenar el rol del usuario en SharedPreferences
                                    val sharedPreferences =
                                        context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
                                    sharedPreferences.edit().putString("rol", rol).apply()

                                    redirectUser(navController, rol, uid)
                                } else {
                                    showError = true
                                    errorMessage = "Usuario no encontrado en la base de datos."
                                }
                            }
                            .addOnFailureListener { exception ->
                                showError = true
                                errorMessage = "Error al obtener el rol del usuario: ${exception.message}"
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

        Button(onClick = { navController.navigate("registro") }) {
            Text("Registrar nuevo usuario")
        }

        if (showError) {
            Text(text = errorMessage, color = Color.Red)
        }
    }
}