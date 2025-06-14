// EditarCoches.kt: copia exacta de Vender.kt con datos precargados y edición habilitada

package com.example.tfg_matias.pantallas


import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tfg_matias.Model.Coche
import com.example.tfg_matias.ViewModel.CarViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

@Composable
fun EditarCoche(carId: String, navController: NavController, carVM: CarViewModel = viewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    // Estados idénticos a Vender.kt
    val marcas = listOf(
        "Abarth", "Alfa Romeo", "Audi", "BMW", "Chevrolet", "Citroën", "Cupra", "Dacia",
        "Fiat", "Ford", "Honda", "Hyundai", "Jaguar", "Jeep", "Kia", "Lancia", "Land Rover",
        "Lexus", "Mazda", "Mercedes-Benz", "Mini", "Mitsubishi", "Nissan", "Opel",
        "Peugeot", "Porsche", "Renault", "Seat", "Skoda", "Smart", "SsangYong",
        "Subaru", "Suzuki", "Tesla", "Toyota", "Volkswagen", "Volvo"
    )
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

    var coche by remember { mutableStateOf<Coche?>(null) }

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
    var etiqueta by remember { mutableStateOf("") }
    var automatico by remember { mutableStateOf<Boolean?>(null) }

    var imageUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var principalIndex by remember { mutableIntStateOf(0) }
    var showErrorDialog by remember { mutableStateOf(false) }


    // Bloque que lanza el selector de imágenes múltiples
    val picker = rememberLauncherForActivityResult(GetMultipleContents()) { uris ->
        uris.forEach { uri ->
            scope.launch {
                try {
                    val ref = FirebaseStorage.getInstance().reference
                        .child("cars/${System.currentTimeMillis()}.jpg")// Ruta única con timestamp
                    ref.putFile(uri).await()// Sube la imagen a Firebase Storage
                    val url = ref.downloadUrl.await().toString()// Obtiene la URL pública
                    imageUrls = imageUrls + url // Añade la URL a la lista de imágenes visibles
                } catch (e: Exception) {
                    Toast.makeText(context, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    // Lógica que carga los datos del coche actual al entrar
    LaunchedEffect(carId) {
        try {
            val car = withContext(Dispatchers.IO) {
                carVM.getCarById(carId)
            }
            if (car == null) {
                Toast.makeText(context, "No se encontró el coche", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
                return@LaunchedEffect
            }
            // Cargamos todos los campos del coche a variables locales
            coche = car
            marca = car.marca
            modelo = car.modelo
            anio = car.año
            provincia = car.provincia
            ciudad = car.ciudad
            combustible = car.combustible
            puertas = car.puertas.toString()
            plazas = car.plazas.toString()
            cilindrada = car.cilindrada.toString()
            potencia = car.potencia?.toString() ?: ""
            color = car.color
            kilometros = car.kilometros.toString()
            precio = car.precio.toString()
            descripcion = car.descripcion
            etiqueta = car.etiqueta
            automatico = car.automatico
            imageUrls = car.fotos
        } catch (e: Exception) {
            Toast.makeText(context, "Error al cargar el coche", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }


    // Contenedor principal con scroll vertical y padding
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {

        // Título de la pantalla
        Text(
            "Editar publicación",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        // Sección de fotos del coche
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFEDEDED))
                .padding(12.dp)
        ) {
            Text(
                "Fotos del coche",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Ordenar imágenes para que la principal vaya primero
            val sortedUrls = buildList {
                imageUrls.getOrNull(principalIndex)?.let { add(it) }
                addAll(imageUrls.filterIndexed { i, _ -> i != principalIndex })
            }
            val chunkedUrls = sortedUrls.chunked(2) // Agrupar en filas de 2

            // Mostrar miniaturas agrupadas por fila
            chunkedUrls.forEachIndexed { rowIndex, rowImages ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowImages.forEach { url ->
                        val index = imageUrls.indexOf(url)
                        val isPrincipal = index == principalIndex
                        val label = if (url == imageUrls.getOrNull(principalIndex)) "PRINCIPAL" else "${sortedUrls.indexOf(url) + 1}"

                        // Tarjeta de imagen
                        Box(Modifier.weight(1f)) {
                            Card(
                                modifier = Modifier.height(160.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(Modifier.fillMaxSize()) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable { principalIndex = index }
                                    )
                                    Text(
                                        label,
                                        color = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .background(if (isPrincipal) Color.Black else Color.Gray)
                                            .padding(4.dp)
                                    )
                                }
                            }

                            // Botón para eliminar la imagen
                            IconButton(
                                onClick = {
                                    imageUrls =
                                        imageUrls.toMutableList().also { it.removeAt(index) }
                                    if (principalIndex >= imageUrls.size) principalIndex = 0
                                },
                                modifier = Modifier
                                    .offset(x = (-12).dp, y = 12.dp)
                                    .zIndex(1f)
                                    .align(Alignment.TopEnd)
                            ) {
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    modifier = Modifier.size(28.dp)
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
                    }

                    if (rowImages.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Tarjeta para añadir más imágenes
            Card(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(140.dp)
                    .clickable { picker.launch("image/*") },
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

        Spacer(Modifier.height(12.dp))

        // Campos de texto y desplegables
        DesplegableCampo("Marca", marca, marcas) { marca = it }
        OutlinedTextField(
            value = modelo,
            onValueChange = { modelo = it },
            label = { Text("Modelo") },
            modifier = Modifier.fillMaxWidth()
        )
        DesplegableCampo("Año", anio, años.map { it.toString() }) { anio = it }
        DesplegableCampo("Provincia", provincia, provincias.keys.sorted()) { provincia = it }
        if (provincia.isNotBlank()) DesplegableCampo(
            "Ciudad",
            ciudad,
            provincias[provincia] ?: emptyList()
        ) { ciudad = it }
        DesplegableCampo("Combustible", combustible, combustibles) { combustible = it }

        Spacer(Modifier.height(12.dp))

        // Selector de tipo de cambio automático/manual
        Text("Tipo de cambio", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(50)).background(Color(0xFFE0E0E0))
                .padding(4.dp)
        ) {
            Box(
                Modifier.weight(1f).clip(RoundedCornerShape(50))
                    .background(if (automatico == true) Color.Black else Color(0xFFE0E0E0))
                    .clickable { automatico = true }.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Automático", color = if (automatico == true) Color.White else Color.Black)
            }
            Box(
                Modifier.weight(1f).clip(RoundedCornerShape(50))
                    .background(if (automatico == false) Color.Black else Color(0xFFE0E0E0))
                    .clickable { automatico = false }.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Manual", color = if (automatico == false) Color.White else Color.Black)
            }
        }

        // Selector de etiqueta ambiental
        val etiquetas = listOf(
            "🟦 Etiqueta CERO",
            "🟢🟦 Etiqueta ECO",
            "🟢 Etiqueta C",
            "🟡 Etiqueta B",
            "🚫 Sin etiqueta"
        )
        DesplegableCampo("Etiqueta", etiqueta, etiquetas) { etiqueta = it }

        // Campos numéricos
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

        Spacer(Modifier.height(12.dp))

        // Estado para controlar si mostrar el diálogo de confirmación al borrar campos
        var showClearDialog by remember { mutableStateOf(false) }


        // Botón para solicitar borrar todos los campos del formulario
        Button(
            onClick = {
                showClearDialog = true // Activa el diálogo
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            )
        ) {
            Text("Borrar campos")
        }

        // Diálogo de confirmación de borrado de todos los campos
        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text("¿Borrar todos los campos?") },
                text = { Text("Esta acción eliminará todo lo editado, incluidas las fotos cargadas. ¿Deseas continuar?") },
                confirmButton = {
                    Button(onClick = {
                        // Resetea todos los campos
                        marca = ""; modelo = ""; anio = ""; provincia = ""; ciudad = "";
                        combustible = ""; puertas = ""; plazas = ""; cilindrada = ""; potencia = "";
                        color = ""; kilometros = ""; precio = ""; descripcion = ""; etiqueta = "";
                        automatico = null; imageUrls = emptyList(); principalIndex = 0
                        showClearDialog = false
                    }) {
                        Text("Sí, borrar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Botón para guardar los cambios realizados en Firestore
        Button(
            onClick = {
                // Validación: si hay algún campo obligatorio vacío, muestra error
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

                // Construcción del mapa de datos con valores convertidos
                val db = FirebaseFirestore.getInstance()
                val datos = mapOf(
                    "marca" to marca,
                    "modelo" to modelo,
                    "año" to anio,
                    "provincia" to provincia,
                    "ciudad" to ciudad,
                    "combustible" to combustible,
                    "puertas" to puertas.toIntOrNull(),
                    "plazas" to plazas.toIntOrNull(),
                    "cilindrada" to cilindrada.toIntOrNull(),
                    "potencia" to potencia.toIntOrNull(),
                    "color" to color,
                    "kilometros" to kilometros.toIntOrNull(),
                    "precio" to precio.toDoubleOrNull(),
                    "descripcion" to descripcion,
                    "etiqueta" to etiqueta,
                    "automatico" to automatico,
                    "fotos" to imageUrls,
                    "imageUrl" to imageUrls.getOrNull(principalIndex).orEmpty()
                )

                // Guardar cambios en Firestore
                scope.launch {
                    db.collection("cars").document(carId).update(datos).addOnSuccessListener {
                        Toast.makeText(context, "✔ Cambios guardados", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }.addOnFailureListener {
                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text("Guardar cambios")
        }


        // Botón para cancelar la edición y volver atrás
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Gray,
                contentColor = Color.White
            )
        ) {
            Text("Cancelar edición")
        }

        // Diálogo para mostrar si hay campos obligatorios sin rellenar
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Error") },
                text = { Text("Tienes que rellenar todos los campos.") },
                confirmButton = {
                    Button(onClick = { showErrorDialog = false }) {
                        Text("Aceptar")
                    }
                })
        }
    }
}