package com.example.taller2.Model

/**
 * Representa los eventos aleatorios que pueden ocurrir durante determinados turnos en Dominó Caos.
 */
enum class Evento(
    val nombre: String,
    val descripcion: String
) {
    NINGUNO("Sin Evento", "Las reglas se mantienen normales."),
    DOBLE_PUNTAJE("Doble Puntaje", "Las fichas colocadas en este turno otorgan el doble de puntos."),
    BLOQUEAR_POZO("Pozo Bloqueado", "No se puede comer fichas del pozo en este turno."),
    TURNO_EXTRA("Turno Extra", "El jugador actual realiza un movimiento adicional."),
    ROTAR_FICHAS("Rotación de Fichas", "Se pasa una ficha aleatoria al siguiente jugador.")
}