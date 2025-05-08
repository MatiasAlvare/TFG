@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
package com.example.tfg_matias.pantallas

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tfg_matias.Model.Coche
import com.example.tfg_matias.R
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@Composable
fun Vender(onSubmit: (Coche, List<Uri>) -> Unit) {
    val context = LocalContext.current

    val marcas = listOf(
        "Abarth", "Alfa Romeo", "Audi", "BMW", "Citroën", "Cupra", "Dacia", "DS", "Fiat",
        "Ford", "Honda", "Hyundai", "Jaguar", "Jeep", "Kia", "Mazda", "Mercedes-Benz", "Mini",
        "Mitsubishi", "Nissan", "Opel", "Peugeot", "Porsche", "Renault", "Seat", "Skoda",
        "Subaru", "Suzuki", "Tesla", "Toyota", "Volkswagen", "Volvo"
    ).sorted()

    val combustibles = listOf("Gasolina", "Diésel", "Eléctrico", "Híbrido")
    val años = (1975..Calendar.getInstance().get(Calendar.YEAR)).toList().reversed()
    val provincias = mapOf(
        "Madrid" to listOf("Madrid", "Alcalá de Henares", "Getafe", "Leganés", "Móstoles"),
        "Barcelona" to listOf("Barcelona", "Hospitalet", "Sabadell", "Terrassa")
    )
    val todasProvincias = provincias.keys.toList().sorted()

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

    var marcaExpanded by remember { mutableStateOf(false) }
    var anioExpanded by remember { mutableStateOf(false) }
    var provinciaExpanded by remember { mutableStateOf(false) }
    var ciudadExpanded by remember { mutableStateOf(false) }
    var combustibleExpanded by remember { mutableStateOf(false) }

    var photoUris by remember { mutableStateOf(listOf<Uri>()) }
    val photoPicker = rememberLauncherForActivityResult(GetMultipleContents()) { uris: List<Uri> ->
        println("🔥 URI SELECCIONADOS: $uris")
        photoUris = photoUris + uris
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Fotos
        Text("Fotos de tu coche", style = MaterialTheme.typography.titleMedium)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            if (photoUris.isEmpty()) {
                IconButton(onClick = { photoPicker.launch("image/*") }) {
                    Icon(
                        painterResource(id = R.drawable.imagenes),
                        contentDescription = "Subir fotos",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray
                    )
                }
            } else {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    items(photoUris) { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .padding(end = 8.dp)
                                .clickable { photoPicker.launch("image/*") }
                        )
                    }
                }
            }
        }

        // Marca
        ExposedDropdownMenuBox(
            expanded = marcaExpanded,
            onExpandedChange = { marcaExpanded = !marcaExpanded }
        ) {
            OutlinedTextField(
                value = marca,
                onValueChange = { marca = it },
                readOnly = true,
                label = { Text("Marca") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = marcaExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = marcaExpanded,
                onDismissRequest = { marcaExpanded = false }
            ) {
                marcas.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            marca = selectionOption
                            marcaExpanded = false
                        }
                    )
                }
            }
        }

        // Modelo
        OutlinedTextField(
            value = modelo,
            onValueChange = { modelo = it },
            label = { Text("Modelo") },
            modifier = Modifier.fillMaxWidth()
        )

        // Año
        ExposedDropdownMenuBox(
            expanded = anioExpanded,
            onExpandedChange = { anioExpanded = !anioExpanded }
        ) {
            OutlinedTextField(
                value = anio,
                onValueChange = { anio = it },
                readOnly = true,
                label = { Text("Año") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = anioExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = anioExpanded,
                onDismissRequest = { anioExpanded = false }
            ) {
                años.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption.toString()) },
                        onClick = {
                            anio = selectionOption.toString()
                            anioExpanded = false
                        }
                    )
                }
            }
        }

        // Provincia
        ExposedDropdownMenuBox(
            expanded = provinciaExpanded,
            onExpandedChange = { provinciaExpanded = !provinciaExpanded }
        ) {
            OutlinedTextField(
                value = provincia,
                onValueChange = { provincia = it },
                readOnly = true,
                label = { Text("Provincia") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = provinciaExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = provinciaExpanded,
                onDismissRequest = { provinciaExpanded = false }
            ) {
                todasProvincias.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            provincia = selectionOption
                            provinciaExpanded = false
                            ciudad = ""
                        }
                    )
                }
            }
        }

        // Ciudad
        if (provincias[provincia] != null) {
            ExposedDropdownMenuBox(
                expanded = ciudadExpanded,
                onExpandedChange = { ciudadExpanded = !ciudadExpanded }
            ) {
                OutlinedTextField(
                    value = ciudad,
                    onValueChange = { ciudad = it },
                    readOnly = true,
                    label = { Text("Ciudad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ciudadExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = ciudadExpanded,
                    onDismissRequest = { ciudadExpanded = false }
                ) {
                    provincias[provincia]?.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                ciudad = selectionOption
                                ciudadExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Combustible
        ExposedDropdownMenuBox(
            expanded = combustibleExpanded,
            onExpandedChange = { combustibleExpanded = !combustibleExpanded }
        ) {
            OutlinedTextField(
                value = combustible,
                onValueChange = { combustible = it },
                readOnly = true,
                label = { Text("Combustible") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = combustibleExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = combustibleExpanded,
                onDismissRequest = { combustibleExpanded = false }
            ) {
                combustibles.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            combustible = selectionOption
                            combustibleExpanded = false
                        }
                    )
                }
            }
        }

        // Otros campos
        OutlinedTextField(value = puertas, onValueChange = { puertas = it }, label = { Text("Puertas") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = plazas, onValueChange = { plazas = it }, label = { Text("Plazas") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = cilindrada, onValueChange = { cilindrada = it }, label = { Text("Cilindrada (cc)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = potencia, onValueChange = { potencia = it }, label = { Text("Potencia (CV)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = kilometros, onValueChange = { kilometros = it }, label = { Text("Kilómetros") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())

        // Publicar
        Button(
            onClick = {
                try {
                    val nuevo = Coche(
                        id = "",
                        ownerId = FirebaseAuth.getInstance().currentUser!!.uid,
                        tipo = "Venta",
                        fotos = photoUris.map { it.toString() },
                        marca = marca,
                        modelo = modelo,
                        carroceria = "",
                        combustible = combustible,
                        año = anio,
                        automatico = false,
                        etiqueta = "",
                        color = color,
                        puertas = puertas.toIntOrNull() ?: 0,
                        plazas = plazas.toIntOrNull() ?: 0,
                        cilindrada = cilindrada.toIntOrNull() ?: 0,
                        potenciaCv = potencia.toIntOrNull() ?: 0,
                        kilometros = kilometros.toIntOrNull() ?: 0,
                        precio = precio.toDoubleOrNull() ?: 0.0,
                        descripcion = descripcion,
                        provincia = provincia,
                        ciudad = ciudad,
                        imageUrl = photoUris.firstOrNull()?.toString() ?: ""
                    )
                    onSubmit(nuevo, photoUris)
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Publicar coche")
        }
    }
}
