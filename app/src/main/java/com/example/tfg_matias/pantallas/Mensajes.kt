package com.example.tfg_matias.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tfg_matias.R
import com.example.tfg_matias.ViewModel.CarViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Mensajes(
    navController: NavController,
    carViewModel: CarViewModel = viewModel()
) {
    val currentUser = FirebaseAuth.getInstance().currentUser

    if (currentUser == null) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("register") },
                modifier = Modifier.fillMaxWidth(0.8f).padding(8.dp)
            ) { Text("Regístrate") }
            OutlinedButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth(0.8f).padding(8.dp)
            ) { Text("Inicia Sesión") }
        }
    } else {
        val cars by carViewModel.cars.collectAsState()
        val chats = cars.map { it.ownerId }
            .distinct()
            .filter { it != currentUser.uid }

        if (chats.isEmpty()) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_sin_comentarios),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text("¿Ningún mensaje?", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Comienza tus conversaciones contactando con el vendedor",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Button(
                    onClick = { navController.navigate("principal") },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Empieza a buscar")
                }
            }
        } else {
            ChatList(navController, chats)
        }
    }
}
