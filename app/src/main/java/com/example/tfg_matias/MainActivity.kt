package com.example.tfg_matias

import Resgistrarse
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tfg_matias.pantallas.LoginScreen
import com.example.tfg_matias.ui.theme.TFG_MATIASTheme
import androidx.navigation.compose.rememberNavController
import com.example.tfg_matias.pantallas.Principal


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TFG_MATIASTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1) Creamos el navController
                    val navController = rememberNavController()

                    // 2) Definimos el NavHost con la pantalla inicial = Login
                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.Login.route
                    ) {
                        // Pantalla de Login
                        composable(NavRoutes.Login.route) {
                            LoginScreen(
                                onLoginClick = { email, password, keepSession ->
                                    // Validación o lógica extra si quieres
                                    // Luego navega a HomeScreen
                                    navController.navigate(NavRoutes.Principal.route) {
                                        // Elimina Login de la pila para que no pueda volver
                                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                                    }
                                },
                                onRegisterClick = {
                                    // Si no tiene cuenta, navegar a la pantalla de registro
                                    navController.navigate(NavRoutes.Register.route)
                                }
                            )
                        }

                        // Pantalla de Registro
                        composable(NavRoutes.Register.route) {
                            Resgistrarse(
                                navController = navController,
                                onRegisterCompleted = {
                                    // Navega a la Home (o a donde quieras) tras registrarse
                                    navController.navigate(NavRoutes.Principal.route) {
                                        popUpTo(NavRoutes.Register.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Pantalla principal (Home)
                        composable(NavRoutes.Principal.route) {
                            Principal()
                        }
                    }
                }
            }
        }
    }
}


sealed class NavRoutes(val route: String) {
    object Login : NavRoutes("login")
    object Register : NavRoutes ("register")
    object Principal : NavRoutes("Principal")
    object Venta_car : NavRoutes("Venta_car")
    object Cerrar_Sesion : NavRoutes("Cerrar_Sesion")
}

