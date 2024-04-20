package com.example.axelero.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

@Composable
fun OrientationVisualizer(
    xAngle: Float,
    yAngle: Float,
    zAngle: Float,
    modifier: Modifier = Modifier
) {
    val visualizerSize = 100.dp
    val squareSize = 500f
    val maxTilt = 10f // Maximum tilt angle in degrees

    Canvas(
        modifier = modifier
            .size(visualizerSize)
            .graphicsLayer {
                rotationX = -yAngle * maxTilt * 1.5f // Reverse the rotation direction
                rotationY = -xAngle * maxTilt * 1.5f // Reverse the rotation direction
                rotationZ = xAngle * maxTilt // Rotate around Z-axis based on xAngle
                shadowElevation = 10f
                ambientShadowColor = Color.White
                spotShadowColor = Color.White
            }
    ) {
        val squareColor = if (xAngle.toInt() == 0 && yAngle.toInt() == 0)
            Color.Green
        else
            Color.Red

        drawRect(
            color = squareColor,
            topLeft = Offset(
                (center.x - squareSize / 2) - (xAngle * 2),
                (center.y - squareSize / 2) - (yAngle * -2)
            ),
            size = Size(squareSize, squareSize)
        )

        drawContext.canvas.nativeCanvas.apply {
            drawText(
                "X: ${xAngle.toInt()} | Y: ${yAngle.toInt()} | Z: ${zAngle.toInt()}",
                center.x,
                center.y + squareSize / 2 + 50f,
                Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 32f
                    textAlign = Paint.Align.CENTER
                }
            )
        }
    }
}