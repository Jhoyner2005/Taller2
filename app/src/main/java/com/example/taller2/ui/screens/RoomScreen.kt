
package com.example.taller2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taller2.Repository.GameRepository

@Composable
fun RoomScreen(
    roomCode: String,
    userId: String,
    onBack: () -> Unit,
    onStartGame: (String) -> Unit
) {
    val repository = remember { GameRepository() }
    var hasNavigated by remember(roomCode) {
        mutableStateOf(false)
    }
    var status by remember { mutableStateOf("WAITING") }
    var hostName by remember { mutableStateOf("Host") }
    var hostId by remember { mutableStateOf("") }
    var guestName by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isStarting by remember { mutableStateOf(false) }

    val isHost = userId == hostId

    // Escuchar cambios de la sala en tiempo real desde Firebase
    DisposableEffect(roomCode) {
        val listener = repository.listenToRoom(
            roomCode = roomCode,
            onUpdate = { snapshot ->
                if (snapshot.exists()) {
                    status = snapshot.child("status").getValue(String::class.java) ?: "WAITING"
                    hostId = snapshot.child("hostId").getValue(String::class.java) ?: ""

                    val playersSnapshot = snapshot.child("players")
                    for (playerSnap in playersSnapshot.children) {
                        val pName = playerSnap.child("name").getValue(String::class.java) ?: "Player"
                        val pIsHost = playerSnap.child("isHost").getValue(Boolean::class.java) ?: false

                        if (pIsHost) {
                            hostName = pName
                        } else {
                            guestName = pName
                        }
                    }

                    // Navegar automáticamente a la partida cuando el estado pase a PLAYING
                    if (status == "PLAYING" && !hasNavigated) {
                        hasNavigated = true
                        onStartGame(roomCode)
                    }
                } else {
                    errorMessage = "The room does not exist or has been deleted."
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Waiting room",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Access code:")

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = roomCode,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        //Card with player information
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Players in the room:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(text = " Host: $hostName")

                Spacer(modifier = Modifier.height(8.dp))

                if (guestName != null) {
                    Text(text = " Opponent: $guestName")
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Waiting for the opponent...", color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isHost && status == "READY") {
            Button(
                onClick = {
                    if (!isStarting) {
                        isStarting = true
                        repository.startGame(
                            roomCode = roomCode,
                            userId = userId,
                            onSuccess = {
                                isStarting = false
                            },
                            onError = { error ->
                                isStarting = false
                                errorMessage = error
                            }
                        )
                    }
                },
                enabled = !isStarting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isStarting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Start Game!")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        TextButton(onClick = onBack) {
            Text("Return to menu")
        }
    }
}
