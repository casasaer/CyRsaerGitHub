package com.saeo.cyrsaer.uiPantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.saeo.cyrsaer.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // Simula una espera de 2 segundos y luego navega a LoginScreen
    LaunchedEffect(key1 = true) {
        delay(2000)
        navController.navigate("login")
    }

    // Diseño de la pantalla de inicio
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo), // Reemplaza con tu logo
            contentDescription = "Logo de la aplicación"
        )
        Text(
            text = "CyRsaer",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}