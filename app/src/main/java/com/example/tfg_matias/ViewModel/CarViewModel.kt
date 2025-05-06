package com.example.tfg_matias.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfg_matias.Model.Coche
import com.example.tfg_matias.Model.Usuario
import com.example.tfg_matias.Model.Comentario
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

open class CarViewModel : ViewModel() {
    private val db = Firebase.firestore

    // --- coches sin filtro y filtrados (igual que antes) ---
    private val _cars = MutableStateFlow<List<Coche>>(emptyList())
    val cars: StateFlow<List<Coche>> = _cars

    private val _filteredCars = MutableStateFlow<List<Coche>>(emptyList())
    val filteredCars: StateFlow<List<Coche>> = _filteredCars

    // --- perfil y comentarios seleccionados ---
    private val _selectedProfile = MutableStateFlow<Usuario?>(null)
    val selectedProfile: StateFlow<Usuario?> = _selectedProfile

    private val _selectedComments = MutableStateFlow<List<Comentario>>(emptyList())
    val selectedComments: StateFlow<List<Comentario>> = _selectedComments

    init {
        refreshAllCars()
    }

    private fun refreshAllCars() {
        viewModelScope.launch {
            val lista = db.collection("cars")
                .get().await()
                .map { doc -> doc.toObject(Coche::class.java).copy(id = doc.id) }

            _cars.value = lista
            _filteredCars.value = lista
        }
    }


    /**
     * Recupera el perfil de un usuario por su ID.
     */
    suspend fun getUserProfile(userId: String): Usuario? {
        val doc = db.collection("users")
            .document(userId)
            .get()
            .await()
        return doc.takeIf { it.exists() }
            ?.toObject(Usuario::class.java)
            ?.copy(id = doc.id)
    }
    fun applyFilters(
        marca: String,
        modelo: String,
        precioMin: Double?,
        precioMax: Double?,
        soloElectricos: Boolean
    ) {
        viewModelScope.launch {
            var query: Query = db.collection("cars")
            if (marca.isNotBlank())      query = query.whereEqualTo("marca", marca)
            if (modelo.isNotBlank())     query = query.whereEqualTo("modelo", modelo)
            if (precioMin != null)       query = query.whereGreaterThanOrEqualTo("precio", precioMin)
            if (precioMax != null)       query = query.whereLessThanOrEqualTo("precio", precioMax)
            if (soloElectricos)          query = query.whereEqualTo("combustible", "Eléctrico")

            val listaFiltrada = query
                .get().await()
                .map { doc -> doc.toObject(Coche::class.java).copy(id = doc.id) }

            _filteredCars.value = listaFiltrada
        }
    }

    fun addCar(car: Coche) {
        viewModelScope.launch {
            _cars.value = _cars.value + car
            db.collection("cars").document(car.id).set(car).await()
        }
    }

    /**
     * Carga en paralelo perfil + comentarios para un usuario dado.
     * Actualiza los StateFlow _selectedProfile y _selectedComments.
     */
    fun loadUserData(userId: String) {
        viewModelScope.launch {
            // Disparamos ambas llamadas simultáneas
            val userDeferred = async {
                db.collection("users").document(userId)
                    .get().await()
                    .toObject(Usuario::class.java)
                    ?.copy(id = userId)
            }
            val commentsDeferred = async {
                db.collection("users")
                    .document(userId)
                    .collection("comments")
                    .orderBy("timestamp")
                    .get().await()
                    .map { doc ->
                        doc.toObject(Comentario::class.java).copy(id = doc.id)
                    }
            }

            _selectedProfile.value = userDeferred.await()
            _selectedComments.value = commentsDeferred.await()
        }
    }

    // --- Métodos mínimos para detalle ---
    suspend fun getCarById(id: String): Coche? {
        val doc = db.collection("cars").document(id).get().await()
        return doc.toObject(Coche::class.java)?.copy(id = id)
    }
}
