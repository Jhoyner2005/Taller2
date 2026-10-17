package com.example.taller2.Model

/**
 * Representa a un jugador participante en la partida de Dominó Caos.
 */
data class Jugador(
    val id: String = "",
    val nombre: String = "",
    val fichas: List<Ficha> = emptyList(),
    val puntaje: Int = 0,
    val usosPoderes: Map<String, Int> = mapOf(
        Poder.COMODIN.name to 1,
        Poder.SALTO.name to 1,
        Poder.INTERCAMBIO.name to 1,
        Poder.INVERSION.name to 1,
    ),
    val poderesUsados: Int = 0,
    val esHost: Boolean = false,
) {
    /**
     * Suma total de los puntos de las fichas que le quedan en la mano.
     */
    val puntosEnMano: Int
        get() {
            var suma = 0
            for (ficha in fichas) {
                suma += ficha.puntos
            }
            return suma
        }

    /**
     * Verifica si el jugador aún tiene disponibilidad de usar un poder.
     */
    fun tienePoderDisponible(poder: Poder): Boolean {
        val cantidad = usosPoderes[poder.name]
        if (cantidad != null) {
            return cantidad > 0
        } else {
            return false
        }
    }
}