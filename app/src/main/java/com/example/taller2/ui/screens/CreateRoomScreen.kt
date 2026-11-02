
package com.example.taller2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taller2.Repository.GameRepository
import kotlinx.coroutines.delay

@Composable
fun CreateRoomScreen(
    userId: String,
    userName: String,
    onBack: () -> Unit,
    onRoomCreated: (String) -> Unit
) {
    val repository = remember { GameRepository() }

    var roomCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Evitar que la pantalla se quede cargando indefinidamente.
    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(20000)

            if (isLoading) {
                isLoading = false
                errorMessage = "Firebase no respondió. Revisa tu conexión y la configuración de la base de datos."
            }
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
            text = "Nueva Partida",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Crea una sala para jugar con otro participante.")

        Spacer(modifier = Modifier.height(32.dp))

        if (roomCode.isBlank()) {
            Button(
                onClick = {
                    if (!isLoading) {
                        isLoading = true
                        errorMessage = null

                        repository.createRoom(
                            userId = userId,
                            userName = userName,
                            onSuccess = { code ->
                                roomCode = code
                                isLoading = false
                                errorMessage = null
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
                    Text("Crear sala")
                }
            }
        } else {
            Text(
                text = "¡Sala creada!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Comparte este código con tu compañero:")

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = roomCode,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Esperando al segundo jugador...")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onRoomCreated(roomCode) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver sala")
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    errorMessage = null
                }
            ) {
                Text("Cerrar mensaje")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = onBack,
            enabled = !isLoading
        ) {
            Text("Volver al menú")
        }
    }
}
