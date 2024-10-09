package com.saeo.cyrsaer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.saeo.cyrsaer.ui.theme.CyRsaerTheme
import com.saeo.cyrsaer.uiPantallas.AdministradorScreen
import com.saeo.cyrsaer.uiPantallas.CamareroScreen
import com.saeo.cyrsaer.uiPantallas.JefeCocinaScreen
import com.saeo.cyrsaer.uiPantallas.LoginScreen
import com.saeo.cyrsaer.uiPantallas.NuevoPedidoScreen
import com.saeo.cyrsaer.uiPantallas.RegistroScreen
import com.saeo.cyrsaer.uiPantallas.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CyRsaerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") { SplashScreen(navController) }
                        composable("login") { LoginScreen(navController) }
                        composable("camarero/{uid}") { backStackEntry ->
                            CamareroScreen(
                                navController,
                                backStackEntry.arguments?.getString("uid") ?: ""
                            )
                        }
                        composable("jefeCocina/{uid}") { backStackEntry ->
                            JefeCocinaScreen(
                                navController,
                                backStackEntry.arguments?.getString("uid") ?: ""
                            )
                        }
                        composable("nuevoPedido/{mesa}") { backStackEntry ->
                            NuevoPedidoScreen(
                                navController,
                                backStackEntry.arguments?.getString("mesa") ?: ""
                            )
                        }
                        composable("administrador/{uid}") { backStackEntry ->
                            AdministradorScreen(
                                navController,
                                backStackEntry.arguments?.getString("uid") ?: ""
                            )
                        }
                        composable("login") { LoginScreen(navController) }
                        composable("registro") { RegistroScreen(navController) }


                        // ... Agrega más rutas para otras pantallas
                    }
                }
            }
        }
    }
}