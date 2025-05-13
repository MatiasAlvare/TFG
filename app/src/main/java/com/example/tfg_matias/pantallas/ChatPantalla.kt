package com.example.tfg_matias.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tfg_matias.Model.Coche
import com.example.tfg_matias.Model.Usuario
import com.example.tfg_matias.ViewModel.CarViewModel
import com.example.tfg_matias.utilidades.ChatViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPantalla(
    chatId: String,
    cocheId: String,
    sellerId: String,
    onBack: () -> Unit,
    navController: NavController,
    chatVM: ChatViewModel = viewModel(),
    carVM: CarViewModel = viewModel()
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val messages by chatVM.messages.collectAsState()
    var text by remember { mutableStateOf("") }

    var coche by remember { mutableStateOf<Coche?>(null) }
    var vendedor by remember { mutableStateOf<Usuario?>(null) }

    // Cargar mensajes al entrar
    LaunchedEffect(Unit) {
        chatVM.loadMessages(chatId)
        chatVM.markMessagesAsSeen(chatId)
    }


    // Marcar como leídos cuando llegan mensajes
    LaunchedEffect(messages) {
        chatVM.markMessagesAsSeen(chatId)
    }

    LaunchedEffect(cocheId) {
        coche = carVM.getCarById(cocheId)
    }

    LaunchedEffect(sellerId) {
        vendedor = carVM.getUserById(sellerId)
    }

    Column(Modifier.fillMaxSize()) {

        // Encabezado del chat
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                chatVM.markMessagesAsSeen(chatId)
                onBack()
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }

            vendedor?.let { user ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { navController.navigate("perfil/${user.id}") }
                        .weight(1f)
                ) {
                    AsyncImage(
                        model = user.photoUrl,
                        contentDescription = "Foto vendedor",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(user.name, fontWeight = FontWeight.Bold)
                        Text("★ ${user.valoracion} (${user.comentarios.size})")
                    }
                }
            }

            coche?.let { car ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { navController.navigate("detail/${car.id}") }
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${car.marca} ${car.modelo}")
                        Text("${car.precio} €", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(8.dp))
                    AsyncImage(
                        model = car.fotos.firstOrNull(),
                        contentDescription = "Foto coche",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }

        // Lista de mensajes
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { msg ->
                val isMe = msg.senderId == currentUserId
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
                        if (!isMe) {
                            AsyncImage(
                                model = vendedor?.photoUrl,
                                contentDescription = "Foto vendedor",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                msg.text,
                                modifier = Modifier.padding(8.dp),
                                color = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Enviado",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        // Campo de entrada de texto
        Row(
            Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Escribe tu mensaje...") },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (text.isNotBlank()) {
                    chatVM.sendMessage(chatId, text, cocheId, sellerId)
                    text = ""
                }
            }) {
                Text("Enviar")
            }
        }
    }
}
