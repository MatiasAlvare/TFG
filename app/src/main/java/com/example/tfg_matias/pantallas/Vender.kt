// ✅ Vender.kt actualizado con botón 'Borrar campos', selector 'Tipo de cambio', y título ajustado

@file:OptIn(
    ExperimentalMaterial3Api::class
)
package com.example.tfg_matias.pantallas

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.tfg_matias.Model.Coche
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@Composable
fun DesplegableCampo(label: String, valor: String, opciones: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(text = { Text(opcion) }, onClick = {
                    onSelect(opcion)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun Vender(onSubmit: (Coche, List<Uri>) -> Unit) {

    val marcas = listOf("Abarth", "Alfa Romeo", "Audi", "BMW", "Chevrolet", "Citroën", "Cupra", "Dacia",
        "Fiat", "Ford", "Honda", "Hyundai", "Jaguar", "Jeep", "Kia", "Lancia", "Land Rover",
        "Lexus", "Mazda", "Mercedes-Benz", "Mini", "Mitsubishi", "Nissan", "Opel",
        "Peugeot", "Porsche", "Renault", "Seat", "Skoda", "Smart", "SsangYong",
        "Subaru", "Suzuki", "Tesla", "Toyota", "Volkswagen", "Volvo")
    val años = (1975..Calendar.getInstance().get(Calendar.YEAR)).toList().reversed()
    val combustibles = listOf("Gasolina", "Diésel", "Eléctrico", "Híbrido")
    val provincias = mapOf(
        "Madrid" to listOf("Madrid", "Alcalá de Henares", "Móstoles", "Leganés", "Getafe"),
        "Barcelona" to listOf("Barcelona", "Hospitalet", "Terrassa", "Badalona"),
        "Valencia" to listOf("Valencia", "Torrent", "Gandía"),
        "Sevilla" to listOf("Sevilla", "Dos Hermanas", "Alcalá de Guadaíra"),
        "Zaragoza" to listOf("Zaragoza", "Calatayud"),
        "Málaga" to listOf("Málaga", "Marbella", "Fuengirola"),
        "Alicante" to listOf("Alicante", "Elche", "Benidorm"),
        "Murcia" to listOf("Murcia", "Cartagena"),
        "Cádiz" to listOf("Cádiz", "Jerez", "Algeciras"),
        "Granada" to listOf("Granada", "Motril", "Baza"),
        "Vizcaya" to listOf("Bilbao", "Barakaldo", "Getxo"),
        "La Coruña" to listOf("A Coruña", "Santiago", "Ferrol")
    )
    val colores = listOf(
        "Blanco", "Negro", "Gris", "Rojo", "Azul",
        "Verde", "Amarillo", "Naranja", "Marrón", "Beige"
    )


    var showErrorDialog by remember { mutableStateOf(false) }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var combustible by remember { mutableStateOf("") }
    var puertas by remember { mutableStateOf("") }
    var plazas by remember { mutableStateOf("") }
    var cilindrada by remember { mutableStateOf("") }
    var potencia by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var kilometros by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var automatico by remember { mutableStateOf<Boolean?>(null) }
    var photoUris by remember { mutableStateOf(listOf<Uri>()) }
    var principalIndex by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }


    val photoPicker = rememberLauncherForActivityResult(GetMultipleContents()) { uris ->
        photoUris = photoUris + uris
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text(
            "Publica tu coche en CarFlow",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFEDEDED)).padding(12.dp)) {
            Text("Fotos de tu coche", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val sortedUris = if (photoUris.isNotEmpty()) listOf(photoUris[principalIndex]) + photoUris.filterIndexed { i, _ -> i != principalIndex } else emptyList()
                sortedUris.chunked(2).forEachIndexed { rowIndex, rowUris ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowUris.forEachIndexed { i, uri ->
                            val index = photoUris.indexOf(uri)
                            val isPrincipal = index == principalIndex
                            val number = sortedUris.indexOf(uri) + 1
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(160.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(Modifier.fillMaxSize()) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable { principalIndex = index }
                                    )
                                    Text(
                                        if (isPrincipal) "PRINCIPAL" else "$number",
                                        color = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .background(if (isPrincipal) Color.Black else Color.Gray)
                                            .padding(4.dp)
                                    )
                                }
                            }

                            // Botón de eliminar externo
                            Box(
                                modifier = Modifier
                                    .offset(x = (-12).dp, y = 12.dp) // separa la papelera del borde
                                    .zIndex(1f) // asegúrate que esté encima
                            ) {
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable {
                                            photoUris = photoUris.toMutableList().also { it.removeAt(index) }
                                            if (principalIndex >= photoUris.size) principalIndex = 0
                                        }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = Color.Black,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }

                        }
                        if (rowUris.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Card(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(140.dp)
                        .clickable { photoPicker.launch("image/*") },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "+",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.Black
                            )
                            Text(
                                "Añadir más",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        DesplegableCampo("Selecciona marca", marca, marcas) { marca = it }
        OutlinedTextField(
            value = modelo,
            onValueChange = { modelo = it },
            label = { Text("Modelo") },
            modifier = Modifier.fillMaxWidth()
        )
        DesplegableCampo("Selecciona año", anio, años.map { it.toString() }) { anio = it }
        DesplegableCampo("Selecciona provincia", provincia, provincias.keys.sorted()) {
            provincia = it
        }
        if (provincia.isNotBlank()) {
            DesplegableCampo(
                "Selecciona ciudad",
                ciudad,
                provincias[provincia] ?: emptyList()
            ) { ciudad = it }
        }
        DesplegableCampo("Combustible", combustible, combustibles) { combustible = it }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Tipo de cambio", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(50)).background(Color(0xFFE0E0E0))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(50))
                    .background(if (automatico == true) Color.Black else Color(0xFFE0E0E0))
                    .clickable { automatico = true }.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Automático",
                    color = if (automatico == true) Color.White else Color.Black,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(50))
                    .background(if (automatico == false) Color.Black else Color(0xFFE0E0E0))
                    .clickable { automatico = false }.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Manual",
                    color = if (automatico == false) Color.White else Color.Black,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val etiquetas = listOf(
            "🟦 Etiqueta CERO",
            "🟢🟦 Etiqueta ECO",
            "🟢 Etiqueta C",
            "🟡 Etiqueta B",
            "🚫 Sin etiqueta"
        )
        var etiqueta by remember { mutableStateOf("") }
        DesplegableCampo("Etiqueta", etiqueta, etiquetas) { etiqueta = it }

        OutlinedTextField(
            value = puertas,
            onValueChange = { puertas = it },
            label = { Text("Puertas") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = plazas,
            onValueChange = { plazas = it },
            label = { Text("Plazas") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = cilindrada,
            onValueChange = { cilindrada = it },
            label = { Text("Cilindrada (cc)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = potencia,
            onValueChange = { potencia = it },
            label = { Text("Potencia (CV)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        DesplegableCampo("Color", color, colores) { color = it }

        OutlinedTextField(
            value = kilometros,
            onValueChange = { kilometros = it },
            label = { Text("Kilómetros") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { showClearDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text("Borrar campos")
        }

        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text("¿Borrar todos los campos?") },
                text = { Text("Esta acción eliminará todo lo introducido, incluidas las fotos cargadas. ¿Deseas continuar?") },
                confirmButton = {
                    TextButton(onClick = {
                        marca = ""; modelo = ""; anio = ""; provincia = ""; ciudad = ""
                        combustible = ""; puertas = ""; plazas = ""; cilindrada = ""; potencia = ""
                        color = ""; kilometros = ""; precio = ""; descripcion = ""; photoUris = emptyList()
                        principalIndex = 0; automatico = null; etiqueta = ""
                        showClearDialog = false
                    }) {
                        Text("Sí, borrar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }


        Button(
            onClick = {
                if (listOf(
                        marca,
                        modelo,
                        anio,
                        provincia,
                        ciudad,
                        combustible,
                        puertas,
                        plazas,
                        cilindrada,
                        potencia,
                        color,
                        kilometros,
                        precio,
                        descripcion
                    ).any { it.isBlank() }
                ) {
                    showErrorDialog = true
                    return@Button
                }

                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                val coche = Coche(
                    id = "",
                    ownerId = uid,
                    tipo = "Venta",
                    fotos = listOf(),
                    marca = marca,
                    modelo = modelo,
                    carroceria = "",
                    combustible = combustible,
                    año = anio,
                    automatico = automatico ?: false,
                    etiqueta = etiqueta,
                    color = color,
                    puertas = puertas.toIntOrNull() ?: 0,
                    plazas = plazas.toIntOrNull() ?: 0,
                    cilindrada = cilindrada.toIntOrNull() ?: 0,
                    potencia = potencia.toIntOrNull() ?: 0,
                    kilometros = kilometros.toIntOrNull() ?: 0,
                    precio = precio.toDoubleOrNull() ?: 0.0,
                    descripcion = descripcion,
                    provincia = provincia,
                    ciudad = ciudad,
                    imageUrl = ""
                )
                onSubmit(coche, photoUris)
                showDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            )
        ) {
            Text("Publicar coche")
        }

// Dialogo de éxito
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("¡Coche publicado!") },
                text = { Text("Tu coche ha sido publicado correctamente.") },
                confirmButton = {

                    Button(onClick = { showDialog = false }) {
                        Text("Aceptar")
                    }
                }
            )
        }

// Dialogo de error si faltan campos
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Error") },
                text = { Text("Tienes que rellenar todos los campos para poder publicar tu coche") },
                confirmButton = {
                    Button(
                        onClick = { showErrorDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}