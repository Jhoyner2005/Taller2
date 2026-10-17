package com.example.taller2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taller2.Model.Ficha

// Reusable component to render a domino tile in Jetpack Compose
@Composable
fun DominoTileComponent(
    tile: Ficha,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    // Determine border color and width if tile is selected
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
    val borderWidth = if (isSelected) 3.dp else 1.dp

    Row(
        modifier = modifier
            .width(70.dp)
            .height(40.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .border(borderWidth, borderColor, shape = RoundedCornerShape(8.dp))
            .clickable(enabled = onClick != null) {
                onClick?.invoke()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        // Tile side A
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = tile.ladoA.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
        }

        // Center divider line
        Box(
            modifier = Modifier
                .width(1.5.dp)
                .fillMaxHeight(0.7f)
                .background(Color.Black),
        )

        // Tile side B
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = tile.ladoB.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
        }
    }
}

// Preview function to display domino tiles in Android Studio Design tab
@Preview(showBackground = true)
@Composable
fun DominoTilePreview() {
    Surface(modifier = Modifier.padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DominoTileComponent(tile = Ficha(6, 6), isSelected = false)
            DominoTileComponent(tile = Ficha(0, 4), isSelected = true)
        }
    }
}