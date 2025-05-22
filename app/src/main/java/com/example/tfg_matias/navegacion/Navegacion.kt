package com.example.tfg_matias.navegacion

import android.annotation.SuppressLint
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.tfg_matias.ViewModel.CarViewModel
import com.example.tfg_matias.utilidades.ChatViewModel
import com.example.tfg_matias.pantallas.*
import com.example.tfg_matias.ui.theme.BarraInferior
import com.example.tfg_matias.utilidades.RequireAuth
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ComposableDestinationInComposeScope")
@Composable
fun Navegacion(
    carVM: CarViewModel,
    initialChatId: String? = null,
    initialCocheId: String? = null,
    initialSellerId: String? = null
) {
    val navController = rememberNavController()
    val cars by carVM.filteredCars.collectAsState()
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route
    val user = FirebaseAuth.getInstance().currentUser

    val chatVM: ChatViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val unreadCount by chatVM.unreadCount.collectAsState()
    val previousUnreadCount = remember { mutableStateOf(0) }
    val context = LocalContext.current

    chatVM.setContext(context)

    LaunchedEffect(initialChatId, initialCocheId, initialSellerId) {
        if (!initialChatId.isNullOrBlank() && !initialCocheId.isNullOrBlank() && !initialSellerId.isNullOrBlank()) {
            navController.navigate("chat/$initialChatId/$initialCocheId/$initialSellerId") {
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(unreadCount) {
        if (unreadCount > previousUnreadCount.value) {
            val mensaje = if (unreadCount == 1) "📩 Tienes un mensaje nuevo" else "📬 Tienes $unreadCount mensajes nuevos"
            val result = snackbarHostState.showSnackbar(
                message = mensaje,
                actionLabel = "Ver",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed && navController.currentDestination?.route != "chats") {
                navController.navigate("chats") {
                    launchSingleTop = true
                }
            }
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (unreadCount == 1) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    val pattern = longArrayOf(0, 150, 100, 150)
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                }
            } else {
                vibrator.vibrate(300)
            }
            val notificationUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, notificationUri)
            ringtone.play()
        }
        previousUnreadCount.value = unreadCount
    }

    LaunchedEffect(Unit) {
        carVM.loadCars()
        chatVM.loadChats()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (currentRoute == "principal" || (user != null && currentRoute !in listOf("login", "register", "forgot_password", "reset_confirmation"))) {
                BarraInferior(navController, chatVM)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (user != null) "principal" else "login",
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            composable("login") {
                LoginScreen(
                    onGoogleSignIn = {},
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
                    onGoogleSignIn = {},
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
            composable("forgot_password") { OlvidoContraseña(navController) }
            composable("reset_confirmation") {
                EnlaceEnviado { navController.navigate("login") { popUpTo("login") { inclusive = true } } }
            }
            composable("principal") {
                Busqueda(cars = cars, onApplyFilters = { marca, modelo, pMin, pMax, provincia, ciudad, añoMin, añoMax, kmMin, kmMax, combustible, color, automatico, puertas, cilindrada ->
                    carVM.applyFilters(marca, modelo, pMin.toDoubleOrNull(), pMax.toDoubleOrNull(), provincia, ciudad, añoMin.toIntOrNull(), añoMax.toIntOrNull(), kmMin.toIntOrNull(), kmMax.toIntOrNull(), combustible, color, automatico, puertas.toIntOrNull(), cilindrada.toIntOrNull())
                }) { id -> navController.navigate("detail/$id") }
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
            composable("detail/{carId}", arguments = listOf(navArgument("carId") { type = NavType.StringType })) { back ->
                RequireAuth(navController) {
                    val carId = back.arguments!!.getString("carId")!!
                    Detalle(
                        carId = carId,
                        onBack = { navController.popBackStack() },
                        onViewSeller = { uid -> navController.navigate("perfil/$uid") },
                        onContact = { chatId, cocheId, sellerId -> navController.navigate("chat/$chatId/$cocheId/$sellerId") }
                    )
                }
            }
            composable("chats") {
                RequireAuth(navController) {
                    ChatList(navController = navController, chatVM = chatVM)
                }
            }
            composable("perfil/me") {
                RequireAuth(navController) {
                    val userId = FirebaseAuth.getInstance().currentUser!!.uid
                    Perfil(
                        userId = userId,
                        onCarClick = { id -> navController.navigate("detail/$id") },  // ✅ CORREGIDO: permite rutas como editar_coche
                        onLogout = { navController.navigate("login") },
                        onUserClick = { uid -> navController.navigate("perfil/$uid") }
                    )
                }
            }
            composable("perfil/{userId}", arguments = listOf(navArgument("userId") { type = NavType.StringType })) { back ->
                RequireAuth(navController) {
                    val userId = back.arguments!!.getString("userId")!!
                    Perfil(
                        userId = userId,
                        onCarClick = { id -> navController.navigate("detail/$id") },
                                onLogout = { navController.navigate("login") },
                        onUserClick = { uid -> navController.navigate("perfil/$uid") }
                    )
                }
            }
            composable("editar_coche/{id}") { backStackEntry ->
                val cocheId = backStackEntry.arguments?.getString("id") ?: return@composable
                EditarCoche(carId = cocheId, navController = navController)
            }

            composable("chat/{chatId}/{cocheId}/{sellerId}", arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("cocheId") { type = NavType.StringType },
                navArgument("sellerId") { type = NavType.StringType }
            )) { backStack ->
                RequireAuth(navController) {
                    val chatId = backStack.arguments!!.getString("chatId")!!
                    val cocheId = backStack.arguments!!.getString("cocheId")!!
                    val sellerId = backStack.arguments!!.getString("sellerId")!!
                    ChatPantalla(
                        chatId = chatId,
                        cocheId = cocheId,
                        sellerId = sellerId,
                        onBack = { navController.popBackStack() },
                        navController = navController,
                        chatVM = chatVM
                    )
                }
            }
        }
    }
}