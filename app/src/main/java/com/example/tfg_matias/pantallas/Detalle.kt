// Detalle.kt completo con botón de retroceso flotante, bloques estilizados y etiquetas listas

package com.example.tfg_matias.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tfg_matias.Model.Coche
import com.example.tfg_matias.Model.Usuario
import com.example.tfg_matias.R
import com.example.tfg_matias.ViewModel.CarViewModel
import com.example.tfg_matias.utilidades.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController




// Función Composable que muestra la pantalla de detalle de un coche
@Composable
fun Detalle(
    carId: String, // ID del coche a mostrar
    onBack: () -> Unit, // Acción al pulsar el botón de retroceso
    onViewSeller: (String) -> Unit, // Acción al pulsar "Ver perfil"
    onContact: (chatId: String, cocheId: String, sellerId: String) -> Unit, // Acción al pulsar "Contactar"
    navController: NavController // Controlador de navegación
) {
    val vm: CarViewModel = viewModel() // ViewModel para acceder a los datos del coche
    val chatVM: ChatViewModel = viewModel() // ViewModel para la lógica del chat
    val coroutineScope = rememberCoroutineScope() // Para lanzar corrutinas
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid // UID del usuario actual

    var coche by remember { mutableStateOf<Coche?>(null) } // Coche actual
    var vendedor by remember { mutableStateOf<Usuario?>(null) } // Usuario que publicó el coche
    var isOwnCar by remember { mutableStateOf(false) } // Si el coche pertenece al usuario actual
    var showProfileImage by remember { mutableStateOf(false) } // Controla si mostrar imagen ampliada
    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null // Verifica si hay sesión iniciada
    var showLoginPrompt by remember { mutableStateOf(false) } // Controla si mostrar aviso de login

    var showGallery by remember { mutableStateOf(false) } // Controla si mostrar galería de imágenes
    var initialPage by remember { mutableStateOf(0) } // Página inicial en la galería

    // Efecto lanzado al montar el Composable: carga el coche y el vendedor
    LaunchedEffect(carId) {
        coche = vm.getCarById(carId)
        coche?.let {
            vendedor = vm.getUserById(it.ownerId)
            isOwnCar = (it.ownerId == currentUser)
        }
    }

    // Si se ha cargado el coche, construimos la interfaz
    coche?.let { c ->
        val imagenes = if (c.fotos.isNotEmpty()) c.fotos else listOfNotNull(c.imageUrl.takeIf { it.isNotBlank() })
        var imagenSeleccionada by remember { mutableStateOf(imagenes.firstOrNull() ?: "") }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            // Imagen principal con botón flotante
            Box {
                AsyncImage(
                    model = imagenSeleccionada,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clickable {
                            initialPage = imagenes.indexOf(imagenSeleccionada)
                            showGallery = true
                        }
                )
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(Color.White.copy(alpha = 0.7f), shape = RoundedCornerShape(50))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.Black
                    )
                }
            }

            // Miniaturas
            LazyRow(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(imagenes) { _, url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { imagenSeleccionada = url }
                    )
                }
            }
            
            // Galería en pantalla completa
            if (showGallery) {
                Dialog(
                    onDismissRequest = { showGallery = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { imagenes.size })
                        Surface(shape = RoundedCornerShape(12.dp)) {
                            Box {
                                HorizontalPager(state = pagerState) { page ->
                                    AsyncImage(
                                        model = imagenes[page],
                                        contentDescription = null,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { showGallery = false },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(12.dp)
                                        .size(36.dp)
                                        .background(Color.White.copy(alpha = 0.9f), shape = CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Cerrar",
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Precio
            Card(
                Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("${c.marca} ${c.modelo}".trim(), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(8.dp))
                    Text("Precio al contado", style = MaterialTheme.typography.bodySmall)
                    Text("${c.precio} €", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }

            // Ficha técnica
            Card(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ficha técnica", style = MaterialTheme.typography.titleMedium)
                    HorizontalDivider()
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        FichaRow("Marca", c.marca, "Modelo", c.modelo)
                        FichaRow("Año", c.año, "Provincia", c.provincia)
                        if (c.ciudad.isNotEmpty()) FichaRow("Ciudad", c.ciudad, "Color", c.color)
                        FichaRow("Kilómetros", "${c.kilometros} km", "Combustible", c.combustible)
                        FichaRow("Cambio", if (c.automatico) "Automático" else "Manual", "Puertas", "${c.puertas}")
                        FichaRow("Plazas", "${c.plazas}", "Cilindrada", "${c.cilindrada} cc")
                        FichaRow("Potencia", "${c.potencia} CV", "Etiqueta", etiquetaVisual(c.etiqueta))
                    }
                }
            }

            // Descripción
            Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(6.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Descripción del anunciante", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(c.descripcion)
                }
            }

            // Tarjeta que muestra la información del vendedor
            Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(6.dp)) {
                Row(Modifier.padding(16.dp)) {
                    // Muestra la foto del vendedor si está disponible
                    if (vendedor?.photoUrl?.isNotBlank() == true) {
                        AsyncImage(
                            model = vendedor!!.photoUrl,
                            contentDescription = "Foto de ${vendedor!!.name}",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(30.dp))
                                .clickable { showProfileImage = true }
                        )
                    } else {
                        // Si no hay foto, se muestra un icono por defecto
                        Icon(
                            painter = painterResource(R.drawable.ic_usuario),
                            contentDescription = "Foto",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(30.dp))
                        )
                    }

                    // Muestra imagen ampliada si se ha pulsado
                    if (showProfileImage && vendedor?.photoUrl?.isNotBlank() == true) {
                        AlertDialog(
                            onDismissRequest = { showProfileImage = false },
                            confirmButton = {},
                            text = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = vendedor!!.photoUrl,
                                        contentDescription = "Foto ampliada",
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        )
                    }

                    Spacer(Modifier.width(12.dp))
                    Column {
                        // Muestra el nombre del vendedor o un texto por defecto
                        Text(vendedor?.name ?: "Usuario no disponible", style = MaterialTheme.typography.titleMedium)

                        // Calcula y muestra la valoración media del vendedor
                        val mediaValoracion = vendedor?.comentarios
                            ?.map { it.valoracion }
                            ?.filter { it > 0 }
                            ?.average()
                            ?.takeUnless { it.isNaN() }
                            ?: 0.0

                        Text("★ ${"%.1f".format(mediaValoracion)}", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        // Si hay datos del vendedor disponibles
                        if (vendedor != null) {
                            // Botón para ver perfil
                            Button(
                                onClick = { if (isLoggedIn) {
                                    onViewSeller(vendedor!!.id)
                                } else {
                                    showLoginPrompt = true
                                }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
                            ) {
                                Text("Ver perfil")
                            }
                            Spacer(Modifier.height(8.dp))

                            // Botón para contactar si no es el coche propio
                            if (!isOwnCar) {
                                Button(
                                    onClick = {
                                        if (isLoggedIn) {
                                            coroutineScope.launch {
                                                val chatId = chatVM.getOrCreateChat(c.id, c.ownerId)
                                                onContact(chatId, c.id, c.ownerId)
                                            }
                                        } else {
                                            showLoginPrompt = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
                                ) {
                                    Text("Contactar")
                                }
                            } else {
                                // Si es el propio anuncio, se muestra un aviso
                                Text("Este es tu anuncio", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        // Diálogo emergente si se intenta interactuar sin estar logueado
        if (showLoginPrompt) {
            AlertDialog(
                onDismissRequest = { showLoginPrompt = false },
                title = { Text("Acceso requerido") },
                text = { Text("Debes registrarte o iniciar sesión para continuar.") },
                confirmButton = {
                    Button(onClick = {
                        showLoginPrompt = false
                        navController.navigate("register")
                    }) {
                        Text("Regístrate")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = {
                        showLoginPrompt = false
                        navController.navigate("login")
                    }) {
                        Text("Inicia sesión")
                    }
                }
            )
        }
    } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

// Fila de dos atributos clave:valor en la ficha técnica
@Composable
fun FichaRow(label1: String, value1: String, label2: String, value2: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(Modifier.weight(1f)) {
            Text(label1, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text(value1, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(label2, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text(value2, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// Función que devuelve una etiqueta visual según el valor
@Composable
fun etiquetaVisual(valor: String): String {
    return when {
        valor.contains("CERO", ignoreCase = true) -> "🟦 CERO"
        valor.contains("ECO", ignoreCase = true) -> "🟢🟦 ECO"
        valor.contains("C (verde)", ignoreCase = true) -> "🟢 C"
        valor.contains("B", ignoreCase = true) -> "🟡 B"
        valor.contains("Sin", ignoreCase = true) -> "🚫"
        else -> valor
    }
}
