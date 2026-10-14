package com.example.taller2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Lobbyscreen(
    onNavigateToGame: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Menú Principal", fontSize = 28.sp)
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onNavigateToGame() },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Nueva Partida")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { /* Aquí irá la lógica para ingresar un código de sala */ },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Unirse a partida")
        }
    }
}