package com.saeo.cyrsaer.model

data class Plato(
    val categoria: String = "",
    val descripcion: String = "",
    val disponible: Boolean = true,
    val imagen: String = "",
    val nombre: String = "",
    val precio: Double = 0.0,
    val cantidad: Int = 0
)