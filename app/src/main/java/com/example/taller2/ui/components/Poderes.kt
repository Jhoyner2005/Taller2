package com.example.taller2.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taller2.Model.Jugador
import com.example.taller2.Model.Poder

// Reusable component to display and select special powers during the game
@Composable
fun PowersBarComponent(
    player: Jugador,
    selectedPower: Poder?,
    onPowerSelected: (Poder) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Poder.entries.forEach { power ->
            val remainingUses = player.usosPoderes[power.name] ?: 0
            val isAvailable = remainingUses > 0
            val isSelected = selectedPower == power

            Button(
                onClick = { onPowerSelected(power) },
                enabled = isAvailable,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(42.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = power.nombre,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = "($remainingUses)",
                        fontSize = 10.sp,
                    )
                }
            }
        }
    }
}

// Preview function to display powers bar in Android Studio Design tab
@Preview(showBackground = true)
@Composable
fun PowersBarPreview() {
    val samplePlayer = Jugador(
        nombre = "Player 1",
        usosPoderes = mapOf(
            Poder.COMODIN.name to 1,
            Poder.SALTO.name to 0,
            Poder.INTERCAMBIO.name to 1,
            Poder.INVERSION.name to 1,
        ),
    )
    PowersBarComponent(
        player = samplePlayer,
        selectedPower = Poder.COMODIN,
        onPowerSelected = {},
    )
}