package com.example.tfg_matias.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.tfg_matias.Model.Coche
import com.example.tfg_matias.Model.Usuario
import com.example.tfg_matias.ViewModel.CarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Detalle(
    carId: String,
    onBack: () -> Unit,
    onViewSeller: (String) -> Unit
) {
    val vm: CarViewModel = viewModel()
    var coche by remember { mutableStateOf<Coche?>(null) }
    var vendedor by remember { mutableStateOf<Usuario?>(null) }

    LaunchedEffect(carId) {
        coche = vm.getCarById(carId)
        coche?.let { vendedor = vm.getUserProfile(it.ownerId) }
    }

    coche?.let { c ->
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("${c.precio} • ${c.tipo}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )

            LazyRow(
                modifier = Modifier
                    .height(200.dp)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(c.fotos) { url ->
                    Image(
                        painter = rememberAsyncImagePainter(url),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }

            // (aquí iría tu ficha técnica)

            vendedor?.let { u ->
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(Modifier.padding(16.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter(u.photoUrl),
                            contentDescription = "Foto de ${u.name}",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(24.dp))
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(u.name, style = MaterialTheme.typography.titleMedium)
                            Text("★ ${u.rating}", style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { onViewSeller(u.id) }) {
                                Text("Ver perfil")
                            }
                        }
                    }
                }
            }
        }
    }
}
