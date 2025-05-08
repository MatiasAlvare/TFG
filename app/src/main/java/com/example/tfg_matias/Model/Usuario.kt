package com.example.tfg_matias.Model


/**
 * Perfil de usuario guardado en Firestore.
 *
 * @param id        UID del usuario
 * @param name      Nombre a mostrar
 * @param photoUrl  URL de foto de perfil
 * @param rating    Valoración promedio
 */
data class Usuario(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val photoUrl: String = "",
    val valoracion: Double = 0.0,
    val comentarios: List<String> = emptyList(),
    val city: String = ""  // 👈 AÑADIDO AQUÍ
)
