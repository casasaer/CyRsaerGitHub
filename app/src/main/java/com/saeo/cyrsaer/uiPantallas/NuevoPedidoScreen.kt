package com.saeo.cyrsaer.uiPantallas

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun NuevoPedidoScreen(navController: NavController, mesa: String) {
    var platos by remember { mutableStateOf<List<Plato>>(emptyList()) }
    var pedido by remember { mutableStateOf<MutableMap<String, Int>>(mutableMapOf()) }
    var comentario by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Obtener la lista de platos de Firebase
    LaunchedEffect(key1 = true) {
        val database = FirebaseDatabase.getInstance()
        val platosRef = database.reference.child("platos")

        val platosListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val platosList = mutableListOf<Plato>()
                for (platoSnapshot in snapshot.children) {
                    val plato = platoSnapshot.getValue(Plato::class.java)
                    if (plato != null) {
                        platosList.add(plato)
                    }
                }
                platos = platosList
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores
            }
        }

        platosRef.addValueEventListener(platosListener)
    }

    LazyColumn {
        items(platos) { plato ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${plato.nombre} - $${plato.precio}")
                Spacer(modifier = Modifier.weight(1f))
                var cantidad by remember { mutableStateOf(0) }
                Button(onClick = {
                    if (cantidad > 0) {
                        cantidad--
                        if (cantidad == 0) {
                            pedido.remove(plato.nombre)
                        } else {
                            pedido[plato.nombre] = cantidad
                        }
                    }
                }, enabled = cantidad > 0) { // Deshabilitar el botón "-" si la cantidad es 0
                    Text("-")
                }
                Text(text = cantidad.toString())
                Button(onClick = {
                    cantidad++
                    pedido[plato.nombre] = cantidad
                }) {
                    Text("+")
                }
            }
        }
        item {
            TextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text("Comentario") }
            )
        }
        item {
            // Mostrar el total del pedido
            val total = pedido.entries.sumOf { (plato, cantidad) ->
                platos.find { it.nombre == plato }?.precio?.times(cantidad) ?: 0.0
            }
            Text(text = "Total: $${total}")
        }
        item {
            Button(onClick = {
                if (pedido.isNotEmpty()) {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    val database = FirebaseDatabase.getInstance()
                    val pedidosRef = database.reference.child("pedidos")
                    val nuevoPedido = pedidosRef.push()

                    val pedidoData = hashMapOf(
                        "mesa" to mesa,
                        "camarero" to uid,
                        "items" to pedido.map { (plato, cantidad) ->
                            hashMapOf(
                                "plato" to plato,
                                "cantidad" to cantidad
                            )
                        },
                        "comentario" to comentario, // Agregar el comentario al pedido
                        "estado" to "pendiente",
                        "timestamp" to System.currentTimeMillis()
                    )

                    nuevoPedido.setValue(pedidoData).addOnSuccessListener {
                        // Pedido guardado con éxito
                        navController.navigate("camarero/$uid")
                    }.addOnFailureListener {
                        showError = true
                        errorMessage = "Error al guardar el pedido"
                    }
                } else {
                    showError = true
                    errorMessage = "Debes agregar al menos un plato al pedido"
                }
            }) {
                Text("Guardar Pedido")
            }
        }
        // Mostrar mensaje de error si existe
        if (showError) {
            item {
                Text(text = errorMessage, color = Color.Red)
            }
        }
    }
}

data class Plato(
    val nombre: String = "",
    val precio: Double = 0.0,
    // ... otros campos del plato
)