// Detalle.kt completo con botón de retroceso flotante, bloques estilizados y etiquetas listas

@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tfg_matias.pantallas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties



@Composable
fun Detalle(
    carId: String,
    onBack: () -> Unit,
    onViewSeller: (String) -> Unit,
    onContact: (chatId: String, cocheId: String, sellerId: String) -> Unit
) {
    val vm: CarViewModel = viewModel()
    val chatVM: ChatViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid

    var coche by remember { mutableStateOf<Coche?>(null) }
    var vendedor by remember { mutableStateOf<Usuario?>(null) }
    var isOwnCar by remember { mutableStateOf(false) }
    var showProfileImage by remember { mutableStateOf(false) }
    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
    var showLoginPrompt by remember { mutableStateOf(false) }



    var showGallery by remember { mutableStateOf(false) }
    var initialPage by remember { mutableStateOf(0) }

    LaunchedEffect(carId) {
        coche = vm.getCarById(carId)
        coche?.let {
            vendedor = vm.getUserById(it.ownerId)
            isOwnCar = (it.ownerId == currentUser)
        }
    }

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
                                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
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
                    Text("${c.marca} ${c.modelo} ${c.carroceria}".trim(), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
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

            // Vendedor
            Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(6.dp)) {
                Row(Modifier.padding(16.dp)) {
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
                        Icon(
                            painter = painterResource(R.drawable.ic_usuario),
                            contentDescription = "Foto",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(30.dp))
                        )
                    }

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
                        Text(vendedor?.name ?: "Usuario no disponible", style = MaterialTheme.typography.titleMedium)
                        val mediaValoracion = vendedor?.comentarios
                            ?.map { it.valoracion }
                            ?.filter { it > 0 }
                            ?.average()
                            ?.takeUnless { it.isNaN() }
                            ?: 0.0

                        Text("★ ${"%.1f".format(mediaValoracion)}", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        if (vendedor != null) {
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
                                Text("Este es tu anuncio", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
        if (showLoginPrompt) {
            AlertDialog(
                onDismissRequest = { showLoginPrompt = false },
                title = { Text("Acceso requerido") },
                text = { Text("Debes registrarte o iniciar sesión para continuar.") },
                confirmButton = {
                    Button(onClick = {
                        showLoginPrompt = false
                        onBack()  // Opcional: vuelve atrás o usa navController.navigate("register")
                    }) {
                        Text("Regístrate")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = {
                        showLoginPrompt = false
                        onBack()  // Opcional: vuelve atrás o usa navController.navigate("login")
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
