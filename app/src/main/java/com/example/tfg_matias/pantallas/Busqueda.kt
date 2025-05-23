// ✅ Código COMPLETO Busqueda.kt con todos los filtros alineados y funcionales


package com.example.tfg_matias.pantallas


import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tfg_matias.Model.Coche
import com.example.tfg_matias.R


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
        color: String,
        automatico: String,
        puertas: String,
        cilindrada: String
    ) -> Unit,
    onCarClick: (String) -> Unit
) {
    val marcas = listOf("Abarth", "Alfa Romeo", "Audi", "BMW", "Chevrolet", "Citroën", "Cupra", "Dacia",
        "Fiat", "Ford", "Honda", "Hyundai", "Jaguar", "Jeep", "Kia", "Lancia", "Land Rover",
        "Lexus", "Mazda", "Mercedes-Benz", "Mini", "Mitsubishi", "Nissan", "Opel",
        "Peugeot", "Porsche", "Renault", "Seat", "Skoda", "Smart", "SsangYong",
        "Subaru", "Suzuki", "Tesla", "Toyota", "Volkswagen", "Volvo")
    val colores = listOf("Blanco", "Negro", "Gris", "Rojo", "Azul", "Verde", "Amarillo", "Naranja", "Marrón", "Beige")
    val combustibles = listOf("Gasolina", "Diésel", "Eléctrico", "Híbrido")
    val provincias = listOf("Madrid", "Barcelona", "Valencia", "Sevilla", "Zaragoza")
    val años = (1975..2025).map { it.toString() }.reversed()
    val tiposCambio = listOf("Automático", "Manual")

    var query by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var etiqueta by remember { mutableStateOf("") }


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
    var automatico by remember { mutableStateOf("") }
    var puertas by remember { mutableStateOf("") }
    var cilindrada by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_carflow),
                contentDescription = null,
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(45.dp))
            Text(
                text = "Coches publicados",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f).height(40.dp)
                    .border(1.5.dp, Color.Black, RoundedCornerShape(50))
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
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            if (query.isEmpty()) {
                                Text("Buscar coches", color = Color.Gray)
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
                title = {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Filtros", style = MaterialTheme.typography.titleLarge)
                        IconButton(onClick = { showFilters = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar filtros",
                                tint = Color.Black
                            )
                        }
                    }
                },
                text = {
                    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {

                        Text("Marca y modelo", style = MaterialTheme.typography.titleMedium)
                        DesplegableCampo("Marca", marca, marcas) { marca = it }
                        OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo") }, modifier = Modifier.fillMaxWidth())

                        Spacer(Modifier.height(8.dp))
                        Text("Precio (€)", style = MaterialTheme.typography.titleMedium)
                        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = precioMin, onValueChange = { precioMin = it }, label = { Text("Mínimo") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                            OutlinedTextField(value = precioMax, onValueChange = { precioMax = it }, label = { Text("Máximo") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                        }

                        Spacer(Modifier.height(8.dp))
                        Text("Provincia y ciudad", style = MaterialTheme.typography.titleMedium)
                        DesplegableCampo("Provincia", provincia, provincias) { provincia = it }
                        OutlinedTextField(value = ciudad, onValueChange = { ciudad = it }, label = { Text("Ciudad") }, modifier = Modifier.fillMaxWidth())

                        Spacer(Modifier.height(8.dp))
                        Text("Año de matriculación", style = MaterialTheme.typography.titleMedium)
                        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                            DesplegableCampo("Desde", añoMin, años) { añoMin = it }
                            DesplegableCampo("Hasta", añoMax, años) { añoMax = it }
                        }

                        Text("Kilómetros", style = MaterialTheme.typography.titleMedium)
                        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = kmMin, onValueChange = { kmMin = it }, label = { Text("Mínimo") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                            OutlinedTextField(value = kmMax, onValueChange = { kmMax = it }, label = { Text("Máximo") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                        }

                        Spacer(Modifier.height(8.dp))
                        Text("Combustible", style = MaterialTheme.typography.titleMedium)
                        DesplegableCampo("Tipo de combustible", combustible, combustibles) { combustible = it }

                        Spacer(Modifier.height(8.dp))
                        Text("Color", style = MaterialTheme.typography.titleMedium)
                        DesplegableCampo("Color", color, colores) { color = it }

                        Spacer(Modifier.height(8.dp))
                        Text("Etiqueta medioambiental", style = MaterialTheme.typography.titleMedium)

                        val etiquetas = listOf(
                            "🟦 Etiqueta CERO",
                            "🟢🟦 Etiqueta ECO",
                            "🟢 Etiqueta C",
                            "🟡 Etiqueta B",
                            "🚫 Sin etiqueta"
                        )

                        DesplegableCampo("Etiqueta", etiqueta, etiquetas) { etiqueta = it }


                        Spacer(Modifier.height(8.dp))
                        Text("Tipo de cambio", style = MaterialTheme.typography.titleMedium)
                        DesplegableCampo("Cambio", automatico, tiposCambio) { automatico = it }

                        Spacer(Modifier.height(8.dp))
                        Text("Puertas y cilindrada", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(value = puertas, onValueChange = { puertas = it }, label = { Text("Número de puertas") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = cilindrada, onValueChange = { cilindrada = it }, label = { Text("Cilindrada (cc)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    Column {
                        Button(
                            onClick = {
                                onApplyFilters(marca, modelo, precioMin, precioMax, provincia, ciudad, añoMin, añoMax, kmMin, kmMax, combustible, color, automatico, puertas, cilindrada)
                                showFilters = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
                        ) {
                            Text("Aplicar filtros")
                        }

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = {
                                marca = ""; modelo = ""; precioMin = ""; precioMax = ""
                                provincia = ""; ciudad = ""; añoMin = ""; añoMax = ""
                                kmMin = ""; kmMax = ""; combustible = ""; color = ""
                                automatico = ""; puertas = ""; cilindrada = ""
                                onApplyFilters("", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
                                showFilters = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
                        ) {
                            Text("Limpiar filtros")
                        }
                    }
                }
            )
        }
    }
}