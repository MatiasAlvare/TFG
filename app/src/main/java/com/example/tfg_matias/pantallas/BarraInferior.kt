package com.example.tfg_matias.ui.theme

// Importaciones necesarias para la barra de navegación inferior
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

// Composable que representa la barra de navegación inferior
@Composable
fun BarraInferior(
    navController: NavController, // Controlador de navegación
    chatVM: ChatViewModel         // ViewModel para chats, necesario para contar mensajes no leídos
) {
    val user = FirebaseAuth.getInstance().currentUser // Usuario actual
    val unreadCount by chatVM.unreadCount.collectAsState() // Conteo de mensajes no leídos

    // Lista de secciones en la barra inferior
    val items = listOf(
        BottomNavItem("Inicio", "principal", R.drawable.ic_coche), // Sección de inicio
        BottomNavItem("Vender", "vender", R.drawable.ic_vender_coche), // Sección de venta
        BottomNavItem("Chats", "chats", R.drawable.comentario), // Sección de chats
        BottomNavItem("Perfil", "perfil/me", R.drawable.ic_usuario) // Perfil del usuario
    )

    // Barra inferior visual
    NavigationBar {
        items.forEach { item ->
            val isChatTab = item.route == "chats" // Verifica si es la pestaña de chats

            NavigationBarItem(
                icon = {
                    // Si hay mensajes no leídos, muestra badge encima del icono
                    if (isChatTab && unreadCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge {
                                    Text(unreadCount.toString()) // Muestra el número
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
                        // Si no hay mensajes no leídos o no es la pestaña de chats
                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = { Text(item.label) }, // Etiqueta de la pestaña
                selected = false, // No se maneja la pestaña seleccionada (puede añadirse si se desea)
                onClick = {
                    if (user != null || item.route == "principal") {
                        // Si el usuario está logueado o es la pestaña principal, navega normalmente
                        navController.navigate(item.route) {
                            launchSingleTop = true // Evita duplicación en el backstack
                        }
                    } else {
                        // Si no está logueado, redirige a login
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}

// Clase de datos que representa un ítem de la barra inferior
data class BottomNavItem(
    val label: String,  // Nombre visible del ítem
    val route: String,  // Ruta de navegación asociada
    val iconRes: Int    // Recurso del icono
)
