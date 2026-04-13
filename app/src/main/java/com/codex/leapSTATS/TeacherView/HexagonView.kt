package com.codex.leapSTATS.TeacherView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HexagonView(
    text: String,
    color: Color,
    fontSize: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(150.dp)
            .height(160.dp),
        contentAlignment = Alignment.Center
    ) {
        // Hexagon shape
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(160.dp)
                .rotate(90f)
                .shadow(elevation = 3.dp, shape = HexagonShape())
                .background(color = color, shape = HexagonShape())
        )

        // Text overlay
        Text(
            text = text,
            fontSize = fontSize.sp,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .width(110.dp)
                .padding(16.dp),
            lineHeight = (fontSize * 1.2f).sp
        )
    }
}

class HexagonShape : androidx.compose.ui.graphics.Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): androidx.compose.ui.graphics.Outline {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(width * 0.50f, 0f) // Top
            lineTo(width, height * 0.25f) // Top-right
            lineTo(width, height * 0.75f) // Bottom-right
            lineTo(width * 0.50f, height) // Bottom
            lineTo(0f, height * 0.75f) // Bottom-left
            lineTo(0f, height * 0.25f) // Top-left
            close()
        }

        return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}