package com.saeo.cyrsaer.uiPantallas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun JefeCocinaScreen(navController: NavController, uid: String) {
    var pedidos by remember { mutableStateOf<List<Pedido>>(emptyList()) }

    // Obtener la lista de pedidos de Firebase
    LaunchedEffect(key1 = true) {
        val database = FirebaseDatabase.getInstance()
        val pedidosRef = database.reference.child("pedidos")

        val pedidosListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pedidosList = mutableListOf<Pedido>()
                for (pedidoSnapshot in snapshot.children) {
                    val pedido = pedidoSnapshot.getValue(Pedido::class.java)
                    if (pedido != null) {
                        pedidosList.add(pedido)
                    }
                }
                pedidos = pedidosList
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores
            }
        }

        pedidosRef.addValueEventListener(pedidosListener)
    }

    LazyColumn {
        items(pedidos) { pedido ->
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = "Mesa: ${pedido.mesa}, Estado: ${pedido.estado}")
                // Mostrar la lista de items del pedido
                for (item in pedido.items) {
                    val nombrePlato = item["plato"] as String
                    val cantidad = item["cantidad"] as Long
                    Text(text = "- $nombrePlato x $cantidad")
                }

                // Botones para cambiar el estado del pedido
                Row {
                    if (pedido.estado == "pendiente") {
                        Button(onClick = { cambiarEstadoPedido(pedido.id, "en_proceso") }) {
                            Text("En proceso")
                        }
                    }
                    if (pedido.estado == "en_proceso") {
                        Button(onClick = { cambiarEstadoPedido(pedido.id, "emplatado") }) {
                            Text("Emplatado")
                        }
                    }
                }
            }
        }
    }
}

fun cambiarEstadoPedido(pedidoId: String, nuevoEstado: String) {
    val database = FirebaseDatabase.getInstance()
    val pedidoRef = database.reference.child("pedidos").child(pedidoId)
    pedidoRef.child("estado").setValue(nuevoEstado)
}

data class Pedido(
    val id: String = "", // Agregar un ID para cada pedido
    val mesa: String = "",
    val camarero: String = "",
    val items: List<Map<String, Any>> = emptyList(),
    val estado: String = "",
    val timestamp: Long = 0
)