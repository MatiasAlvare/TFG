// ✅ Navegacion.kt actualizado para incluir todas las pantallas (login, registro, chats, perfil...)

package com.example.tfg_matias.navegacion

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

@SuppressLint("ComposableDestinationInComposeScope")
@Composable
fun Navegacion(carVM: CarViewModel) {
    val navController = rememberNavController()
    val cars by carVM.filteredCars.collectAsState()

    // Para controlar la bottom bar y el login
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route
    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        carVM.loadCars()
    }

    Scaffold(
        bottomBar = {
            if (
                currentRoute == "principal" ||
                (user != null && currentRoute !in listOf(
                    "login",
                    "register",
                    "forgot_password",
                    "reset_confirmation"
                ))
            ) {
                BarraInferior(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (user != null) "principal" else "login",
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            composable("login") {
                LoginScreen(
                    onGoogleSignIn = { /*…*/ },
                    onLoginSuccess = {
                        navController.navigate("principal") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onRegisterClick = { navController.navigate("register") },
                    onForgotPasswordClick = { navController.navigate("forgot_password") },
                    onGuestAccess = {
                        navController.navigate("principal") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("register") {
                Registrarse(
                    onGoogleSignIn = { /*…*/ },
                    navController = navController,
                    onRegisterCompleted = {
                        navController.navigate("principal") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onGuestAccess = {
                        navController.navigate("principal") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                )
            }

            composable("forgot_password") {
                OlvidoContraseña(navController)
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
                    onApplyFilters = { marca, modelo, pMin, pMax, provincia, ciudad, añoMin, añoMax, kmMin, kmMax, combustible, color ->
                        carVM.applyFilters(
                            marca = marca,
                            modelo = modelo,
                            precioMin = pMin.toDoubleOrNull(),
                            precioMax = pMax.toDoubleOrNull(),
                            provincia = provincia,
                            ciudad = ciudad,
                            añoMin = añoMin.toIntOrNull(),
                            añoMax = añoMax.toIntOrNull(),
                            kmMin = kmMin.toIntOrNull(),
                            kmMax = kmMax.toIntOrNull(),
                            combustible = combustible,
                            color = color
                        )
                    }
                ) { id ->
                    navController.navigate("detail/$id")
                }
            }


            composable("vender") {
                RequireAuth(navController) {
                    Vender(onSubmit = { coche, uris ->
                        carVM.addCarWithImage(coche, uris)
                        navController.navigate("principal") {
                            popUpTo("principal") { inclusive = true }
                        }
                    })
                }
            }

            composable(
                "detail/{carId}",
                arguments = listOf(navArgument("carId") { type = NavType.StringType })
            ) { back ->
                RequireAuth(navController) {
                    val carId = back.arguments!!.getString("carId")!!
                    Detalle(
                        carId = carId,
                        onBack = { navController.popBackStack() },
                        onViewSeller = { uid -> navController.navigate("perfil/$uid") },
                        onContact = { chatId, cocheId, sellerId ->
                            navController.navigate("chat/$chatId/$cocheId/$sellerId")
                        }
                    )
                }
            }

            composable("chats") {
                RequireAuth(navController) {
                    ChatList(
                        navController = navController,
                    )
                }
            }

            composable("perfil/me") {
                RequireAuth(navController) {
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid
                    Perfil(
                        userId = uid,
                        onCarClick = { cid -> navController.navigate("detail/$cid") },
                        onLogout = {
                            navController.navigate("login") {
                                popUpTo("principal") { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable(
                "perfil/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { back ->
                RequireAuth(navController) {
                    val uid = back.arguments!!.getString("userId")!!
                    Perfil(
                        userId = uid,
                        onCarClick = { cid -> navController.navigate("detail/$cid") },
                        onLogout = {
                            navController.navigate("login") {
                                popUpTo("principal") { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable(
                "chat/{chatId}/{cocheId}/{sellerId}",
                arguments = listOf(
                    navArgument("chatId") { type = NavType.StringType },
                    navArgument("cocheId") { type = NavType.StringType },
                    navArgument("sellerId") { type = NavType.StringType }
                )
            ) { backStack ->
                RequireAuth(navController) {
                    val chatId = backStack.arguments!!.getString("chatId")!!
                    val cocheId = backStack.arguments!!.getString("cocheId")!!
                    val sellerId = backStack.arguments!!.getString("sellerId")!!

                    ChatPantalla(
                        chatId = chatId,
                        cocheId = cocheId,
                        sellerId = sellerId,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}