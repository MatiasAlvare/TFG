// ✅ Código COMPLETO para Perfil.kt actualizado con confirmación de eliminación y redirección tras logout

package com.example.tfg_matias.pantallas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tfg_matias.R
import com.example.tfg_matias.ViewModel.CarViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun Perfil(
    userId: String,
    onCarClick: (String) -> Unit,
    onLogout: () -> Unit // 👈 añadido para redirigir tras logout
) {
    val vm: CarViewModel = viewModel()
    val user by vm.selectedProfile.collectAsState()
    val profileError by vm.profileError.collectAsState()
    val cars by vm.cars.collectAsState()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }

    val isCurrentUser = FirebaseAuth.getInstance().currentUser?.uid == userId

    LaunchedEffect(userId) {
        vm.getUserProfile(userId)
        vm.loadCars()
    }

    if (user != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // FOTO + NOMBRE + EMAIL
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (user!!.photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = user!!.photoUrl,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
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
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = if (user!!.name.isNotBlank()) user!!.name else user!!.email,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = user!!.email,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Valoración
            Text("Valoración: ★ ${user!!.valoracion}", style = MaterialTheme.typography.bodyMedium)

            // Comentarios
            Text("Comentarios:", style = MaterialTheme.typography.titleMedium)
            if (user!!.comentarios.isEmpty()) {
                Text("Sin comentarios aún.", style = MaterialTheme.typography.bodySmall)
            } else {
                user!!.comentarios.forEach { comentario ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = comentario,
                            Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Coches publicados
            Spacer(Modifier.height(16.dp))
            Text("Coches publicados:", style = MaterialTheme.typography.titleMedium)

            val cochesPublicados = cars.filter { it.ownerId == user!!.id }
            if (cochesPublicados.isEmpty()) {
                Text("Este usuario no ha publicado coches aún.", style = MaterialTheme.typography.bodySmall)
            } else {
                cochesPublicados.forEach { coche ->
                    CocheCard(
                        coche = coche,
                        onClick = { onCarClick(coche.id) }
                    )
                }
            }

            // 🔽 Solo si es el perfil propio
            if (isCurrentUser) {
                Spacer(Modifier.height(24.dp))
                Divider()
                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                        onLogout() // 👈 volvemos al login
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar sesión")
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        showDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar cuenta", color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    } else if (profileError.isNotBlank()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: $profileError")
        }
    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    // 🔔 Dialog de confirmación
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

                            // 1️⃣ Eliminar coches
                            val carsSnapshot = db.collection("cars").whereEqualTo("ownerId", uid).get().await()
                            for (doc in carsSnapshot.documents) {
                                db.collection("cars").document(doc.id).delete().await()
                            }

                            // 2️⃣ Eliminar chats
                            val chatsSnapshot = db.collection("chats").whereArrayContains("participants", uid).get().await()
                            for (doc in chatsSnapshot.documents) {
                                db.collection("chats").document(doc.id).delete().await()
                            }

                            // 3️⃣ Eliminar perfil
                            db.collection("users").document(uid).delete().await()

                            // 4️⃣ Eliminar cuenta Auth
                            authUser.delete().addOnCompleteListener { task ->
                                CoroutineScope(Dispatchers.Main).launch {
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Cuenta eliminada", Toast.LENGTH_LONG).show()
                                        onLogout() // 👈 volvemos al login
                                    } else {
                                        Toast.makeText(context, "Error al eliminar cuenta", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }

                        } catch (e: Exception) {
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
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