package com.example.taller2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taller2.Model.Jugador

// Reusable component to display player scores, turn indicator, and active events
@Composable
fun ScoreTableComponent(
    players: List<Jugador>,
    currentTurnIndex: Int,
    activeEventName: String,
    currentRound: Int,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
        ) {
            // Header with round number and active event
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Round $currentRound",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Event: $activeEventName",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Player scores list
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                players.forEachIndexed { index, player ->
                    val isCurrentTurn = index == currentTurnIndex
                    val backgroundColor = if (isCurrentTurn) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        Color.Transparent
                    }

                    Box(
                        modifier = Modifier
                            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
                            .border(
                                width = if (isCurrentTurn) 1.5.dp else 0.dp,
                                color = if (isCurrentTurn) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(8.dp),
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isCurrentTurn) "👉 ${player.nombre}" else player.nombre,
                                fontSize = 14.sp,
                                fontWeight = if (isCurrentTurn) FontWeight.Bold else FontWeight.Normal,
                            )
                            Text(
                                text = "Score: ${player.puntaje} pts",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "Tiles: ${player.fichas.size}",
                                fontSize = 11.sp,
                                color = Color.Gray,
                            )
                        }
                    }
                }
            }
        }
    }
}

// Preview function to display score table in Android Studio Design tab
@Preview(showBackground = true)
@Composable
fun ScoreTablePreview() {
    val samplePlayers = listOf(
        Jugador(nombre = "Juan", puntaje = 25),
        Jugador(nombre = "Carlos", puntaje = 10),
    )
    ScoreTableComponent(
        players = samplePlayers,
        currentTurnIndex = 0,
        activeEventName = "Double Points",
        currentRound = 1,
    )
}