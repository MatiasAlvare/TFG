// ✅ Detalle.kt actualizado: agregado chequeo robusto en Contactar

package com.example.tfg_matias.pantallas

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.tfg_matias.Model.Coche
import com.example.tfg_matias.Model.Usuario
import com.example.tfg_matias.R
import com.example.tfg_matias.ViewModel.CarViewModel
import com.example.tfg_matias.utilidades.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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

    LaunchedEffect(carId) {
        coche = vm.getCarById(carId)
        coche?.let {
            vendedor = vm.getUserById(it.ownerId)
            isOwnCar = (it.ownerId == currentUser)
        }
    }

    @Composable
    fun FichaItem(label: String, value: String) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }

    coche?.let { c ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            TopAppBar(
                title = { Text("${c.precio} € • ${c.marca} ${c.modelo}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )

            LazyRow(
                modifier = Modifier
                    .height(220.dp)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(c.fotos) { url ->
                    Image(
                        painter = rememberAsyncImagePainter(url),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(220.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ficha técnica", style = MaterialTheme.typography.titleMedium)
                    HorizontalDivider()

                    FichaItem("Marca", c.marca)
                    FichaItem("Modelo", c.modelo)
                    FichaItem("Año", c.año)
                    FichaItem("Provincia", c.provincia)
                    if (c.ciudad.isNotEmpty()) FichaItem("Ciudad", c.ciudad)
                    FichaItem("Kilómetros", "${c.kilometros} km")
                    FichaItem("Combustible", c.combustible)
                    FichaItem("Cambio", if (c.automatico) "Automático" else "Manual")
                    FichaItem("Color", c.color)
                    FichaItem("Puertas", "${c.puertas}")
                    FichaItem("Plazas", "${c.plazas}")
                    FichaItem("Cilindrada", "${c.cilindrada} cc")
                    FichaItem("Potencia", "${c.potenciaCv} CV")
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Comentarios del anunciante", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(c.descripcion)
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(Modifier.padding(16.dp)) {
                    if (vendedor?.photoUrl?.isNotBlank() == true) {
                        AsyncImage(
                            model = vendedor!!.photoUrl,
                            contentDescription = "Foto de ${vendedor!!.name}",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(30.dp))
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
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            vendedor?.name ?: "Usuario no disponible",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            if (vendedor != null) "★ ${vendedor!!.valoracion}" else "Sin valoración",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(8.dp))
                        if (vendedor != null) {
                            Button(onClick = { onViewSeller(vendedor!!.id) }) {
                                Text("Ver perfil")
                            }
                            Spacer(Modifier.height(8.dp))
                            if (!isOwnCar) {
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            val chatId = chatVM.getOrCreateChat(c.id, c.ownerId)
                                            if (!c.ownerId.isNullOrEmpty() && !chatId.isNullOrEmpty()) {
                                                onContact(chatId, c.id, c.ownerId)
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Error al iniciar el chat. Intenta más tarde.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_usuario),
                                        contentDescription = "Contactar",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("Contactar")
                                }
                            } else {
                                Text("Este es tu anuncio", style = MaterialTheme.typography.bodySmall)
                            }
                        } else {
                            Text("Contacto no disponible", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    } ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
