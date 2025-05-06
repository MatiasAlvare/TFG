package com.example.tfg_matias.pantallas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Lista de chats con cada vendedor (por userId).
 */
@Composable
fun ChatList(
    navController: NavController,
    users: List<String>
) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        items(users) { uid ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { navController.navigate("chat/$uid") }
            ) {
                Text("Chat con $uid", Modifier.padding(16.dp))
            }
        }
    }
}