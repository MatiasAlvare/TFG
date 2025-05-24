package com.example.tfg_matias.Model

import com.google.firebase.Timestamp

/**
 * Modelo de datos para representar un comentario entre usuarios dentro de la app.
 * Este comentario puede incluir una valoración en estrellas, texto y se guarda en Firestore.
 */
data class Comentario(
    val id: String = "",           // ID único del comentario (usado para editar o eliminar)
    val authorId: String = "",     // UID del autor del comentario (usuario que lo escribió)
    val name: String = "",         // Nombre visible del autor (se guarda para no tener que consultarlo cada vez)
    val email: String = "",        // Email del autor (opcional, útil para administración)
    val photoUrl: String = "",     // URL de la foto de perfil del autor al momento de hacer el comentario
    val text: String = "",         // Texto libre del comentario
    val valoracion: Float = 0f,    // Valoración en estrellas (de 0 a 5)
    val timestamp: Timestamp? = null // Fecha y hora en la que se hizo el comentario (Firebase)
)
