// ✅ ChatList.kt actualizado: NO se eliminan chats huérfanos, se muestran con aviso

package com.example.tfg_matias.pantallas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tfg_matias.ViewModel.CarViewModel
import com.example.tfg_matias.ViewModel.ChatViewModel

@Composable
fun ChatList(
    navController: NavController,
    chatVM: ChatViewModel = viewModel(),
    carVM: CarViewModel = viewModel()
) {
    val chats by chatVM.chatList.collectAsState()
    val cars by carVM.cars.collectAsState()

    LaunchedEffect(Unit) {
        chatVM.loadChats()
        carVM.loadCars()
    }

    if (chats.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No tienes conversaciones activas.")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(chats) { chat ->
                val coche = cars.find { it.id == chat.cocheId }

                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable(enabled = coche != null) {
                            val sellerId = chat.participants.firstOrNull { it != com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid }
                            if (sellerId != null) {
                                navController.navigate("chat/${chat.chatId}/${chat.cocheId}/$sellerId")
                            }
                        }
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = coche?.imageUrl ?: "https://via.placeholder.com/150x100?text=Sin+imagen",
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .fillMaxHeight()
                                .weight(1f),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(2f)) {
                            Text(
                                coche?.let { "${it.marca} ${it.modelo}" } ?: "Coche eliminado",
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (chat.lastMessage.isNotEmpty()) {
                                Spacer(Modifier.height(4.dp))
                                Text(chat.lastMessage, style = MaterialTheme.typography.bodySmall)
                            }
                            if (coche == null) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "Este coche ya no está disponible.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
