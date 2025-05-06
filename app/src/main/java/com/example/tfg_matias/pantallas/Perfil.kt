package com.example.tfg_matias.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.tfg_matias.ViewModel.CarViewModel


@Composable
fun Perfil(
    userId: String,
    onCarClick: (String) -> Unit
) {
    val vm: CarViewModel = viewModel()

    // Cargamos datos SOLO UNA VEZ al cambiar userId
    LaunchedEffect(userId) {
        vm.loadUserData(userId)
    }

    // Recogemos perfil y comentarios desde el ViewModel
    val usuario    by vm.selectedProfile.collectAsState()
    val comentarios by vm.selectedComments.collectAsState()

    // Lista de coches filtrada en memoria
    val coches by remember {
        derivedStateOf { vm.cars.value.filter { it.ownerId == userId } }
    }

    // Si perfil todavía no llegó, mostramos spinner
    if (usuario == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(usuario!!.photoUrl),
                    contentDescription = "Avatar de ${usuario!!.name}",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.height(8.dp))
                Text(usuario!!.name, style = MaterialTheme.typography.titleLarge)
                Text("★ ${usuario!!.rating}", style = MaterialTheme.typography.bodyMedium)
            }
        }

        if (coches.isNotEmpty()) {
            item { Text("Coches publicados", style = MaterialTheme.typography.titleMedium) }
            items(coches) { coche ->
                CocheCard(coche) { onCarClick(coche.id) }
            }
        }

        item { Text("Comentarios", style = MaterialTheme.typography.titleMedium) }
        items(comentarios) { c ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(c.text, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = c.timestamp.toDate().toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
