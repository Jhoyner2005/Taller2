
package com.example.taller2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taller2.Repository.GameRepository
import com.google.firebase.database.DataSnapshot

@Composable
fun AvailableGamesScreen(
    userId: String,
    userName: String,
    onBack: () -> Unit,
    onJoinRoom: (String) -> Unit
) {
    val repository = remember { GameRepository() }

    var availableRooms by remember {
        mutableStateOf<List<DataSnapshot>>(emptyList())
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var isJoining by remember {
        mutableStateOf(false)
    }

    val backgroundColor = Color(0xFF11152B)
    val cardColor = Color(0xFF1D2340)
    val accentColor = Color(0xFF9B83FF)
    val secondaryText = Color(0xFFB7BCD2)

    DisposableEffect(userId) {
        val listener = repository.listenToAvailableRooms(
            userId = userId,
            onUpdate = { rooms ->
                availableRooms = rooms
                isLoading = false
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
            }
        )

        onDispose {
            repository.removeAvailableRoomsListener(listener)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp)
    ) {
        TextButton(
            onClick = onBack,
            enabled = !isJoining
        ) {
            Text(
                text = "Volver",
                color = accentColor
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Partidas disponibles",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Selecciona una sala para unirte a la partida.",
            fontSize = 14.sp,
            color = secondaryText
        )

        Spacer(modifier = Modifier.height(25.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color(0xFFFF8585),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = accentColor)
                }
            }

            availableRooms.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay partidas disponibles en este momento.",
                        color = secondaryText,
                        fontSize = 15.sp
                    )
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = availableRooms,
                        key = { it.key ?: "" }
                    ) { room ->

                        val roomCode = room.child("code")
                            .getValue(String::class.java) ?: ""

                        val hostName = room.child("players")
                            .children
                            .firstOrNull()
                            ?.child("name")
                            ?.getValue(String::class.java)
                            ?: "Jugador"

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = cardColor
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(
                                    text = "Sala de $hostName",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Código: $roomCode",
                                    color = secondaryText,
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        errorMessage = ""
                                        isJoining = true

                                        repository.joinRoom(
                                            roomCode = roomCode,
                                            userId = userId,
                                            userName = userName,
                                            onSuccess = {
                                                isJoining = false
                                                onJoinRoom(roomCode)
                                            },
                                            onError = { error ->
                                                isJoining = false
                                                errorMessage = error
                                            }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !isJoining,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = accentColor
                                    )
                                ) {
                                    if (isJoining) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text("Unirse a partida")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
