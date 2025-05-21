package com.example.tfg_matias.pantallas

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    var chatIdToDelete by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        chatVM.loadChats()
        carVM.loadCars()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Título
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_carflow),
                        contentDescription = "Logo CarFlow",
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .clickable { navController.navigate("principal") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No tienes conversaciones activas",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
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

                    val backgroundColor by animateColorAsState(
                        targetValue = if (hasUnread)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else
                            Color(0xFFF5F5F5),
                        label = "chatBackgroundColor"
                    )

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
                            .border(
                                width = 2.dp,
                                color = if (hasUnread) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = backgroundColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
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
                                coche?.let {
                                    Text("${it.marca} ${it.modelo}", style = MaterialTheme.typography.titleMedium)
                                }
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

                            Spacer(Modifier.width(8.dp))

                            IconButton(onClick = {
                                chatIdToDelete = chat.chatId
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar chat"
                                )
                            }
                        }
                    }
                }
            }
        }

        // Diálogo de confirmación de borrado
        chatIdToDelete?.let { chatId ->
            AlertDialog(
                onDismissRequest = { chatIdToDelete = null },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = { Text("¿Seguro que quieres borrar el chat?") },
                text = {
                    Text("Esta acción eliminará la conversación de forma permanente de tu bandeja. No podrás recuperarla.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            chatVM.deleteChat(chatId)
                            chatIdToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
                    ) {
                        Text("Sí")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { chatIdToDelete = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
                    ) {
                        Text("No")
                    }
                }
            )
        }
    }
}
