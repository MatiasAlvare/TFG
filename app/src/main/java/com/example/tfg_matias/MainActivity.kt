package com.example.tfg_matias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tfg_matias.ViewModel.CarViewModel
import com.example.tfg_matias.pantallas.*
import com.example.tfg_matias.ui.theme.BarraInferior
import com.example.tfg_matias.utilidades.RequireAuth
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val carVM: CarViewModel = viewModel()
            val currentEntry by navController.currentBackStackEntryAsState()
            val currentRoute = currentEntry?.destination?.route
            val user = FirebaseAuth.getInstance().currentUser
            val cars by carVM.filteredCars.collectAsState()

            Scaffold(
                bottomBar = {
                    // Solo mostramos la barra si estamos en principal,
                    // o si hay sesión y no estamos en login/register/forgot
                    if (
                        currentRoute == "principal" ||
                        (user != null && currentRoute !in listOf("login","register","forgot_password"))
                    ) {
                        BarraInferior(navController)
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "login",
                    modifier = Modifier
                        .padding(innerPadding)    // <— aquí aplicamos el padding que Scaffold nos pasa
                        .fillMaxSize()
                ) {
                    composable("login") {
                        LoginScreen(
                            onGoogleSignIn        = {
                                /* igual que antes */
                            },
                            onLoginSuccess        = {
                                navController.navigate("principal") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onRegisterClick       = { navController.navigate("register") },
                            onForgotPasswordClick = { navController.navigate("forgot_password") },
                            onGuestAccess         = {
                                navController.navigate("principal") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("register") {
                        Registrarse(
                            navController       = navController,
                            onRegisterCompleted = {
                                navController.navigate("principal") { popUpTo("register") { inclusive = true } }
                            },
                            onGuestAccess = {
                                navController.navigate("principal") { popUpTo("register") { inclusive = true } }
                            }
                        )
                    }

                    composable("forgot_password") {
                        OlvidoContraseña(navController = navController)
                    }

                    composable("reset_confirmation") {
                        EnlaceEnviado {
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }

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

                    composable(
                        route = "detail/{carId}",
                        arguments = listOf(navArgument("carId") { type = NavType.StringType })
                    ) { back ->
                        RequireAuth(navController) {
                            val id = back.arguments?.getString("carId") ?: ""
                            Detalle(
                                carId        = id,
                                onBack       = { navController.popBackStack() },
                                onViewSeller = { uid -> navController.navigate("perfil/$uid") }
                            )
                        }
                    }

                    composable("chats") {
                        RequireAuth(navController) {
                            ChatList(
                                navController = navController,
                                users         = cars.map { it.ownerId }.distinct()
                            )
                        }
                    }

                    composable("perfil/me") {
                        RequireAuth(navController) {
                            val uid = FirebaseAuth.getInstance().currentUser!!.uid
                            Perfil(
                                userId     = uid,
                                onCarClick = { cid -> navController.navigate("detail/$cid") }
                            )
                        }
                    }

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
