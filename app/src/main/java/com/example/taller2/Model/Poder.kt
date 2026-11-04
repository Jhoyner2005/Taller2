package com.example.taller2.Model

/**
 * Representa el poder especial disponible para los jugadores en Dominó Caos.
 */
enum class Poder(
    val nombre: String,
    val descripcion: String,
    val limiteUsosInicial: Int = 1
) {
    CAMBIAR_FICHA("Cambiar Ficha", "Cambia una ficha aleatoria de tu mano por una nueva del pozo o del oponente.", limiteUsosInicial = 1)
}