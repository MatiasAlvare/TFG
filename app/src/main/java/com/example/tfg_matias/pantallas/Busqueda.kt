@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)
package com.example.tfg_matias.pantallas

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable             // <<<<<<<<<<<<<<<<<
import androidx.compose.foundation.layout.FlowRow       // <<<<<<<<<<<<<<<<<
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tfg_matias.Model.Coche
import com.example.tfg_matias.R


@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun Busqueda(
    cars: List<Coche>,
    onApplyFilters: (
        marca: String,
        modelo: String,
        pMin: String,
        pMax: String,
        ubicacion: String,
        vendedorTipo: String,
        aMin: String,
        aMax: String,
        kmMin: String,
        kmMax: String,
        carroList: List<String>,
        compList: List<String>,
        soloElec: Boolean,
        equipList: List<String>,
        color: String
    ) -> Unit,
    onCarClick: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }

    // Estados de filtro
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var precioMin by remember { mutableStateOf("") }
    var precioMax by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var vendedorTipo by remember { mutableStateOf("Particular") }
    var annoMin by remember { mutableStateOf("") }
    var annoMax by remember { mutableStateOf("") }
    var kmMin by remember { mutableStateOf("") }
    var kmMax by remember { mutableStateOf("") }
    val carrocerias = listOf("SUV", "Berlina", "Familiar", "Coupe", "Todoterreno")
    val selectedCarrocerias = remember { mutableStateListOf<String>() }
    val combustibles = listOf("Gasolina", "Diésel", "Híbrido", "Eléctrico")
    val selectedCombustibles = remember { mutableStateListOf<String>() }
    var soloElectricos by remember { mutableStateOf(false) }
    val equipamientos = listOf("Aire Acondicionado", "Navegación", "Bluetooth", "Cámara")
    val selectedEquipamientos = remember { mutableStateListOf<String>() }
    val colores = listOf("Rojo", "Blanco", "Negro", "Azul", "Gris")
    var selectedColor by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        // Barra de búsqueda + botón filtros
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Escribe qué estás buscando") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            )
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = { showFilters = true }) {
                Icon(painterResource(R.drawable.ic_filtrar), contentDescription = "Filtros")
            }
        }

        // Grid de coches filtrados
        // Lista de coches filtrados en formato vertical (estilo AutoHero)
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
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
                        // Marca y modelo
                        OutlinedTextField(
                            value = marca,
                            onValueChange = { marca = it },
                            label = { Text("Marca") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = modelo,
                            onValueChange = { modelo = it },
                            label = { Text("Modelo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))

                        // Precio
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = precioMin,
                                onValueChange = { precioMin = it },
                                label = { Text("Desde") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = precioMax,
                                onValueChange = { precioMax = it },
                                label = { Text("Hasta") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(8.dp))

                        // Ubicación
                        OutlinedTextField(
                            value = ubicacion,
                            onValueChange = { ubicacion = it },
                            label = { Text("Ubicación") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))

                        // Vendedores
                        Text("Vendedores", style = MaterialTheme.typography.titleMedium)
                        FlowRow(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            listOf("Particular", "Concesionario", "Cualquiera").forEach { opt ->
                                Row(
                                    Modifier
                                        .clickable { vendedorTipo = opt }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = (vendedorTipo == opt),
                                        onClick = { vendedorTipo = opt }
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(opt)
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))

                        // Año
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = annoMin,
                                onValueChange = { annoMin = it },
                                label = { Text("Año min") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = annoMax,
                                onValueChange = { annoMax = it },
                                label = { Text("Año max") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(8.dp))

                        // Kilómetros
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = kmMin,
                                onValueChange = { kmMin = it },
                                label = { Text("Km min") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = kmMax,
                                onValueChange = { kmMax = it },
                                label = { Text("Km max") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(8.dp))

                        // Carrocería
                        Text("Carrocería", style = MaterialTheme.typography.titleMedium)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            carrocerias.forEach { opt ->
                                FilterChip(
                                    selected = selectedCarrocerias.contains(opt),
                                    onClick = {
                                        if (selectedCarrocerias.contains(opt)) selectedCarrocerias.remove(
                                            opt
                                        )
                                        else selectedCarrocerias.add(opt)
                                    },
                                    label = { Text(opt) }
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))

                        // Motor
                        Text("Motor", style = MaterialTheme.typography.titleMedium)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            combustibles.forEach { opt ->
                                FilterChip(
                                    selected = selectedCombustibles.contains(opt),
                                    onClick = {
                                        if (selectedCombustibles.contains(opt)) selectedCombustibles.remove(
                                            opt
                                        )
                                        else selectedCombustibles.add(opt)
                                    },
                                    label = { Text(opt) }
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))

                        // Eléctricos
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = soloElectricos,
                                onCheckedChange = { soloElectricos = it }
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Sólo eléctricos")
                        }
                        Spacer(Modifier.height(8.dp))

                        // Equipamiento
                        Text("Equipamiento", style = MaterialTheme.typography.titleMedium)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            equipamientos.forEach { opt ->
                                FilterChip(
                                    selected = selectedEquipamientos.contains(opt),
                                    onClick = {
                                        if (selectedEquipamientos.contains(opt)) selectedEquipamientos.remove(
                                            opt
                                        )
                                        else selectedEquipamientos.add(opt)
                                    },
                                    label = { Text(opt) }
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))

                        // Color
                        Text("Color", style = MaterialTheme.typography.titleMedium)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            colores.forEach { opt ->
                                FilterChip(
                                    selected = (selectedColor == opt),
                                    onClick = { selectedColor = opt },
                                    label = { Text(opt) }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        onApplyFilters(
                            marca,
                            modelo,
                            precioMin,
                            precioMax,
                            ubicacion,
                            vendedorTipo,
                            annoMin,
                            annoMax,
                            kmMin,
                            kmMax,
                            selectedCarrocerias.toList(),
                            selectedCombustibles.toList(),
                            soloElectricos,
                            selectedEquipamientos.toList(),
                            selectedColor
                        )
                        showFilters = false
                    }) {
                        Text("Aplicar filtros")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showFilters = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
