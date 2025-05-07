package com.example.tfg_matias.pantallas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tfg_matias.Model.Coche

@Composable
fun CocheCard(
    coche: Coche,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors()
    ) {
        Column {
            AsyncImage(
                model = coche.fotos.firstOrNull(),
                contentDescription = "${coche.marca} ${coche.modelo}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Spacer(Modifier.height(8.dp))
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                    text = "${coche.marca} ${coche.modelo}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${coche.precio} €",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
