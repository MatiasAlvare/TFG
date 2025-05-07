package com.example.tfg_matias.ui.theme  // Ajusta el package según tu proyecto

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tfg_matias.R
import com.google.firebase.auth.FirebaseAuth

data class BottomNavItem(
    val label: String,
    val route: String,
    val iconRes: Int
)

@Composable
fun BarraInferior(navController: NavController) {
    // ¿Hay usuario logueado?
    val user = FirebaseAuth.getInstance().currentUser

    // Ruta actual
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    // Definimos nuestros 4 botones
    val items = listOf(
        BottomNavItem("Inicio",  "principal", R.drawable.ic_coche),
        BottomNavItem("Vender",  "vender",    R.drawable.ic_vender_coche),
        BottomNavItem("Chats",   "chats",     R.drawable.comentario),
        BottomNavItem("Perfil",  "perfil/me", R.drawable.ic_usuario)
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)      // tamaño estándar
                    )
                },
                label     = { androidx.compose.material3.Text(item.label) },
                selected  = currentRoute == item.route,
                onClick   = {
                    if (item.route == "principal") {
                        // Siempre permito "Inicio"
                        navController.navigate(item.route) {
                            launchSingleTop = true
                        }
                    } else if (user != null) {
                        // Si está logueado, permito las demás pestañas
                        navController.navigate(item.route) {
                            launchSingleTop = true
                        }
                    } else {
                        // Si no hay sesión, fuerzo al login
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}
