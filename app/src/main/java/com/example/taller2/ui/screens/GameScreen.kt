package com.example.taller2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.taller2.Model.Ficha
import com.example.taller2.Model.Jugador
import com.example.taller2.Model.Poder

import com.example.taller2.ui.components.DominoTileComponent
import com.example.taller2.ui.components.BoardComponent
import com.example.taller2.ui.components.PowersBarComponent
import com.example.taller2.ui.components.ScoreTableComponent

@Composable
fun GameScreen(
    roomCode: String,
    players: List<Jugador>,
    boardTiles: List<Ficha>,
    currentTurnIndex: Int,
    activeEventName: String,
    currentRound: Int,

    onPlayTile: (Ficha, Boolean) -> Unit,
    onPassTurn: () -> Unit,
    onPowerSelected: (Poder) -> Unit,
    onExit: () -> Unit,

    modifier: Modifier = Modifier
) {
    var selectedTile by remember {
        mutableStateOf<Ficha?>(null)
    }

    var selectedPower by remember {
        mutableStateOf<Poder?>(null)
    }

    val myPlayer = players.getOrNull(0)
    val opponent = players.getOrNull(1)

    val isMyTurn = currentTurnIndex == 0

    val backgroundColor = Color(0xFF20152B)
    val accentColor = Color(0xFFE8B85C)
    val secondaryColor = Color(0xFF9C4DCC)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ENCABEZADO
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DOMINÓ",
                    color = accentColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "CAOS",
                    color = secondaryColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SALA",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )

                Text(
                    text = roomCode,
                    color = accentColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(
                onClick = onExit
            ) {
                Text(
                    text = "Salir",
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // TABLA DE PUNTUACIÓN
        ScoreTableComponent(
            players = players,
            currentTurnIndex = currentTurnIndex,
            activeEventName = activeEventName,
            currentRound = currentRound
        )

        Spacer(modifier = Modifier.height(8.dp))

        // INFORMACIÓN DEL TURNO
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isMyTurn) {
                    Color(0xFF4A245A)
                } else {
                    Color(0xFF30283A)
                }
            )
        ) {
            Text(
                text = if (isMyTurn) {
                    "¡ES TU TURNO!"
                } else {
                    "TURNO DEL OPONENTE"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // TABLERO CENTRAL
        Text(
            text = "MESA DE JUEGO",
            color = accentColor,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        BoardComponent(
            boardTiles = boardTiles,
            hasSelectedTile = selectedTile != null,
            onPlaceTileLeft = {
                selectedTile?.let { tile ->
                    onPlayTile(tile, true)
                    selectedTile = null
                }
            },
            onPlaceTileRight = {
                selectedTile?.let { tile ->
                    onPlayTile(tile, false)
                    selectedTile = null
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // FICHAS DEL OPONENTE
        Text(
            text = opponent?.nombre ?: "Oponente",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(opponent?.fichas?.size ?: 0) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .width(20.dp)
                        .height(34.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = secondaryColor
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {}
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // FICHAS DEL JUGADOR ACTUAL
        Text(
            text = "TUS FICHAS",
            color = accentColor,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            myPlayer?.fichas?.forEach { tile ->

                val isSelected = selectedTile == tile

                DominoTileComponent(
                    tile = tile,
                    isSelected = isSelected,
                    onClick = {
                        if (isMyTurn) {
                            selectedTile = if (isSelected) {
                                null
                            } else {
                                tile
                            }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PODERES
        Text(
            text = "PODERES ESPECIALES",
            color = accentColor,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        myPlayer?.let { player ->
            PowersBarComponent(
                player = player,
                selectedPower = selectedPower,
                onPowerSelected = { power ->
                    selectedPower = power
                    onPowerSelected(power)
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // BOTÓN PASAR TURNO
        Button(
            onClick = onPassTurn,
            enabled = isMyTurn,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = secondaryColor
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "PASAR TURNO",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
