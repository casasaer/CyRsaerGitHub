package com.saeo.cyrsaer.uiPantallas

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
import com.google.firebase.firestore.FirebaseFirestore
import com.saeo.cyrsaer.model.Pedido

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JefeCocinaScreen(navController: NavController, userId: String?) {
    val db = FirebaseFirestore.getInstance()
    var pedidos by remember { mutableStateOf<List<Pedido>>(emptyList()) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Obtener pedidos de Firestore
    LaunchedEffect(Unit) {
        db.collection("pedidos")
            .whereEqualTo("estado", "pendiente") // Filtrar por estado "pendiente"
            .get()
            .addOnSuccessListener { result ->
                pedidos = result.toObjects(Pedido::class.java)
            }
            .addOnFailureListener { exception ->
                Log.w("JefeCocinaScreen", "Error getting documents.", exception)
                showError = true
                errorMessage = "Error al obtener los pedidos: ${exception.message}"
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Pedidos Pendientes", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (pedidos.isEmpty()) {
            Text(text = "No hay pedidos pendientes.", style = MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(pedidos) { pedido ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Mesa: ${pedido.mesa}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Fecha: ${pedido.fecha}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            // Mostrar los platos del pedido
                            pedido.platos?.forEach { plato ->
                                Text(
                                    text = "${plato.nombre} - ${plato.cantidad}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            // Botón para marcar el pedido como "en preparación"
                            Button(
                                onClick = {
                                    // Actualizar el estado del pedido en Firestore
                                    db.collection("pedidos").document(pedido.id.orEmpty())
                                        .update("estado", "en preparación")
                                        .addOnSuccessListener {
                                            Log.d(
                                                "JefeCocinaScreen",
                                                "Pedido actualizado a 'en preparación'"
                                            )
                                            // Actualizar la lista de pedidos (opcional)
                                            pedidos = pedidos.filter { it.id != pedido.id }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(
                                                "JefeCocinaScreen",
                                                "Error al actualizar el pedido",
                                                e
                                            )
                                            showError = true
                                            errorMessage =
                                                "Error al actualizar el pedido: ${e.message}"
                                        }
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("En Preparación")
                            }
                        }
                    }
                }
            }
        }

        if (showError) {
            Text(text = errorMessage, color = Color.Red)
        }
    }
}