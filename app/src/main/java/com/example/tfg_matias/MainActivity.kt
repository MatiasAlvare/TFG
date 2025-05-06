package com.example.tfg_matias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tfg_matias.ViewModel.CarViewModel
import com.example.tfg_matias.pantallas.*
import com.example.tfg_matias.utilidades.AuthViewModel
import com.example.tfg_matias.utilidades.RequireAuth
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()
            val carVM: CarViewModel = viewModel()
            val cars by carVM.filteredCars.collectAsState()

            // Creamos aquí el AuthViewModel que pasaremos a la pantalla de reset
            val authVM: AuthViewModel = viewModel()

            Scaffold(bottomBar = { BarraInferior(navController) }) { padding ->
                NavHost(
                    navController    = navController,
                    startDestination = "login",
                    modifier         = Modifier.padding(padding)
                ) {
                    // LOGIN
                    composable("login") {
                        LoginScreen(
                            onGoogleSignIn        = { /* ... */ },
                            onLoginSuccess        = {
                                navController.navigate("principal") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onRegisterClick       = {
                                navController.navigate("register")
                            },
                            onForgotPasswordClick = {
                                navController.navigate("forgot_password")
                            }
                        )
                    }

                    // REGISTER
                    composable("register") {
                        Registrarse(
                            navController        = navController,
                            onRegisterCompleted  = {
                                navController.navigate("principal") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }
                        )
                    }

                    // RUTA DE OLVIDO
                    composable("forgot_password") {
                        OlvidoContraseña(
                            navController = navController
                        )
                    }

                    // RUTA DE CONFIRMACIÓN
                    composable("reset_confirmation") {
                        EnlaceEnviado {
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }

                    // PRINCIPAL / BÚSQUEDA
                    composable("principal") {
                        Busqueda(
                            cars = cars,
                            onApplyFilters = { marca, modelo, pMin, pMax, _, _, _, _, _, _, _, _, soloElec, _, _ ->
                                carVM.applyFilters(
                                    marca          = marca,
                                    modelo         = modelo,
                                    precioMin      = pMin.toDoubleOrNull(),
                                    precioMax      = pMax.toDoubleOrNull(),
                                    soloElectricos = soloElec
                                )
                            },
                            onCarClick = { id -> navController.navigate("detail/$id") }
                        )
                    }

                    // VENDER (protegido)
                    composable("vender") {
                        RequireAuth(navController) {
                            Vender { coche ->
                                carVM.addCar(coche)
                                navController.navigate("principal") {
                                    popUpTo("principal") { inclusive = true }
                                }
                            }
                        }
                    }

                    // DETALLE
                    composable(
                        route = "detail/{carId}",
                        arguments = listOf(navArgument("carId") { type = NavType.StringType })
                    ) { back ->
                        val id = back.arguments?.getString("carId") ?: ""
                        Detalle(
                            carId        = id,
                            onBack       = { navController.popBackStack() },
                            onViewSeller = { uid -> navController.navigate("perfil/$uid") }
                        )
                    }

                    // CHATS (protegido)
                    composable("chats") {
                        RequireAuth(navController) {
                            ChatList(
                                navController = navController,
                                users         = cars.map { it.ownerId }.distinct()
                            )
                        }
                    }

                    // PERFIL PROPIO (protegido)
                    composable("perfil/me") {
                        RequireAuth(navController) {
                            val uid = FirebaseAuth.getInstance().currentUser!!.uid
                            Perfil(
                                userId     = uid,
                                onCarClick = { cid -> navController.navigate("detail/$cid") }
                            )
                        }
                    }

                    // PERFIL OTRO USUARIO
                    composable(
                        route = "perfil/{userId}",
                        arguments = listOf(navArgument("userId") { type = NavType.StringType })
                    ) { back ->
                        RequireAuth(navController) {
                            val uid = back.arguments?.getString("userId") ?: ""
                            Perfil(
                                userId     = uid,
                                onCarClick = { cid -> navController.navigate("detail/$cid") }
                            )
                        }
                    }
                }
            }
        }
    }
}
