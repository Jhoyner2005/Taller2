package com.example.taller2.Model

/**
 * Representa los poderes especiales disponibles para los jugadores en Dominó Caos.
 */
enum class Poder(
    val nombre: String,
    val descripcion: String,
    val limiteUsosInicial: Int = 1
) {
    COMODIN("Comodín", "Permite colocar una ficha sin cumplir uno de los extremos"),
    SALTO("Salto", "El siguiente jugador pierde su turno"),
    INTERCAMBIO("Intercambio", "Permite intercambiar una ficha propia con una del oponente"),
    INVERSION("Inversión", "Cambia el sentido de los turnos")
}