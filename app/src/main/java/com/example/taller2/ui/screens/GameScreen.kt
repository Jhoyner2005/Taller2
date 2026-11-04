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
import com.example.taller2.Repository.GameRepository

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

@Composable
fun GameScreen(
    roomCode: String,
    userId: String,
    onExit: () -> Unit
) {
    val repository = remember { GameRepository() }

    var players by remember { mutableStateOf<List<Jugador>>(emptyList()) }
    var boardTiles by remember { mutableStateOf<List<Ficha>>(emptyList()) }
    var currentTurnIndex by remember { mutableStateOf(0) }
    var activeEventName by remember { mutableStateOf("NINGUNO") }
    var currentRound by remember { mutableStateOf(1) }
    var winnerId by remember { mutableStateOf("") }
    var winnerName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isComodinActive by remember { mutableStateOf(false) }

    DisposableEffect(roomCode) {
        val listener = repository.listenToRoom(
            roomCode = roomCode,
            onUpdate = { snapshot ->
                if (snapshot.exists()) {
                    val playersSnap = snapshot.child("players")

                    val hostId = snapshot.child("hostId").getValue(String::class.java) ?: ""
                    val playerIdsOrdered = mutableListOf<String>()

                    for (pSnap in playersSnap.children) {
                        val pId = pSnap.child("id").getValue(String::class.java) ?: pSnap.key ?: ""
                        playerIdsOrdered.add(pId)
                    }

                    playerIdsOrdered.sortWith(Comparator { a, b ->
                        if (a == hostId) -1 else if (b == hostId) 1 else a.compareTo(b)
                    })

                    val parsedPlayersMap = mutableMapOf<String, Jugador>()
                    for (pSnap in playersSnap.children) {
                        val pId = pSnap.child("id").getValue(String::class.java) ?: pSnap.key ?: ""
                        val name = pSnap.child("name").getValue(String::class.java) ?: "Jugador"
                        val isHost = pSnap.child("isHost").getValue(Boolean::class.java) ?: false
                        val score = pSnap.child("score").getValue(Int::class.java) ?: 0
                        val powersUsed = pSnap.child("powersUsed").getValue(Int::class.java) ?: 0

                        val tiles = mutableListOf<Ficha>()
                        for (tileSnap in pSnap.child("tiles").children) {
                            val a = tileSnap.child("ladoA").getValue(Int::class.java) ?: 0
                            val b = tileSnap.child("ladoB").getValue(Int::class.java) ?: 0
                            tiles.add(Ficha(a, b))
                        }

                        val powerUses = mutableMapOf<String, Int>()
                        for (powerSnap in pSnap.child("powerUses").children) {
                            val key = powerSnap.key ?: continue
                            val count = powerSnap.getValue(Int::class.java) ?: 0
                            powerUses[key] = count
                        }

                        parsedPlayersMap[pId] = Jugador(
                            id = pId,
                            nombre = name,
                            fichas = tiles,
                            puntaje = score,
                            usosPoderes = if (powerUses.isNotEmpty()) powerUses else Jugador().usosPoderes,
                            poderesUsados = powersUsed,
                            esHost = isHost
                        )
                    }

                    val myPlayerObj = parsedPlayersMap[userId] ?: Jugador(id = userId, nombre = "Yo")
                    val opponentObj = parsedPlayersMap.values.firstOrNull { it.id != userId } ?: Jugador(nombre = "Oponente")

                    players = listOf(myPlayerObj, opponentObj)

                    val turnIndexFromFb = snapshot.child("currentTurnIndex").getValue(Int::class.java) ?: 0
                    val activePlayerId = if (turnIndexFromFb in playerIdsOrdered.indices) {
                        playerIdsOrdered[turnIndexFromFb]
                    } else {
                        ""
                    }

                    currentTurnIndex = if (activePlayerId == userId) 0 else 1

                    val boardList = mutableListOf<Ficha>()
                    for (tileSnap in snapshot.child("boardTiles").children) {
                        val a = tileSnap.child("ladoA").getValue(Int::class.java) ?: 0
                        val b = tileSnap.child("ladoB").getValue(Int::class.java) ?: 0
                        boardList.add(Ficha(a, b))
                    }
                    boardTiles = boardList

                    activeEventName = snapshot.child("activeEvent").getValue(String::class.java) ?: "NINGUNO"
                    currentRound = snapshot.child("currentRound").getValue(Int::class.java) ?: 1

                    winnerId = snapshot.child("winnerId").getValue(String::class.java) ?: ""
                    if (winnerId.isNotEmpty()) {
                        winnerName = parsedPlayersMap[winnerId]?.nombre ?: "Jugador"
                    }
                }
            },
            onError = { error ->
                errorMessage = error
            }
        )

        onDispose {
            repository.removeRoomListener(roomCode, listener)
        }
    }

    if (winnerId.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("¡Partida Finalizada!") },
            text = { Text("¡El ganador es $winnerName!") },
            confirmButton = {
                Button(onClick = onExit) {
                    Text("Volver al menú")
                }
            }
        )
    }

    GameScreen(
        roomCode = roomCode,
        players = players,
        boardTiles = boardTiles,
        currentTurnIndex = currentTurnIndex,
        activeEventName = activeEventName,
        currentRound = currentRound,
        onPlayTile = { tile, porIzquierda ->
            repository.playTile(
                roomCode = roomCode,
                userId = userId,
                tile = tile,
                playOnLeft = porIzquierda,
                isComodin = isComodinActive,
                onSuccess = {
                    isComodinActive = false
                },
                onError = { err ->
                    errorMessage = err
                }
            )
        },
        onPassTurn = {
            repository.passTurn(
                roomCode = roomCode,
                userId = userId,
                onSuccess = { },
                onError = { err ->
                    errorMessage = err
                }
            )
        },
        onPowerSelected = { power ->
            if (power == Poder.COMODIN) {
                isComodinActive = true
            }
            repository.usePower(
                roomCode = roomCode,
                userId = userId,
                power = power,
                onSuccess = { },
                onError = { err ->
                    errorMessage = err
                }
            )
        },
        onExit = onExit
    )
}

