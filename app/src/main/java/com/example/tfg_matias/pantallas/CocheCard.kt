package com.example.tfg_matias.pantallas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tfg_matias.Model.Coche

@Composable
fun CocheCard(
    coche: Coche,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            AsyncImage(
                model = coche.imageUrl.ifBlank {
                    coche.fotos.firstOrNull() ?: "https://via.placeholder.com/600x400?text=Sin+imagen"
                },
                contentDescription = "Foto del coche",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(8.dp))

            Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {

                // Marca y modelo en azul oscuro
                Text(
                    "${coche.marca} ${coche.modelo}",
                    color = Color(0xFF1C1C1E),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(4.dp))

                // Precio en rojo fuerte
                Text(
                    "${coche.precio} €",
                    color = Color(0xFFCC0000), // Rojo destacado
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(4.dp))

                // Kilómetros, año y CV en gris oscuro
                Text(
                    "${coche.año} · ${coche.kilometros} km · ${coche.potenciaCv} CV",
                    color = Color(0xFF5A5A5A), // Gris oscuro
                    style = MaterialTheme.typography.bodyMedium
                )

                // Provincia y ciudad
                Text(
                    coche.provincia + if (coche.ciudad.isNotEmpty()) ", ${coche.ciudad}" else "",
                    color = Color(0xFF5A5A5A),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
