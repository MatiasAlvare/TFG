package com.example.tfg_matias.ui.theme

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tfg_matias.R
import com.example.tfg_matias.utilidades.ChatViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun BarraInferior(
    navController: NavController,
    chatVM: ChatViewModel
) {
    val user = FirebaseAuth.getInstance().currentUser
    val unreadCount by chatVM.unreadCount.collectAsState()

    val items = listOf(
        BottomNavItem("Inicio", "principal", R.drawable.ic_coche),
        BottomNavItem("Vender", "vender", R.drawable.ic_vender_coche),
        BottomNavItem("Chats", "chats", R.drawable.comentario),
        BottomNavItem("Perfil", "perfil/me", R.drawable.ic_usuario)
    )

    NavigationBar {
        items.forEach { item ->
            val isChatTab = item.route == "chats"
            NavigationBarItem(
                icon = {
                    if (isChatTab && unreadCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge {
                                    Text(unreadCount.toString())
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = item.iconRes),
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = { Text(item.label) },
                selected = false,
                onClick = {
                    if (user != null || item.route == "principal") {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val route: String,
    val iconRes: Int
)
