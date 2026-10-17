package com.example.taller2.Utils

import com.example.taller2.Model.Evento
import com.example.taller2.Model.Ficha

// Objeto singleton encargado de validar las reglas del juego de Dominó Caos
object Gamerulesvalidator {

    // Genera el mazo completo de 28 fichas de dominó (desde 0-0 hasta 6-6)
    fun generarMazoDomino(): List<Ficha> {
        val mazo = mutableListOf<Ficha>()
        for (i in 0..6) {
            for (j in i..6) {
                mazo.add(Ficha(ladoA = i, ladoB = j))
            }
        }
        return mazo
    }

    // Mezcla el mazo y reparte 7 fichas a cada jugador, el resto queda en el pozo
    fun repartirFichasIniciales(
        cantidadJugadores: Int,
    ): Pair<List<List<Ficha>>, List<Ficha>> {
        val mazoMezclado = generarMazoDomino().shuffled().toMutableList()
        val manosJugadores = mutableListOf<List<Ficha>>()

        repeat(cantidadJugadores) {
            val mano = mutableListOf<Ficha>()
            repeat(7) {
                if (mazoMezclado.isNotEmpty()) {
                    mano.add(mazoMezclado.removeAt(0))
                }
            }
            manosJugadores.add(mano)
        }

        // Lo que queda en mazoMezclado es el pozo de fichas
        return Pair(manosJugadores, mazoMezclado)
    }

    // Verifica si un movimiento es válido según las reglas o si se usa el poder Comodín
    fun esMovimientoValido(
        ficha: Ficha,
        fichasTablero: List<Ficha>,
        esComodin: Boolean = false
    ): Boolean {
        // Si el tablero está vacío, cualquier ficha es válida
        if (fichasTablero.isEmpty()) {
            return true
        }

        // Si usó el poder Comodín, se permite colocar sin cumplir extremos
        if (esComodin) {
            return true
        }

        val extremoIzquierdo = fichasTablero.first().ladoA
        val extremoDerecho = fichasTablero.last().ladoB

        // La ficha es válida si alguno de sus lados coincide con los extremos del tablero
        return (ficha.ladoA == extremoIzquierdo) || (ficha.ladoA == extremoDerecho) ||
               (ficha.ladoB == extremoIzquierdo) || (ficha.ladoB == extremoDerecho)
    }

    // Determina la ficha en la orientación correcta (normal o invertida) para encajar en el tablero
    fun obtenerFichaAjustada(
        ficha: Ficha,
        fichasTablero: List<Ficha>,
        porIzquierda: Boolean
    ): Ficha {
        if (fichasTablero.isEmpty()) {
            return ficha
        }

        if (porIzquierda) {
            val extremoIzquierdo = fichasTablero.first().ladoA
            if (ficha.ladoB == extremoIzquierdo) {
                return ficha
            } else if (ficha.ladoA == extremoIzquierdo) {
                return ficha.invertida()
            }
        } else {
            val extremoDerecho = fichasTablero.last().ladoB
            if (ficha.ladoA == extremoDerecho) {
                return ficha
            } else if (ficha.ladoB == extremoDerecho) {
                return ficha.invertida()
            }
        }

        return ficha
    }

    // Verifica si un jugador tiene al menos una ficha en su mano que pueda jugar
    fun tieneMovimientosPosibles(
        mano: List<Ficha>,
        fichasTablero: List<Ficha>,
        tieneComodin: Boolean = false
    ): Boolean {
        if (fichasTablero.isEmpty()) {
            return mano.isNotEmpty()
        }

        if (tieneComodin && mano.isNotEmpty()) {
            return true
        }

        for (ficha in mano) {
            if (esMovimientoValido(ficha, fichasTablero, esComodin = false)) {
                return true
            }
        }

        return false
    }

    // Calcula el puntaje asignado a una jugada considerando el evento activo
    fun calcularPuntajeJugada(
        ficha: Ficha,
        eventoActivo: String
    ): Int {
        var puntosBase = ficha.puntos

        // Si el evento aleatorio es Doble Puntaje, se duplican los puntos ganados
        if (eventoActivo == Evento.DOBLE_PUNTAJE.name) {
            puntosBase *= 2
        }

        return puntosBase
    }
}