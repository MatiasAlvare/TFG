package com.example.tfg_matias.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tfg_matias.Model.Coche
import com.example.tfg_matias.R

/**
 * Pantalla principal (home) que muestra los coches cargados desde Firestore.
 *
 * @param cars Lista de coches obtenida del ViewModel.
 * @param onSell Acción para navegar a la pantalla de publicar coche.
 * @param onCarClick Acción al pulsar un coche (navegar a detalle).
 */
@Composable
fun Principal(
    cars: List<Coche>,
    onSell: () -> Unit,
    onCarClick: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = cars.filter { it.tipo.contains(query, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "CARFLOW",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Buscar...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filtrar),
                    contentDescription = "Filtrar resultados",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
            }
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtered) { coche ->
                CocheCard(
                    coche = coche,
                    onClick = { onCarClick(coche.id) }
                )
            }
        }

        Button(
            onClick = onSell,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Vender tu coche")
        }
    }
}
