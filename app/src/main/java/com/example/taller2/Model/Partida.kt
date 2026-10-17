package com.example.taller2.Model

/**
 * Representa el estado global de una partida de Dominó Caos para sincronizar en Firebase.
 */
data class Partida(
    val idSala: String = "",
    val codigoAcceso: String = "",
    val jugadores: List<Jugador> = emptyList(),
    val pozo: List<Ficha> = emptyList(),
    val fichasTablero: List<Ficha> = emptyList(),
    val turnoActualIndex: Int = 0,
    val sentidoInvertido: Boolean = false,
    val eventoActivo: String = Evento.NINGUNO.name,
    val poderPendiente: String = "",
    val estado: String = "EN_ESPERA",
    val ganadorId: String = "",
    val rondaActual: Int = 1,
    val maxRondas: Int = 10,
) {
    /**
     * Obtiene el jugador al que le toca jugar en el turno actual.
     */
    fun obtenerJugadorActual(): Jugador? {
        if (jugadores.isNotEmpty() && (turnoActualIndex >= 0) && (turnoActualIndex < jugadores.size)) {
            return jugadores[turnoActualIndex]
        } else {
            return null
        }
    }
}