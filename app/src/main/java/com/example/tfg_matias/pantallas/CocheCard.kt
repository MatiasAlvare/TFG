package com.example.tfg_matias.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.tfg_matias.Model.Coche

@Composable
fun CocheCard(
    coche: Coche,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Imagen principal si la hay
            if (coche.fotos.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(coche.fotos[0]),
                    contentDescription = "${coche.marca} ${coche.modelo}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            Column(Modifier.padding(12.dp)) {
                // Título: marca + modelo
                Text(
                    text = "${coche.marca} ${coche.modelo}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                // Precio formateado
                Text(
                    text = "${coche.precio} €",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}
