package com.example.tfg_matias.Model

import com.google.firebase.Timestamp

/**
 * Representa un coche en Firestore.
 *
 * @param id         ID del documento
 * @param ownerId    UID del usuario que lo publica
 * @param title      Título (marca y modelo)
 * @param price      Precio en texto
 * @param images     Lista de URLs de imágenes
 * @param location   Localización (ciudad)
 * @param specs      Mapa con especificaciones (km, año, combustible...)
 */
data class Coche(
    val id: String = "",
    val ownerId: String = "",

    // ——— Campos nuevos ———
    val tipo: String = "",
    val fotos: List<String> = emptyList(),
    val marca: String = "",
    val modelo: String = "",
    val carroceria: String = "",
    val combustible: String = "",
    val año: String = "",
    val version: String = "",
    val automatico: Boolean = false,
    val manual: Boolean = false,
    val etiqueta: String = "",
    val color: String = "",
    val kilometros: Int = 0,
    val precio: Double = 0.0,
    val matricula: String = "",
    val descripcion: String = ""
)