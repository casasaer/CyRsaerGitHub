package com.saeo.cyrsaer.uiPantallas

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.saeo.cyrsaer.model.Camarero
import com.saeo.cyrsaer.uiPantallas.AdministradorUtils.imprimirReporte

@SuppressLint("RestrictedApi")
@Composable
fun AdministradorScreen(navController: NavController, uid: String) {
    var camareros by remember { mutableStateOf<List<Camarero>>(emptyList()) }
    var reportes by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }

    // Obtener la lista de camareros de Firebase
    LaunchedEffect(key1 = true) {
        val db = FirebaseFirestore.getInstance()
        db.collection("usuarios")
            .whereEqualTo("rol", "camarero")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Manejar errores
                    return@addSnapshotListener
                }

                val camarerosList = mutableListOf<Camarero>()
                for (camareroSnapshot in snapshot!!) {
                    val camarero = camareroSnapshot.toObject(Camarero::class.java)
                    camarerosList.add(camarero)
                }
                camareros = camarerosList
            }
    }

    // Obtener los reportes de ventas (Este código necesita ser adaptado a tu lógica)
    LaunchedEffect(key1 = true) {
        val db = FirebaseFirestore.getInstance()
        db.collection("pedidos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Manejar errores
                    return@addSnapshotListener
                }

                val ventasPorDia = mutableMapOf<String, Double>()
                for (pedidoSnapshot in snapshot!!) {
                    // ... (Tu lógica para obtener los reportes)
                }
                reportes = ventasPorDia
            }
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
            val context = LocalContext.current
            Button(onClick = {
                AdministradorUtils.imprimirReporte(reportes, context)
            }) {
                Text("Imprimir reporte")
            }
        }
    }
}
