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

    // Test hasPossibleMoves when hand contains a matching tile
    @Test
    fun tieneMovimientosPosibles_withMatchingTile_returnsTrue() {
        val board = listOf(Ficha(6, 6), Ficha(6, 4))
        val hand = listOf(Ficha(1, 1), Ficha(4, 3))

        val hasMoves = Gamerulesvalidator.tieneMovimientosPosibles(
            mano = hand,
            fichasTablero = board,
            tieneComodin = false
        )

        assertTrue(hasMoves)
    }

    // Test hasPossibleMoves when hand contains no matching tiles
    @Test
    fun tieneMovimientosPosibles_withoutMatchingTile_returnsFalse() {
        val board = listOf(Ficha(6, 6), Ficha(6, 4))
        val hand = listOf(Ficha(1, 1), Ficha(2, 3))

        val hasMoves = Gamerulesvalidator.tieneMovimientosPosibles(
            mano = hand,
            fichasTablero = board,
            tieneComodin = false
        )

        assertFalse(hasMoves)
    }

    // Test game lock winner determination: lower points wins
    @Test
    fun determinarGanadorPorCierre_lowerPoints_wins() {
        val hands = mapOf(
            "p1" to listOf(Ficha(1, 1)), // 2 pts
            "p2" to listOf(Ficha(6, 6))  // 12 pts
        )

        val winner = Gamerulesvalidator.determinarGanadorPorCierre(hands)
        assertEquals("p1", winner)
    }

    // Test game lock winner determination: tie in points -> fewer tiles wins
    @Test
    fun determinarGanadorPorCierre_equalPoints_fewerTiles_wins() {
        val hands = mapOf(
            "p1" to listOf(Ficha(6, 4)),               // 10 pts, 1 ficha
            "p2" to listOf(Ficha(2, 3), Ficha(1, 4))  // 10 pts, 2 fichas
        )

        val winner = Gamerulesvalidator.determinarGanadorPorCierre(hands)
        assertEquals("p1", winner)
    }
}