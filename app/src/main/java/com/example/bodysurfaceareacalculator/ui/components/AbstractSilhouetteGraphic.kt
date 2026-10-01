package com.example.bodysurfaceareacalculator.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun AbstractSilhouetteGraphic(
    progress: Float,
    modifier: Modifier = Modifier,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    accentColor: Color = MaterialTheme.colorScheme.tertiary
) {
    Canvas(modifier = modifier.size(160.dp)) {
        val w = size.width
        val h = size.height

        val centerX = w * 0.45f

        // Phase 1: Silhouette / Abstract figure lines (0.0 to 0.4)
        val figureAlpha = (progress / 0.4f).coerceIn(0f, 1f)

        // Head circle
        val headRadius = w * 0.09f
        val headCenter = Offset(centerX, h * 0.18f)
        drawCircle(
            color = primaryColor.copy(alpha = 0.85f * figureAlpha),
            radius = headRadius,
            center = headCenter,
            style = Stroke(width = 3.dp.toPx())
        )

        // Shoulders & Torso abstract path
        val torsoPath = Path().apply {
            moveTo(centerX - w * 0.18f, h * 0.40f)
            quadraticTo(centerX, h * 0.32f, centerX + w * 0.18f, h * 0.40f)
            lineTo(centerX + w * 0.14f, h * 0.65f)
            quadraticTo(centerX, h * 0.68f, centerX - w * 0.14f, h * 0.65f)
            close()
        }
        drawPath(
            path = torsoPath,
            color = primaryColor.copy(alpha = 0.20f * figureAlpha)
        )
        drawPath(
            path = torsoPath,
            color = primaryColor.copy(alpha = 0.80f * figureAlpha),
            style = Stroke(width = 2.5.dp.toPx())
        )

        // Phase 2: Height measurement marker on the right (0.25 to 0.65)
        val heightProgress = ((progress - 0.25f) / 0.4f).coerceIn(0f, 1f)
        if (heightProgress > 0f) {
            val bracketX = w * 0.78f
            val topY = h * 0.12f
            val bottomY = topY + (h * 0.68f) * heightProgress

            // Vertical line
            drawLine(
                color = accentColor.copy(alpha = 0.9f),
                start = Offset(bracketX, topY),
                end = Offset(bracketX, bottomY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Top bracket cap
            drawLine(
                color = accentColor.copy(alpha = 0.9f),
                start = Offset(bracketX - 8.dp.toPx(), topY),
                end = Offset(bracketX + 4.dp.toPx(), topY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Bottom bracket cap
            if (heightProgress > 0.8f) {
                drawLine(
                    color = accentColor.copy(alpha = 0.9f),
                    start = Offset(bracketX - 8.dp.toPx(), bottomY),
                    end = Offset(bracketX + 4.dp.toPx(), bottomY),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Phase 3: Weight / width marker across chest (0.5 to 0.85)
        val weightProgress = ((progress - 0.5f) / 0.35f).coerceIn(0f, 1f)
        if (weightProgress > 0f) {
            val startX = centerX - (w * 0.16f) * weightProgress
            val endX = centerX + (w * 0.16f) * weightProgress
            val lineY = h * 0.48f

            drawLine(
                color = primaryColor.copy(alpha = 0.85f),
                start = Offset(startX, lineY),
                end = Offset(endX, lineY),
                strokeWidth = 1.8.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Arrowheads/ticks
            drawLine(
                color = primaryColor.copy(alpha = 0.85f),
                start = Offset(startX, lineY - 4.dp.toPx()),
                end = Offset(startX, lineY + 4.dp.toPx()),
                strokeWidth = 1.8.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = primaryColor.copy(alpha = 0.85f),
                start = Offset(endX, lineY - 4.dp.toPx()),
                end = Offset(endX, lineY + 4.dp.toPx()),
                strokeWidth = 1.8.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Phase 4: Surface-area result accent ring/glow (0.75 to 1.0)
        val accentProgress = ((progress - 0.75f) / 0.25f).coerceIn(0f, 1f)
        if (accentProgress > 0f) {
            val arcSize = Size(w * 0.85f, h * 0.85f)
            val arcOffset = Offset(w * 0.075f, h * 0.075f)
            drawArc(
                color = primaryColor.copy(alpha = 0.35f * accentProgress),
                startAngle = 135f,
                sweepAngle = 270f * accentProgress,
                useCenter = false,
                topLeft = arcOffset,
                size = arcSize,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
    }
}
