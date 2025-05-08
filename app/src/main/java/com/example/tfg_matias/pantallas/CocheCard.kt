// ✅ CocheCard.kt actualizado para aceptar 'modifier'

package com.example.tfg_matias.pantallas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Imagen del coche
            AsyncImage(
                model = coche.fotos.firstOrNull() ?: "https://via.placeholder.com/600x400?text=Sin+imagen",
                contentDescription = "Foto del coche",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(8.dp))

            Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    "${coche.marca} ${coche.modelo}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))

                Text(
                    "${coche.precio} €",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))

                Text(
                    "${coche.año} • ${coche.kilometros} km • ${coche.potenciaCv} CV",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    coche.provincia + (if (coche.ciudad.isNotEmpty()) ", ${coche.ciudad}" else ""),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
