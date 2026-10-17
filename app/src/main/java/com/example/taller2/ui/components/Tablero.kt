package com.example.taller2.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taller2.Model.Ficha

// Reusable component to render the game board and tile chain in Jetpack Compose
@Composable
fun BoardComponent(
    boardTiles: List<Ficha>,
    hasSelectedTile: Boolean,
    onPlaceTileLeft: () -> Unit,
    onPlaceTileRight: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E3A2B), // Dark green felt color
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (boardTiles.isEmpty()) {
                Text(
                    text = "Board is empty. Play the first tile!",
                    color = Color.White,
                    fontSize = 14.sp,
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    // Left placement button
                    if (hasSelectedTile) {
                        Button(
                            onClick = { onPlaceTileLeft() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.padding(end = 6.dp),
                        ) {
                            Text("← Left", fontSize = 11.sp)
                        }
                    }

                    // Render played tiles sequence
                    boardTiles.forEachIndexed { index, tile ->
                        DominoTileComponent(tile = tile)
                        if (index < boardTiles.size - 1) {
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }

                    // Right placement button
                    if (hasSelectedTile) {
                        Button(
                            onClick = { onPlaceTileRight() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.padding(start = 6.dp),
                        ) {
                            Text("Right →", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// Preview function to display board component in Android Studio Design tab
@Preview(showBackground = true)
@Composable
fun BoardPreview() {
    val sampleBoard = listOf(
        Ficha(6, 6),
        Ficha(6, 4),
        Ficha(4, 2),
    )
    BoardComponent(
        boardTiles = sampleBoard,
        hasSelectedTile = true,
        onPlaceTileLeft = {},
        onPlaceTileRight = {},
    )
}