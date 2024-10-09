package com.saeo.cyrsaer.uiPantallas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.saeo.cyrsaer.model.Plato
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoPedidoScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val platos = remember { mutableStateListOf<Plato>() }
    var mesa by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Mapa para almacenar las cantidades de cada plato
    val cantidadesPlatos = remember { mutableStateMapOf<String, Int>() }

    // Obtener platos de Firestore
    LaunchedEffect(Unit) {
        db.collection("platos")
            .get()
            .addOnSuccessListener { result: QuerySnapshot ->
                for (document in result) {
                    val plato = document.toObject(Plato::class.java)
                    platos.add(plato)
                    // Inicializar la cantidad de cada plato a 0
                    cantidadesPlatos[plato.nombre] = 0
                }
            }
            .addOnFailureListener { exception ->
                Log.w("NuevoPedidoScreen", "Error getting documents.", exception)
                showError = true
                errorMessage = "Error al obtener los platos: ${exception.message}"
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = mesa,
            onValueChange = { mesa = it },
            label = { Text("Número de Mesa") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(platos) { plato ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = plato.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(text = "Precio: ${plato.precio}€", style = MaterialTheme.typography.bodyMedium)
                        }
                        var cantidad by remember { mutableStateOf(cantidadesPlatos[plato.nombre] ?: 0) }
                        OutlinedTextField(
                            value = cantidad.toString(),
                            onValueChange = {
                                cantidad = it.toIntOrNull() ?: 0
                                cantidadesPlatos[plato.nombre] = cantidad // Actualizar la cantidad en el mapa
                            },
                            label = { Text("Cantidad") },
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (mesa.isNotEmpty()) {
                    // Crear el pedido en Firestore
                    val pedido = mutableMapOf<String, Any>(
                        "mesa" to mesa,
                        "fecha" to SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date()),
                        "estado" to "pendiente",
                        "usuario" to FirebaseAuth.getInstance().currentUser?.uid ?: "",
                        "platos" to cantidadesPlatos.filter { it.value > 0 }
                            .map { mapOf("nombre" to it.key, "cantidad" to it.value) } // Agregar platos con cantidad > 0
                    )

                    db.collection("pedidos").add(pedido)
                        .addOnSuccessListener { documentReference ->
                            Log.d("NuevoPedidoScreen", "Pedido creado con ID: ${documentReference.id}")
                            navController.navigate("camarero") // Redirigir a la pantalla del camarero
                        }
                        .addOnFailureListener { e ->
                            Log.w("NuevoPedidoScreen", "Error al crear el pedido", e)
                            showError = true
                            errorMessage = "Error al crear el pedido: ${e.message}"
                        }
                } else {
                    showError = true
                    errorMessage = "Por favor, ingresa el número de mesa."
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Crear Pedido")
        }

        if (showError) {
            Text(text = errorMessage, color = Color.Red)
        }
    }
}