// ✅ Código COMPLETO Busqueda.kt con filtros alineados con la publicación

@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)
package com.example.tfg_matias.pantallas

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tfg_matias.Model.Coche
import com.example.tfg_matias.R
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun Busqueda(
    cars: List<Coche>,
    onApplyFilters: (
        marca: String,
        modelo: String,
        precioMin: String,
        precioMax: String,
        provincia: String,
        ciudad: String,
        añoMin: String,
        añoMax: String,
        kmMin: String,
        kmMax: String,
        combustible: String,
        color: String
    ) -> Unit,
    onCarClick: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }

    // Filtros
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var precioMin by remember { mutableStateOf("") }
    var precioMax by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var añoMin by remember { mutableStateOf("") }
    var añoMax by remember { mutableStateOf("") }
    var kmMin by remember { mutableStateOf("") }
    var kmMax by remember { mutableStateOf("") }
    var combustible by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        // Logo + título
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_carflow),
                contentDescription = "Logo CarFlow",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(45.dp))
            Text(
                text = "Coches publicados",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            )
        }

        // Buscador + botón de filtros
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .border(
                        width = 1.5.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.Black),
                    decorationBox = { innerTextField ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            if (query.isEmpty()) {
                                Text(
                                    "Buscar coches",
                                    color = Color(0xFF666666),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            IconButton(onClick = { showFilters = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filtrar),
                    contentDescription = "Filtros",
                    modifier = Modifier.size(30.dp)
                )
            }
        }


        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            val filtered = cars.filter {
                "${it.marca} ${it.modelo}".contains(query, ignoreCase = true)
            }
            items(filtered, key = { it.id }) { coche ->
                CocheCard(
                    coche = coche,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    onCarClick(coche.id)
                }
            }
        }

        if (showFilters) {
            AlertDialog(
                onDismissRequest = { showFilters = false },
                title = { Text("Filtros", style = MaterialTheme.typography.titleLarge) },
                text = {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text("Marca y modelo", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo") }, modifier = Modifier.fillMaxWidth())

                        Spacer(Modifier.height(8.dp))
                        Text("Precio (€)", style = MaterialTheme.typography.titleMedium)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = precioMin, onValueChange = { precioMin = it }, label = { Text("Mínimo") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                            OutlinedTextField(value = precioMax, onValueChange = { precioMax = it }, label = { Text("Máximo") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                        }

                        Spacer(Modifier.height(8.dp))
                        Text("Provincia y ciudad", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(value = provincia, onValueChange = { provincia = it }, label = { Text("Provincia") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = ciudad, onValueChange = { ciudad = it }, label = { Text("Ciudad") }, modifier = Modifier.fillMaxWidth())

                        Spacer(Modifier.height(8.dp))
                        Text("Año de matriculación", style = MaterialTheme.typography.titleMedium)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = añoMin, onValueChange = { añoMin = it }, label = { Text("Desde") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                            OutlinedTextField(value = añoMax, onValueChange = { añoMax = it }, label = { Text("Hasta") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                        }

                        Spacer(Modifier.height(8.dp))
                        Text("Kilómetros", style = MaterialTheme.typography.titleMedium)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = kmMin, onValueChange = { kmMin = it }, label = { Text("Mínimo") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                            OutlinedTextField(value = kmMax, onValueChange = { kmMax = it }, label = { Text("Máximo") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                        }

                        Spacer(Modifier.height(8.dp))
                        Text("Combustible", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(value = combustible, onValueChange = { combustible = it }, label = { Text("Tipo de combustible") }, modifier = Modifier.fillMaxWidth())

                        Spacer(Modifier.height(8.dp))
                        Text("Color", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    Column {
                        Button(
                            onClick = {
                                onApplyFilters(
                                    marca, modelo, precioMin, precioMax, provincia, ciudad,
                                    añoMin, añoMax, kmMin, kmMax, combustible, color
                                )
                                showFilters = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Aplicar filtros")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                // Limpiar todas las variables a vacío
                                marca = ""
                                modelo = ""
                                precioMin = ""
                                precioMax = ""
                                provincia = ""
                                ciudad = ""
                                añoMin = ""
                                añoMax = ""
                                kmMin = ""
                                kmMax = ""
                                combustible = ""
                                color = ""

                                // Aplicar filtros vacíos (mostrar todos los coches)
                                onApplyFilters(
                                    "", "", "", "", "", "",
                                    "", "", "", "", "", ""
                                )
                                showFilters = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Limpiar filtros")
                        }
                    }
                }
            )
        }
    }
}
