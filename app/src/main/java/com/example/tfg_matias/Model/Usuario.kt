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
    val id: String = "",              // UID único del usuario (clave en Firestore)
    val email: String = "",           // Correo electrónico del usuario
    val name: String = "",            // Nombre visible que se muestra en el perfil
    val photoUrl: String = "",        // URL de la foto de perfil del usuario (puede estar vacía)
    val valoracion: Double = 0.0,     // Valoración promedio calculada a partir de los comentarios recibidos
    val comentarios: List<Comentario> = emptyList(), // Lista de comentarios que otros usuarios han dejado sobre este usuario
    val city: String = ""             // Ciudad del usuario (puede ser utilizada para filtrar resultados o mostrar información)
)



