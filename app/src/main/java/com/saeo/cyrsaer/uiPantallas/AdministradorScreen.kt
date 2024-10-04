package com.saeo.cyrsaer.uiPantallas

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.firebase.database.*
import com.saeo.cyrsaer.model.Camarero

@SuppressLint("RestrictedApi")
@Composable
fun AdministradorScreen(navController: NavController, uid: String) {
    var camareros by remember { mutableStateOf<List<Camarero>>(emptyList()) }
    var reportes by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }

    // Obtener la lista de camareros de Firebase
    LaunchedEffect(key1 = true) {
        val database = FirebaseDatabase.getInstance()
        val camarerosRef =
            database.reference.child("usuarios").orderByChild("rol").equalTo("camarero")

        val camarerosListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val camarerosList = mutableListOf<Camarero>()
                for (camareroSnapshot in snapshot.children) {
                    val camarero = camareroSnapshot.getValue(Camarero::class.java)
                    if (camarero != null) {
                        camarerosList.add(camarero)
                    }
                }
                camareros = camarerosList
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores
            }
        }
        camarerosRef.addValueEventListener(camarerosListener)
    }

    // Obtener los reportes de ventas (Este código necesita ser adaptado a tu lógica)
    LaunchedEffect(key1 = true) {
        val database = FirebaseDatabase.getInstance()
        val pedidosRef = database.reference.child("pedidos")

        val pedidosListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ventasPorDia = mutableMapOf<String, Double>()
                for (pedidoSnapshot in snapshot.children) {
                    // ... (Tu lógica para obtener los reportes)
                }
                reportes = ventasPorDia
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores
            }
        }
        pedidosRef.addValueEventListener(pedidosListener)
    }

    LazyColumn {
        items(camareros) { camarero ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = camarero.nombre)
                Spacer(modifier = Modifier.weight(1f))
                var habilitado by remember { mutableStateOf(camarero.habilitado) }
                Switch(
                    checked = habilitado,
                    onCheckedChange = {
                        habilitado = it
                        AdministradorUtils.cambiarEstadoCamarero(camarero.uid, habilitado)
                    }
                )
            }
        }

        // Mostrar reportes (Este código necesita ser adaptado a tu lógica)
        items(reportes.entries.toList()) { (fecha, total) ->
            Text(text = "Ventas del $fecha: $${total}")
        }

        item {
            Button(onClick = {
                @Composable {
                    ImprimirReporte(reportes)
                }
            }) {
                Text("Imprimir reporte")
            }
        }
    }
}

@Composable
fun ImprimirReporte(reportes: Map<String, Double>) {
    val context = LocalContext.current
    AdministradorUtils.imprimirReporte(reportes, context)
}
