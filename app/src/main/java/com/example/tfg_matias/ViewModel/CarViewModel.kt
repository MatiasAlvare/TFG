package com.example.tfg_matias.ViewModel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfg_matias.Model.Coche
import com.example.tfg_matias.Model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class CarViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _cars = MutableStateFlow<List<Coche>>(emptyList())
    val cars: StateFlow<List<Coche>> = _cars

    private val _filteredCars = MutableStateFlow<List<Coche>>(emptyList())
    val filteredCars: StateFlow<List<Coche>> = _filteredCars

    // Perfil
    private val _selectedProfile = MutableStateFlow<Usuario?>(null)
    val selectedProfile: StateFlow<Usuario?> = _selectedProfile

    private val _profileError = MutableStateFlow("")
    val profileError: StateFlow<String> = _profileError

    private val _allUsers = mutableStateListOf<Usuario>()
    val allUsers: List<Usuario> get() = _allUsers


    // Cargar coches
    fun loadCars() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("cars").get().await()
                val coches = snapshot.documents.mapNotNull { it.toObject(Coche::class.java) }
                _cars.value = coches
                _filteredCars.value = coches
            } catch (e: Exception) {
                println("❌ Error cargando coches: ${e.localizedMessage}")
            }
        }
    }

    // Añadir coche con imagen
    fun addCarWithImage(car: Coche, localUris: List<Uri>) {
        viewModelScope.launch {
            try {
                val urls = localUris.map { uploadImage(it) }
                val principal = urls.firstOrNull().orEmpty()

                val newId = db.collection("cars").document().id
                val cocheConUrl = car.copy(
                    id = newId,
                    imageUrl = principal,
                    fotos = urls
                )

                db.collection("cars")
                    .document(newId)
                    .set(cocheConUrl)
                    .await()

                loadCars()

            } catch (e: Exception) {
                println("❌ Error al subir coche: ${e.localizedMessage}")
            }
        }
    }

    // ✅ Nueva función applyFilters estilo coches.net con todos los campos
    fun applyFilters(
        marca: String?,
        modelo: String?,
        precioMin: Double?,
        precioMax: Double?,
        provincia: String?,
        ciudad: String?,
        añoMin: Int?,
        añoMax: Int?,
        kmMin: Int?,
        kmMax: Int?,
        combustible: String?,
        color: String?,
        automatico: String?,
        puertas: Int?,
        cilindrada: Int?
    ) {
        _filteredCars.value = _cars.value.filter { coche ->
            val cumpleMarca = marca.isNullOrBlank() || coche.marca.contains(marca, ignoreCase = true)
            val cumpleModelo = modelo.isNullOrBlank() || coche.modelo.contains(modelo, ignoreCase = true)
            val cumplePrecioMin = precioMin == null || coche.precio >= precioMin
            val cumplePrecioMax = precioMax == null || coche.precio <= precioMax
            val cumpleProvincia = provincia.isNullOrBlank() || coche.provincia.contains(provincia, ignoreCase = true)
            val cumpleCiudad = ciudad.isNullOrBlank() || coche.ciudad.contains(ciudad, ignoreCase = true)
            val cumpleAñoMin = añoMin == null || coche.año.toIntOrNull()?.let { it >= añoMin } ?: true
            val cumpleAñoMax = añoMax == null || coche.año.toIntOrNull()?.let { it <= añoMax } ?: true
            val cumpleKmMin = kmMin == null || coche.kilometros >= kmMin
            val cumpleKmMax = kmMax == null || coche.kilometros <= kmMax
            val cumpleCombustible = combustible.isNullOrBlank() || coche.combustible.contains(combustible, ignoreCase = true)
            val cumpleColor = color.isNullOrBlank() || coche.color.contains(color, ignoreCase = true)
            val cumpleCambio = automatico.isNullOrBlank() || (automatico.equals("Automático", true) && coche.automatico) || (automatico.equals("Manual", true) && !coche.automatico)
            val cumplePuertas = puertas == null || coche.puertas == puertas
            val cumpleCilindrada = cilindrada == null || coche.cilindrada == cilindrada

            cumpleMarca && cumpleModelo && cumplePrecioMin && cumplePrecioMax &&
                    cumpleProvincia && cumpleCiudad && cumpleAñoMin && cumpleAñoMax &&
                    cumpleKmMin && cumpleKmMax && cumpleCombustible && cumpleColor &&
                    cumpleCambio && cumplePuertas && cumpleCilindrada
        }
    }

    suspend fun getCarById(carId: String): Coche? {
        return try {
            val snapshot = db.collection("cars").document(carId).get().await()
            snapshot.toObject(Coche::class.java)?.copy(id = carId)
        } catch (e: Exception) {
            println("❌ Error al obtener coche: ${e.localizedMessage}")
            null
        }
    }

    suspend fun getUserById(userId: String): Usuario? {
        return try {
            val snapshot = db.collection("users").document(userId).get().await()
            snapshot.toObject(Usuario::class.java)?.copy(id = userId)
        } catch (e: Exception) {
            println("❌ Error al obtener usuario: ${e.localizedMessage}")
            null
        }
    }

    // Subir imagen
    private suspend fun uploadImage(uri: Uri): String {
        val ref = storage.reference
            .child("cars/${System.currentTimeMillis()}.jpg")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

    fun etiquetaVisual(valor: String): String {
        return when {
            valor.contains("CERO", ignoreCase = true) -> "🟦 CERO"
            valor.contains("ECO", ignoreCase = true) -> "🟢 ECO"
            valor.contains("C (verde)", ignoreCase = true) -> "🟢 C"
            valor.contains("B", ignoreCase = true) -> "🟡 B"
            valor.contains("Sin", ignoreCase = true) -> "🚫 Sin etiqueta"
            else -> valor
        }
    }


    // Obtener perfil de usuario
    fun getUserProfile(userId: String) {
        val db = FirebaseFirestore.getInstance()
        viewModelScope.launch {
            try {
                val doc = db.collection("users").document(userId).get().await()

                // 🔍 Migración de comentarios tipo String a Comentario
                val rawComentarios = doc.get("comentarios")
                if (rawComentarios is List<*> && rawComentarios.any { it is String }) {
                    val nuevosComentarios = rawComentarios.mapNotNull {
                        if (it is String) {
                            com.example.tfg_matias.Model.Comentario(
                                id = UUID.randomUUID().toString(),
                                authorId = "desconocido",
                                text = it,
                                valoracion = 0f,
                                timestamp = com.google.firebase.Timestamp.now()
                            )
                        } else it as? com.example.tfg_matias.Model.Comentario
                    }

                    // Actualiza Firestore con los nuevos objetos Comentario
                    db.collection("users").document(userId)
                        .update("comentarios", nuevosComentarios)
                        .await()
                }

                val usuario = doc.toObject(com.example.tfg_matias.Model.Usuario::class.java)
                _selectedProfile.value = usuario
                _profileError.value = ""
            } catch (e: Exception) {
                _selectedProfile.value = null
                _profileError.value = "Error al cargar perfil: ${e.localizedMessage}"
            }
        }
    }

    fun getUserNameById(userId: String): String {
        val user = allUsers.find { it.id == userId }
        Log.d("AutorComentario", "Buscando $userId -> ${user?.name ?: "NO ENCONTRADO"}")
        return user?.name ?: "Anónimo"
    }

    fun loadAllUsers() {
        val db = FirebaseFirestore.getInstance()
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users").get().await()
                val users = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Usuario::class.java)?.copy(id = doc.id)
                }
                _allUsers.clear()
                _allUsers.addAll(users)
            } catch (e: Exception) {
                Log.e("loadAllUsers", "Error cargando usuarios: ${e.localizedMessage}")
            }
        }
    }



    fun removeCarLocally(carId: String) {
        _cars.value = _cars.value.filterNot { it.id == carId }
        _filteredCars.value = _filteredCars.value.filterNot { it.id == carId }
    }
}