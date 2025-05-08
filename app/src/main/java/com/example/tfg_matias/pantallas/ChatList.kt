// ✅ ChatList.kt actualizado: oculta chats cuyo coche ya no existe

package com.example.tfg_matias.pantallas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    // Filtrar chats que SÍ tienen coche asociado y que no ha sido eliminado
    val chatsFiltrados = chats.filter { chat ->
        cars.any { it.id == chat.cocheId }
    }

    if (chatsFiltrados.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No tienes conversaciones activas.")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(chatsFiltrados) { chat ->
                val coche = cars.find { it.id == chat.cocheId }

                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            val sellerId = chat.participants.firstOrNull { it != com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid }
                            if (sellerId != null) {
                                navController.navigate("chat/${chat.chatId}/${chat.cocheId}/$sellerId")
                            }
                        }
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = coche?.imageUrl ?: "",
                            contentDescription = null,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(coche?.let { "${it.marca} ${it.modelo}" } ?: "Coche eliminado", style = MaterialTheme.typography.titleMedium)
                            if (chat.lastMessage.isNotEmpty()) {
                                Spacer(Modifier.height(4.dp))
                                Text(chat.lastMessage, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}