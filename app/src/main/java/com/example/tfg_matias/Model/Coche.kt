package com.example.tfg_matias.Model

data class Coche(
    val id: String = "",
    val ownerId: String = "",
    val tipo: String = "",
    val fotos: List<String> = emptyList(),
    val marca: String = "",
    val modelo: String = "",
    val carroceria: String = "",
    val combustible: String = "",
    val año: String = "",
    val automatico: Boolean = false,
    val etiqueta: String = "",
    val color: String = "",
    val puertas: Int = 0,
    val plazas: Int = 0,
    val cilindrada: Int = 0,
    val potencia: Int = 0,  // en lugar de Int? = null
    val kilometros: Int = 0,
    val precio: Double = 0.0,
    val descripcion: String = "",
    val provincia: String = "",
    val ciudad: String = "",
    val imageUrl: String = ""
)
