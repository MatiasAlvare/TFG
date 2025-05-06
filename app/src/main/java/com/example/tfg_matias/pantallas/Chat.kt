package com.example.tfg_matias.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Chat sencillo (mensajes en memoria).
 */
@Composable
fun Chat(
    userId: String
) {
    var messages by remember { mutableStateOf(listOf<String>()) }
    var draft by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(Modifier.weight(1f)) {
            items(messages) { msg -> Text(msg, Modifier.padding(4.dp)) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                if (draft.isNotBlank()) {
                    messages = messages + "Yo: $draft"
                    draft = ""
                }
            }) {
                Text("Enviar")
            }
        }
    }
}