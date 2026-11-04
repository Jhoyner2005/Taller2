package com.example.taller2

import com.example.taller2.Model.Evento
import com.example.taller2.Model.Ficha
import com.example.taller2.Utils.Gamerulesvalidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Unit tests for domino game rules and logic validator
class GamerulesvalidatorTest {

    // Test that the domino deck generates exactly 28 tiles
    @Test
    fun generateDominoDeck_createsTwentyEightTiles() {
        val deck = Gamerulesvalidator.generarMazoDomino()
        assertEquals(28, deck.size)
    }

    // Test that initial dealing gives 7 tiles to each player and 14 to the well
    @Test
    fun dealInitialTiles_dealsSevenTilesPerPlayer() {
        val (hands, well) = Gamerulesvalidator.repartirFichasIniciales(cantidadJugadores = 2)
        assertEquals(2, hands.size)
        assertEquals(7, hands[0].size)
        assertEquals(7, hands[1].size)
        assertEquals(14, well.size)
    }

    // Test that valid tile placement works correctly
    @Test
    fun isValidMove_matchingTile_returnsTrue() {
        val board = listOf(Ficha(6, 6), Ficha(6, 4))
        val matchingTile = Ficha(4, 2)

        val isValid = Gamerulesvalidator.esMovimientoValido(
            ficha = matchingTile,
            fichasTablero = board,
            esComodin = false,
        )

        assertTrue(isValid)
    }

    // Test that non-matching tile is rejected when no wildcard power is used
    @Test
    fun isValidMove_nonMatchingTile_returnsFalse() {
        val board = listOf(Ficha(6, 6), Ficha(6, 4))
        val nonMatchingTile = Ficha(1, 1)

        val isValid = Gamerulesvalidator.esMovimientoValido(
            ficha = nonMatchingTile,
            fichasTablero = board,
            esComodin = false,
        )

        assertFalse(isValid)
    }

    // Test that wildcard power allows playing any tile
    @Test
    fun isValidMove_wildcardPower_returnsTrue() {
        val board = listOf(Ficha(6, 6), Ficha(6, 4))
        val anyTile = Ficha(1, 1)

        val isValid = Gamerulesvalidator.esMovimientoValido(
            ficha = anyTile,
            fichasTablero = board,
            esComodin = true,
        )

        assertTrue(isValid)
    }

    // Test that double points event doubles the move score
    @Test
    fun calculateScore_doublePointsEvent_doublesScore() {
        val tile = Ficha(6, 4) // 10 points
        val score = Gamerulesvalidator.calcularPuntajeJugada(
            ficha = tile,
            eventoActivo = Evento.DOBLE_PUNTAJE.name,
        )

        assertEquals(20, score)
    }
}