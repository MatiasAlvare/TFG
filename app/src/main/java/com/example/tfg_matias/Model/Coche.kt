package com.example.tfg_matias.Model

// Clase que representa un coche dentro de la aplicación
// Se utiliza para guardar y transferir la información de un coche,
// tanto en Firestore como en las pantallas de la app.

data class Coche(
    val id: String = "",                  // Identificador único del coche (Firestore)
    val ownerId: String = "",             // UID del usuario que publicó el coche
    val tipo: String = "",                // Tipo de publicación
    val fotos: List<String> = emptyList(),// Lista de URLs con las fotos del coche
    val marca: String = "",               // Marca del coche
    val modelo: String = "",              // Modelo del coche
    val combustible: String = "",         // Tipo de combustible
    val año: String = "",                 // Año de matriculación o fabricación
    val automatico: Boolean = false,      // Indica si el cambio es automático
    val etiqueta: String = "",            // Etiqueta ambiental (CERO, ECO, etc.)
    val color: String = "",               // Color del coche
    val puertas: Int = 0,                 // Número de puertas (ej: 3, 5)
    val plazas: Int = 0,                  // Número de plazas disponibles
    val cilindrada: Int = 0,              // Cilindrada en centímetros cúbicos (cc)
    val potencia: Int = 0,                // Potencia en caballos de vapor (CV)
    val kilometros: Int = 0,              // Kilometraje total del coche
    val precio: Double = 0.0,             // Precio de venta del coche
    val descripcion: String = "",         // Descripción libre del anunciante
    val provincia: String = "",           // Provincia donde se encuentra el coche
    val ciudad: String = "",              // Ciudad dentro de la provincia
    val imageUrl: String = ""             // URL de la imagen principal del coche (la portada)
)
