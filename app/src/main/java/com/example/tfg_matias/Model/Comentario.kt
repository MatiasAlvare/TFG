package com.example.tfg_matias.Model

import com.google.firebase.Timestamp

/**
 * Comentario dejado por un usuario sobre otro.
 *
 * @param id          ID único del comentario
 * @param authorId    UID del autor del comentario
 * @param text        Texto del comentario
 * @param valoracion  Valoración en estrellas (0-5)
 * @param timestamp   Fecha de creación
 */
data class Comentario(
    val id: String = "",
    val authorId: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val text: String = "",
    val valoracion: Float = 0f,
    val timestamp: Timestamp? = null
)
