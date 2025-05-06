package com.example.tfg_matias.Model

import com.google.firebase.Timestamp

/**
 * Comentario dejado por un usuario sobre un vendedor.
 *
 * @param id         ID del documento
 * @param authorId   UID del que escribe
 * @param text       Contenido del comentario
 * @param timestamp  Fecha de creación
 */
data class Comentario(
    val id: String = "",
    val authorId: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now()
)