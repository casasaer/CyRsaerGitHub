package com.saeo.cyrsaer.uiPantallas

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun CamareroScreen(navController: NavController, uid: String) {
    Text(text = "Pantalla del Camarero")
    val mesas = listOf("Mesa 1", "Mesa 2", "Mesa 3", "Mesa 4", "Mesa 5") // Lista de mesas

    LazyColumn {
        items(mesas) { mesa ->
            Button(onClick = { navController.navigate("nuevoPedido/${mesa}") }) { // Navegar a la pantalla de nuevo pedido
                Text(text = mesa)
            }
        }
    }


    // Aquí puedes agregar la lógica y el diseño de la pantalla del camarero
    // Puedes usar el uid para obtener información del camarero si es necesario
}