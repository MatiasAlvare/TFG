package com.example.tfg_matias.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfg_matias.Model.Coche
import com.example.tfg_matias.Model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
                val imageUrl = if (localUris.isNotEmpty()) {
                    uploadImage(localUris.first())
                } else {
                    ""
                }

                val newId = db.collection("cars").document().id
                val cocheConUrl = car.copy(id = newId, imageUrl = imageUrl)

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

    fun applyFilters(
        marca: String?,
        modelo: String?,
        precioMin: Double?,
        precioMax: Double?,
        soloElectricos: Boolean
    ) {
        _filteredCars.value = _cars.value.filter { coche ->
            val cumpleMarca = marca.isNullOrBlank() || coche.marca.contains(marca, ignoreCase = true)
            val cumpleModelo = modelo.isNullOrBlank() || coche.modelo.contains(modelo, ignoreCase = true)
            val cumplePrecioMin = precioMin == null || coche.precio >= precioMin
            val cumplePrecioMax = precioMax == null || coche.precio <= precioMax
            val cumpleElectrico = !soloElectricos || coche.combustible.equals("Eléctrico", ignoreCase = true)

            cumpleMarca && cumpleModelo && cumplePrecioMin && cumplePrecioMax && cumpleElectrico
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

        println("🔥 SUBIENDO URI: $uri")

        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

    // Obtener perfil de usuario
    fun getUserProfile(userId: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users").document(userId).get().await()
                if (snapshot.exists()) {
                    val user = snapshot.toObject(Usuario::class.java)!!.copy(id = userId)
                    _selectedProfile.value = user
                    _profileError.value = ""
                } else {
                    _profileError.value = "El perfil no existe."
                }
            } catch (e: Exception) {
                _profileError.value = "Error al cargar perfil: ${e.localizedMessage}"
            }
        }
    }
}
