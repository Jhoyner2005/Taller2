package com.example.taller2.Model

/**
 * Representa una ficha de dominó con dos extremos (ladoA y ladoB).
 */
data class Ficha(
    val ladoA: Int = 0,
    val ladoB: Int = 0
) {
    /**
     * Suma total de puntos de la ficha (útil para el cálculo de puntuación).
     */
    val puntos: Int
        get() = ladoA + ladoB

    /**
     * Indica si la ficha es doble (ej: 6-6, 5-5).
     */
    val esDoble: Boolean
        get() = ladoA == ladoB

    /**
     * Devuelve una nueva ficha con sus lados invertidos.
     */
    fun invertida(): Ficha = Ficha(ladoB, ladoA)
}