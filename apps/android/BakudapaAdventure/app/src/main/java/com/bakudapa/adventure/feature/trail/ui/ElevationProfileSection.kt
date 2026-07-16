package com.bakudapa.adventure.feature.trail.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bakudapa.adventure.feature.trail.domain.model.Checkpoint

@Composable
fun ElevationProfileSection(
    checkpoints: List<Checkpoint>,
    modifier: Modifier = Modifier
) {
    if (checkpoints.isEmpty()) return

    val elevations = checkpoints.map { it.elevation.toFloat() }
    val minElevation = elevations.minOrNull() ?: 0f
    val maxElevation = elevations.maxOrNull() ?: 1000f
    val range = (maxElevation - minElevation).coerceAtLeast(1f)
    val textMeasurer = rememberTextMeasurer()
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.surfaceVariant
    val outlineColor = MaterialTheme.colorScheme.outline
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val errorColor = MaterialTheme.colorScheme.error

    @OptIn(ExperimentalTextApi::class)
    @Composable
    fun rememberDrawContext() = Triple(primaryColor, outlineColor, onSurfaceColor)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Elevation Profile",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Gain: ${(maxElevation - minElevation).toInt()}m",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        ElevationChartCanvas(
            checkpoints = checkpoints,
            elevations = elevations,
            minElevation = minElevation,
            maxElevation = maxElevation,
            range = range,
            textMeasurer = textMeasurer,
            primaryColor = primaryColor,
            surfaceVariant = primaryContainer,
            outlineColor = outlineColor,
            onSurfaceColor = onSurfaceColor
        )

        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val maxCpElevation = checkpoints.maxByOrNull { it.elevation }?.elevation
            checkpoints.forEach { cp ->
                val isPeak = cp.elevation == maxCpElevation
                val chipColor = if (isPeak) errorColor else primaryColor

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = chipColor.copy(alpha = 0.12f),
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = cp.name,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${cp.elevation}m",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = chipColor
                        )
                        if (cp.eta.isNotBlank()) {
                            Text(
                                text = cp.eta,
                                style = MaterialTheme.typography.labelSmall,
                                color = outlineColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun ElevationChartCanvas(
    checkpoints: List<Checkpoint>,
    elevations: List<Float>,
    minElevation: Float,
    maxElevation: Float,
    range: Float,
    textMeasurer: TextMeasurer,
    primaryColor: Color,
    surfaceVariant: Color,
    outlineColor: Color,
    onSurfaceColor: Color
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(
                color = surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        val width = size.width
        val height = size.height
        val padding = 20.dp.toPx()
        val chartWidth = width - 2 * padding
        val chartHeight = height - 2 * padding
        val pointCount = checkpoints.size
        val gridColor = outlineColor.copy(alpha = 0.15f)

        for (i in 0..3) {
            val y = padding + i * chartHeight / 3f
            drawLine(
                color = gridColor,
                start = Offset(padding, y),
                end = Offset(width - padding, y),
                strokeWidth = 1f
            )
        }

        if (pointCount >= 2) {
            val path = Path().apply {
                moveTo(padding, height - padding)
                for (i in 0 until pointCount) {
                    val elevation = elevations[i]
                    val normalized = (elevation - minElevation) / range
                    val x = padding + (i / (pointCount - 1).toFloat()) * chartWidth
                    val y = height - padding - normalized * chartHeight
                    lineTo(x, y)
                }
                lineTo(width - padding, height - padding)
                close()
            }
            drawPath(
                path,
                color = primaryColor.copy(alpha = 0.15f),
                style = Fill
            )
        }

        if (pointCount >= 2) {
            val linePath = Path().apply {
                for (i in 0 until pointCount) {
                    val elevation = elevations[i]
                    val normalized = (elevation - minElevation) / range
                    val x = padding + (i / (pointCount - 1).toFloat()) * chartWidth
                    val y = height - padding - normalized * chartHeight
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
            }
            drawPath(
                linePath,
                color = primaryColor,
                style = Stroke(
                    width = 3f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        for (i in 0 until pointCount) {
            val elevation = elevations[i]
            val normalized = (elevation - minElevation) / range
            val x = padding + (i / (pointCount - 1).toFloat()) * chartWidth
            val y = height - padding - normalized * chartHeight

            drawCircle(
                color = primaryColor,
                radius = 6f,
                center = Offset(x, y)
            )

            val isPeak = elevation == maxElevation
            if (i == 0 || i == pointCount - 1 || isPeak) {
                val label = checkpoints[i].name
                val textLayout = textMeasurer.measure(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 10.sp,
                                color = onSurfaceColor.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(label)
                        }
                    }
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    topLeft = Offset(x - textLayout.size.width / 2, y - 20.dp.toPx() - textLayout.size.height)
                )
            }
        }

        for (i in 0..3) {
            val elev = maxElevation - (i * range / 3)
            val y = padding + i * chartHeight / 3f
            val labelText = "${elev.toInt()}m"
            drawText(
                textMeasurer = textMeasurer,
                text = labelText,
                topLeft = Offset(2.dp.toPx(), y - 6.dp.toPx())
            )
        }
    }
}
