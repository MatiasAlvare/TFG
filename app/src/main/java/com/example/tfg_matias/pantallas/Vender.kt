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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.tfg_matias.utilidades.RequireAuth
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Vender(onSubmit: (Coche) -> Unit) {
    // Envuelto en RequireAuth en MainActivity, así user no es null
    val context = LocalContext.current

    // Guardar URIs como Strings para rememberSaveable
    var photoUris by rememberSaveable { mutableStateOf(listOf<String>()) }
    // launcher para seleccionar múltiples imágenes
    val photoPicker = rememberLauncherForActivityResult(GetMultipleContents()) { uris: List<Uri> ->
        photoUris = photoUris + uris.map { it.toString() }
    }

    val scrollState = rememberScrollState()

    // ... otros estados existentes ...
    var tipo by remember { mutableStateOf("Coche / 4×4") }
    var expandedTipo by remember { mutableStateOf(false) }
    val tipoOptions = listOf("Coche / 4×4", "Todoterreno", "SUV", "Familiar")

    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var carroceria by remember { mutableStateOf("") }
    var combustible by remember { mutableStateOf("") }
    var ano by remember { mutableStateOf("") }
    var version by remember { mutableStateOf("") }
    val transmissionOptions = listOf("Manual", "Automático")
    var selectedTransmission by remember { mutableStateOf(transmissionOptions[1]) }
    var etiqueta by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var kilometros by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- Tipo ---
        Text("Tipo de vehículo", style = MaterialTheme.typography.titleMedium)
        ExposedDropdownMenuBox(
            expanded = expandedTipo,
            onExpandedChange = { expandedTipo = it }
        ) {
            TextField(
                value = tipo,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedTipo,
                onDismissRequest = { expandedTipo = false }
            ) {
                tipoOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            tipo = option
                            expandedTipo = false
                        }
                    )
                }
            }
        }

        // --- Fotos ---
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
                        painter = painterResource(id = R.drawable.imagenes),
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
                    items(photoUris) { uriString ->
                        val uri = Uri.parse(uriString)
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

        // --- Campos básicos ---
        OutlinedTextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = carroceria, onValueChange = { carroceria = it }, label = { Text("Carrocería") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = combustible, onValueChange = { combustible = it }, label = { Text("Combustible") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = ano, onValueChange = { ano = it }, label = { Text("Año") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = version, onValueChange = { version = it }, label = { Text("Versión") }, modifier = Modifier.fillMaxWidth())

        // --- Transmisión ---
        Text("Tipo de cambio", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            transmissionOptions.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable { selectedTransmission = option }
                ) {
                    RadioButton(selected = (selectedTransmission == option), onClick = { selectedTransmission = option })
                    Spacer(Modifier.width(4.dp))
                    Text(option)
                }
            }
        }

        // --- Más campos ---
        OutlinedTextField(value = etiqueta, onValueChange = { etiqueta = it }, label = { Text("Etiqueta medioambiental") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color exterior") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = kilometros, onValueChange = { kilometros = it }, label = { Text("Kilómetros") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = matricula, onValueChange = { matricula = it }, label = { Text("Matrícula") }, modifier = Modifier.fillMaxWidth())
        Text("Datos adicionales", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, modifier = Modifier.fillMaxWidth().height(120.dp), placeholder = { Text("¿Ha pasado la última ITV? ...") })

        Spacer(Modifier.height(24.dp))

        // --- Botón Publicar ---
        Button(
            onClick = {
                try {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val ownerId = currentUser?.uid ?: ""
                    val nuevo = Coche(
                        id = "",
                        ownerId = ownerId,
                        tipo = tipo,
                        fotos = photoUris,
                        marca = marca,
                        modelo = modelo,
                        carroceria = carroceria,
                        combustible = combustible,
                        año = ano,
                        version = version,
                        automatico = (selectedTransmission == "Automático"),
                        etiqueta = etiqueta,
                        color = color,
                        kilometros = kilometros.toIntOrNull() ?: 0,
                        precio = precio.toDoubleOrNull() ?: 0.0,
                        matricula = matricula,
                        descripcion = descripcion
                    )
                    onSubmit(nuevo)
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Publicar coche")
        }
    }
}
