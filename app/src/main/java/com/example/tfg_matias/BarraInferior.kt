package com.example.tfg_matias

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth

@Composable
fun BarraInferior(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser

    val items = listOf(
        BottomNavItem("principal",
            icon  = { Icon(Icons.Default.Home, contentDescription = "Inicio", Modifier.size(24.dp)) },
            label = "Inicio"
        ),
        BottomNavItem("vender",
            icon  = { Icon(painter = painterResource(id = R.drawable.ic_vender_coche), contentDescription = "Vender", Modifier.size(24.dp)) },
            label = "Vender"
        ),
        BottomNavItem("chats",
            icon  = { Icon(painter = painterResource(id = R.drawable.comentario), contentDescription = "Chats", Modifier.size(24.dp)) },
            label = "Chats"
        ),
        BottomNavItem("perfil/me",
            icon  = { Icon(Icons.Default.Person, contentDescription = "Perfil", Modifier.size(24.dp)) },
            label = "Perfil"
        )
    )

    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = item.icon,
                label = { Text(item.label) },
                selected = when {
                    item.route == currentRoute -> true
                    item.route == "perfil/me" && currentRoute?.startsWith("perfil/") == true -> true
                    else -> false
                },
                onClick = {
                    // Para las rutas protegidas, redirigir a login si no hay user
                    val protected = item.route != "principal"
                    if (protected && user == null) {
                        navController.navigate("login") {
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(item.route) {
                            popUpTo("principal") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: @Composable () -> Unit,
    val label: String
)
