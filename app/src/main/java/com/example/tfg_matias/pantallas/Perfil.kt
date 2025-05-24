// Código COMPLETO para Perfil.kt con media de valoración, comentarios editables, diseño visual y sección de publicaciones + cuenta

package com.example.tfg_matias.pantallas

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tfg_matias.Model.Comentario
import com.example.tfg_matias.R
import com.example.tfg_matias.ViewModel.CarViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*


fun formatFecha(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}
@Composable
fun Perfil(userId: String, onCarClick: (String) -> Unit, onCarEdit: (String) -> Unit, onLogout: () -> Unit, onUserClick: (String) -> Unit) {
    val vm: CarViewModel = viewModel()
    val user by vm.selectedProfile.collectAsState()
    val cars by vm.cars.collectAsState()
    val profileError by vm.profileError.collectAsState()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var comentario by remember { mutableStateOf("") }
    var valoracion by remember { mutableFloatStateOf(0f) }
    var editando by remember { mutableStateOf(false) }
    var showImageDialog by remember { mutableStateOf(false) }
    var nuevaFotoUri by remember { mutableStateOf<Uri?>(null) }
    var comentarioEditando by remember { mutableStateOf<Comentario?>(null) }
    var cocheAEliminarId by remember { mutableStateOf<String?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val isCurrentUser = FirebaseAuth.getInstance().currentUser?.uid == userId
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
                ?: return@rememberLauncherForActivityResult
            val db = FirebaseFirestore.getInstance()
            if (uri != null) {
                nuevaFotoUri = uri
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val storageRef =
                            FirebaseStorage.getInstance().reference.child("usuarios/$uid.jpg")
                        storageRef.putFile(uri).await()
                        val url = storageRef.downloadUrl.await().toString()
                        db.collection("users").document(uid).update("photoUrl", url).await()
                        val authUser = FirebaseAuth.getInstance().currentUser
                        authUser?.updateProfile(
                            com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                .setPhotoUri(Uri.parse(url))
                                .build()
                        )?.await()
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(context, "Foto actualizada", Toast.LENGTH_SHORT).show()
                            vm.getUserProfile(uid)
                        }
                    } catch (e: Exception) {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(context, "Error al subir la foto", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

    LaunchedEffect(userId) {
        vm.getUserProfile(userId)
        vm.loadCars()
        vm.loadAllUsers()  // aquí se cargan los nombres de los autores
    }


    user?.let { usuario ->
        var nombreEditado by remember { mutableStateOf(usuario.name) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (nuevaFotoUri != null) {
                        AsyncImage(
                            model = nuevaFotoUri,
                            contentDescription = "Foto nueva",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .clickable { showImageDialog = true }
                        )
                    } else if (usuario.photoUrl.isNotBlank()) {
                        AsyncImage(
                            model = usuario.photoUrl,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .clickable { showImageDialog = true }
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_usuario),
                            contentDescription = "Sin foto",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                        )
                    }

                    if (showImageDialog && (nuevaFotoUri != null || usuario.photoUrl.isNotBlank())) {
                        AlertDialog(
                            onDismissRequest = { showImageDialog = false },
                            confirmButton = {},
                            text = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = nuevaFotoUri ?: usuario.photoUrl,
                                        contentDescription = "Foto ampliada",
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        )
                    }

                    if (isCurrentUser) {
                        Text(
                            text = "Modificar foto",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.clickable { launcher.launch("image/*") }
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = if (usuario.name.isNotBlank()) usuario.name else usuario.email,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = usuario.email,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (isCurrentUser) {
                if (!editando) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                        Button(
                            onClick = { editando = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Editar nombre")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val uid =
                                    FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                                val db = FirebaseFirestore.getInstance()
                                val storageRef =
                                    FirebaseStorage.getInstance().reference.child("usuarios/$uid.jpg")
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        storageRef.delete().await()
                                    } catch (_: Exception) {
                                    }
                                    db.collection("users").document(uid).update("photoUrl", "")
                                        .await()
                                    CoroutineScope(Dispatchers.Main).launch {
                                        nuevaFotoUri = null
                                        Toast.makeText(
                                            context,
                                            "Foto eliminada",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        vm.getUserProfile(uid)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Eliminar foto")
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = nombreEditado,
                        onValueChange = { nombreEditado = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val uid =
                                    FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                                CoroutineScope(Dispatchers.IO).launch {
                                    FirebaseFirestore.getInstance().collection("users")
                                        .document(uid).update("name", nombreEditado).await()
                                    CoroutineScope(Dispatchers.Main).launch {
                                        editando = false
                                        Toast.makeText(
                                            context,
                                            "Nombre actualizado",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        vm.getUserProfile(uid)
                                    }
                                }
                            }, modifier = Modifier.weight(1f)
                        ) { Text("Guardar") }
                        OutlinedButton(
                            onClick = { editando = false },
                            modifier = Modifier.weight(1f)
                        ) { Text("Cancelar") }
                    }
                }
            }

            val valoracionesValidas = usuario.comentarios.map { it.valoracion }.filter { it > 0 }
            val mediaValoracion =
                valoracionesValidas.average().takeUnless { it.isNaN() }?.toFloat() ?: 0f
            Text(
                "Valoración: ★ ${"%.1f".format(mediaValoracion)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text("Comentarios:", style = MaterialTheme.typography.titleMedium)
            usuario.comentarios.forEach { com ->
                val autorUsuario = vm.allUsers.find { it.id == com.authorId }

                val autor = when {
                    autorUsuario != null && autorUsuario.name.isNotBlank() -> autorUsuario.name
                    autorUsuario != null -> autorUsuario.email
                    else -> "Cargando..."
                }

                val fotoAutor = autorUsuario?.photoUrl ?: ""
                val fecha = com.timestamp?.let { formatFecha(it) } ?: "Sin fecha"
                val currentUid = FirebaseAuth.getInstance().currentUser?.uid

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(12.dp)) {

                        // Autor + fecha
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                autorUsuario?.let {
                                    onUserClick(it.id)
                                }
                            }
                        ) {
                            if (fotoAutor.isNotBlank()) {
                                AsyncImage(
                                    model = fotoAutor,
                                    contentDescription = "Foto autor",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.ic_usuario),
                                    contentDescription = "Sin foto",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "$autor – $fecha",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

                        Spacer(Modifier.height(8.dp))
                        Text("★ ${com.valoracion}", fontWeight = FontWeight.Bold)
                        Text(com.text)

                        // Botones
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (com.authorId == currentUid) {
                                TextButton(onClick = {
                                    comentario = com.text
                                    valoracion = com.valoracion.toFloat()
                                    comentarioEditando = com
                                }) {
                                    Text("Editar")
                                }

                                TextButton(
                                    onClick = {
                                        val nuevosComentarios =
                                            usuario.comentarios.filterNot { it.id == com.id }
                                        CoroutineScope(Dispatchers.IO).launch {
                                            FirebaseFirestore.getInstance().collection("users")
                                                .document(userId)
                                                .update("comentarios", nuevosComentarios).await()
                                            CoroutineScope(Dispatchers.Main).launch {
                                                Toast.makeText(
                                                    context,
                                                    "Comentario eliminado",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                vm.getUserProfile(userId)
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red) // botón rojo
                                ) {
                                    Text("Eliminar")
                                }
                            } else if (isCurrentUser) {
                                var showDeleteConfirm by remember { mutableStateOf(false) }
                                TextButton(
                                    onClick = { showDeleteConfirm = true },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red) // botón rojo
                                ) {

                                    Text("Eliminar")
                                }
                                if (showDeleteConfirm) {
                                    AlertDialog(
                                        onDismissRequest = { showDeleteConfirm = false },
                                        title = { Text("¿Eliminar comentario?") },
                                        text = { Text("¿Estás seguro de que deseas eliminar este comentario? Se reducirá la valoración media y el comentario desaparecerá.") },
                                        confirmButton = {
                                            TextButton(onClick = {
                                                showDeleteConfirm = false
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    val nuevosComentarios =
                                                        usuario.comentarios.filterNot { it.id == com.id }
                                                    FirebaseFirestore.getInstance()
                                                        .collection("users")
                                                        .document(userId)
                                                        .update("comentarios", nuevosComentarios)
                                                        .await()
                                                    CoroutineScope(Dispatchers.Main).launch {
                                                        Toast.makeText(
                                                            context,
                                                            "Comentario eliminado",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        vm.getUserProfile(userId)
                                                    }
                                                }
                                            }) {
                                                Text(
                                                    "Eliminar",
                                                    color = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = { showDeleteConfirm = false }) {
                                                Text("Cancelar")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (!isCurrentUser) {
                HorizontalDivider()
                Text(
                    if (comentarioEditando != null) "Editar comentario:" else "Deja una valoración y comentario:",
                    style = MaterialTheme.typography.titleMedium
                )
                Slider(
                    value = valoracion,
                    onValueChange = { valoracion = it },
                    valueRange = 0f..5f,
                    steps = 4
                )
                Text("Valoración: ${valoracion.toInt()} estrellas")
                OutlinedTextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    label = { Text("Comentario") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                    val userFirebase = FirebaseAuth.getInstance().currentUser
                    val nuevoComentario = Comentario(
                        id = comentarioEditando?.id ?: UUID.randomUUID().toString(),
                        authorId = uid,
                        name = userFirebase?.displayName ?: "Anónimo",
                        email = userFirebase?.email ?: "",
                        photoUrl = userFirebase?.photoUrl?.toString() ?: "",
                        text = comentario,
                        valoracion = valoracion,
                        timestamp = Timestamp.now()
                    )


                    CoroutineScope(Dispatchers.IO).launch {
                        val nuevos =
                            usuario.comentarios.filter { it.authorId != uid || it.id != comentarioEditando?.id } + nuevoComentario
                        FirebaseFirestore.getInstance().collection("users").document(userId)
                            .update("comentarios", nuevos).await()
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(context, "Comentario guardado", Toast.LENGTH_SHORT)
                                .show()
                            comentario = ""
                            valoracion = 0f
                            comentarioEditando = null
                            vm.getUserProfile(userId)
                        }
                    }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (comentarioEditando != null) "Actualizar" else "Enviar")
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Coches publicados:", style = MaterialTheme.typography.titleMedium)
            val cochesPublicados = cars.filter { it.ownerId == usuario.id }
            if (cochesPublicados.isEmpty()) {
                Text(
                    "Este usuario no ha publicado coches aún.",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                cochesPublicados.forEach { coche ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            CocheCard(coche = coche, onClick = { onCarClick(coche.id) })

                            if (isCurrentUser) {
                                Spacer(Modifier.height(8.dp))

                                Button(
                                    onClick = { onCarEdit(coche.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Black,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Editar publicación")
                                }

                                Spacer(Modifier.height(8.dp))

                                Button(
                                    onClick = { cocheAEliminarId = coche.id },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Eliminar publicación", color = MaterialTheme.colorScheme.onError)
                                }
                            }
                        }
                    }
                }
                if (cocheAEliminarId != null) {
                    AlertDialog(
                        onDismissRequest = { cocheAEliminarId = null },
                        title = { Text("¿Eliminar publicación?") },
                        text = { Text("¿Estás seguro de que deseas eliminar esta publicación? Esta acción no se puede deshacer.") },
                        confirmButton = {
                            TextButton(onClick = {
                                val id = cocheAEliminarId
                                cocheAEliminarId = null
                                if (id != null) {
                                    val db = FirebaseFirestore.getInstance()
                                    db.collection("cars").document(id).delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Publicación eliminada",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            db.collection("chats")
                                                .whereEqualTo("cocheId", id).get()
                                                .addOnSuccessListener { snapshot ->
                                                    for (doc in snapshot.documents) {
                                                        db.collection("chats").document(doc.id).delete()
                                                    }
                                                }
                                            vm.removeCarLocally(id)
                                            vm.applyFilters(
                                                marca = "", modelo = "", precioMin = null, precioMax = null,
                                                provincia = "", ciudad = "", añoMin = null, añoMax = null,
                                                kmMin = null, kmMax = null, combustible = "", color = "",
                                                automatico = "", puertas = null, cilindrada = null
                                            )

                                            vm.loadCars()
                                        }
                                }
                            }) {
                                Text("Eliminar", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { cocheAEliminarId = null }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }

            }

            if (isCurrentUser) {
                Spacer(Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar sesión")
                }

                if (showLogoutDialog) {
                    AlertDialog(
                        onDismissRequest = { showLogoutDialog = false },
                        title = { Text("¿Cerrar sesión?") },
                        text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
                        confirmButton = {
                            TextButton(onClick = {
                                showLogoutDialog = false
                                FirebaseAuth.getInstance().signOut()
                                Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                                onLogout()
                            }) {
                                Text("Sí, cerrar sesión", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showLogoutDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar cuenta", color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    } ?: run {
        if (profileError.isNotBlank()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $profileError")
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("¿Seguro que quieres eliminar tu cuenta?") },
            text = { Text("Esta acción eliminará todos tus datos, coches, valoraciones, comentarios y chats. ¡No podrás recuperarlos!") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    val authUser = FirebaseAuth.getInstance().currentUser
                    val db = FirebaseFirestore.getInstance()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val uid = authUser?.uid ?: return@launch

                            // Eliminar coches
                            val carsSnapshot =
                                db.collection("cars").whereEqualTo("ownerId", uid).get().await()
                            for (doc in carsSnapshot.documents) {
                                db.collection("cars").document(doc.id).delete().await()
                            }

                            // Eliminar chats
                            val chatsSnapshot =
                                db.collection("chats").whereArrayContains("participants", uid).get()
                                    .await()
                            for (doc in chatsSnapshot.documents) {
                                db.collection("chats").document(doc.id).delete().await()
                            }

                            // Eliminar comentarios en otros usuarios
                            val usersSnapshot = db.collection("users").get().await()
                            for (userDoc in usersSnapshot.documents) {
                                val comentarios =
                                    userDoc["comentarios"] as? List<Map<String, Any>> ?: continue
                                val nuevosComentarios =
                                    comentarios.filterNot { it["authorId"] == uid }
                                db.collection("users").document(userDoc.id)
                                    .update("comentarios", nuevosComentarios).await()
                            }

                            // Eliminar usuario
                            db.collection("users").document(uid).delete().await()

                            // Eliminar autenticación
                            authUser.delete().addOnCompleteListener { task ->
                                CoroutineScope(Dispatchers.Main).launch {
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "Cuenta eliminada",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        onLogout() // Redirige al login
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Error al eliminar cuenta",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(
                                    context,
                                    "Error: ${e.localizedMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }) {
                    Text("Sí, eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}