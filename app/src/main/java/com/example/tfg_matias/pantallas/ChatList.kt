package com.example.tfg_matias.pantallas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tfg_matias.R
import com.example.tfg_matias.ViewModel.CarViewModel
import com.example.tfg_matias.utilidades.ChatViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChatList(
    navController: NavController,
    chatVM: ChatViewModel = viewModel(),
    carVM: CarViewModel = viewModel()
) {
    val chats by chatVM.chatList.collectAsState()
    val cars by carVM.cars.collectAsState()
    val unreadCounts by chatVM.globalUnreadCounts.collectAsState()

    LaunchedEffect(Unit) {
        chatVM.loadChats()
        carVM.loadCars()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // ✅ Título fijo arriba
        Text(
            text = "Mis mensajes",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            textAlign = TextAlign.Center
        )

        if (chats.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes conversaciones activas.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(chats, key = { it.chatId }) { chat ->
                    val coche = cars.find { it.id == chat.cocheId }
                    val lastMessage = chat.lastMessage
                    val unreadCount = unreadCounts[chat.chatId]?.count ?: 0
                    val hasUnread = unreadCount > 0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                val sellerId = chat.participants.firstOrNull {
                                    it != FirebaseAuth.getInstance().currentUser?.uid
                                }
                                if (sellerId != null) {
                                    chatVM.markMessagesAsSeen(chat.chatId)
                                    navController.navigate("chat/${chat.chatId}/${chat.cocheId}/$sellerId")
                                }
                            }
                            .then(
                                if (hasUnread)
                                    Modifier.border(
                                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                else Modifier
                            ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            Modifier
                                .background(
                                    if (hasUnread)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    else
                                        MaterialTheme.colorScheme.surface
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = coche?.imageUrl
                                    ?: "https://via.placeholder.com/150x100?text=Sin+imagen",
                                contentDescription = null,
                                modifier = Modifier.size(80.dp)
                            )

                            Spacer(Modifier.width(12.dp))

                            Column(Modifier.weight(1f)) {
                                Text(
                                    coche?.let { "${it.marca} ${it.modelo}" } ?: "Coche eliminado",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                if (lastMessage.isNotEmpty()) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(lastMessage, style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            if (unreadCount > 0) {
                                Spacer(Modifier.width(8.dp))
                                Badge(containerColor = MaterialTheme.colorScheme.error) {
                                    Text(unreadCount.toString())
                                }
                            }

                            IconButton(onClick = {
                                chatVM.deleteChat(chat.chatId)
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_eliminar),
                                    contentDescription = "Eliminar chat"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
