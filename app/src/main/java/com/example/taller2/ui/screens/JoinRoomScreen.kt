
package com.example.taller2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taller2.Repository.GameRepository

@Composable
fun JoinRoomScreen(
    userId: String,
    userName: String,
    onBack: () -> Unit,
    onRoomJoined: (String) -> Unit
) {
    val repository = remember { GameRepository() }

    var roomCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Unirse a Partida",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Ingresa el código de la sala.")

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = roomCode,
            onValueChange = { value ->
                roomCode = value.filter { it.isDigit() }.take(6)
                errorMessage = null
            },
            label = { Text("Código de la partida") },
            placeholder = { Text("Ejemplo: 123456") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (roomCode.length != 6) {
                    errorMessage = "El código debe tener 6 números."
                } else {
                    isLoading = true
                    errorMessage = null

                    repository.joinRoom(
                        roomCode = roomCode,
                        userId = userId,
                        userName = userName,
                        onSuccess = {
                            isLoading = false
                            onRoomJoined(roomCode)
                        },
                        onError = { error ->
                            isLoading = false
                            errorMessage = error
                        }
                    )
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Unirse a la sala")
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onBack) {
            Text("Volver al menú")
        }
    }
}
