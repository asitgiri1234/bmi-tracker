package com.asitkg.bmitracker.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

/** One plotted measurement. [label] is the x-axis caption, e.g. "Mon". */
data class WeightChartPoint(
    val label: String,
    val value: Double,
    val isLatest: Boolean = false,
)

/**
 * Seven-day weight line chart.
 *
 * Drawn directly on a Canvas rather than via a charting library: the shapes
 * needed here are simple, and owning the drawing keeps the styling consistent
 * with [BmiGauge] and avoids a dependency for one screen.
 */
@Composable
fun WeightChart(
    points: List<WeightChartPoint>,
    unitLabel: String,
    modifier: Modifier = Modifier,
) {
    when {
        points.isEmpty() -> ChartMessage(
            title = "No weight history yet",
            body = "Record your weight to start building a chart.",
            modifier = modifier,
        )

        // A single point has no line to draw and no meaningful range, so it is
        // stated rather than rendered as a degenerate chart.
        points.size == 1 -> ChartMessage(
            title = "${formatValue(points.first().value)} $unitLabel",
            body = "One measurement so far. Add another to see your trend.",
            modifier = modifier,
        )

        else -> LineChart(points = points, unitLabel = unitLabel, modifier = modifier)
    }
}

@Composable
private fun LineChart(
    points: List<WeightChartPoint>,
    unitLabel: String,
    modifier: Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val lineColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val dotColor = MaterialTheme.colorScheme.primary
    val dotCenter = MaterialTheme.colorScheme.surface
    val fillBrush = Brush.verticalGradient(
        listOf(lineColor.copy(alpha = 0.28f), lineColor.copy(alpha = 0f)),
    )
    val labelStyle = TextStyle(fontSize = 11.sp, color = labelColor)

    val values = points.map { it.value }
    // Pad the range so the line never touches the top or bottom edge; a flat
    // series would otherwise collapse onto a single row of pixels.
    val rawMin = values.min()
    val rawMax = values.max()
    val padding = ((rawMax - rawMin) * 0.25).takeIf { it > 0.05 } ?: 1.0
    val minValue = rawMin - padding
    val maxValue = rawMax + padding
    val span = (maxValue - minValue).takeIf { it > 0.0 } ?: 1.0

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
        ) {
            val leftGutter = 44.dp.toPx()
            val bottomGutter = 22.dp.toPx()
            val topPad = 10.dp.toPx()
            val plotWidth = size.width - leftGutter
            val plotHeight = size.height - bottomGutter - topPad

            fun xFor(index: Int): Float =
                leftGutter + plotWidth * index / (points.size - 1).toFloat()

            fun yFor(value: Double): Float =
                topPad + (plotHeight * (1.0 - (value - minValue) / span)).toFloat()

            // Horizontal gridlines with value labels.
            val gridLines = 4
            repeat(gridLines) { i ->
                val value = minValue + span * i / (gridLines - 1).toFloat()
                val y = yFor(value)
                drawLine(
                    color = gridColor,
                    start = Offset(leftGutter, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(6.dp.toPx(), 6.dp.toPx()),
                    ),
                )
                drawAxisLabel(
                    textMeasurer = textMeasurer,
                    text = formatValue(value),
                    style = labelStyle,
                    x = 0f,
                    centerY = y,
                    maxWidth = leftGutter - 6.dp.toPx(),
                )
            }

            // Filled area under the line, closed along the baseline.
            val areaPath = Path().apply {
                moveTo(xFor(0), yFor(points.first().value))
                points.forEachIndexed { index, point -> lineTo(xFor(index), yFor(point.value)) }
                lineTo(xFor(points.lastIndex), topPad + plotHeight)
                lineTo(xFor(0), topPad + plotHeight)
                close()
            }
            drawPath(areaPath, brush = fillBrush)

            val linePath = Path().apply {
                moveTo(xFor(0), yFor(points.first().value))
                points.forEachIndexed { index, point ->
                    if (index > 0) lineTo(xFor(index), yFor(point.value))
                }
            }
            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(width = 2.5.dp.toPx()),
            )

            points.forEachIndexed { index, point ->
                val centre = Offset(xFor(index), yFor(point.value))
                val radius = if (point.isLatest) 6.dp.toPx() else 4.dp.toPx()
                drawCircle(color = dotColor, radius = radius, center = centre)
                // Hollow centre so overlapping dots stay distinguishable.
                drawCircle(color = dotCenter, radius = radius / 2.2f, center = centre)

                drawAxisLabel(
                    textMeasurer = textMeasurer,
                    text = point.label,
                    style = labelStyle,
                    x = centre.x - 16.dp.toPx(),
                    centerY = size.height - bottomGutter / 2,
                    maxWidth = 32.dp.toPx(),
                    centreHorizontally = true,
                )
            }
        }

        Text(
            text = "Weight ($unitLabel), last 7 days",
            style = MaterialTheme.typography.labelMedium,
            color = labelColor,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAxisLabel(
    textMeasurer: TextMeasurer,
    text: String,
    style: TextStyle,
    x: Float,
    centerY: Float,
    maxWidth: Float,
    centreHorizontally: Boolean = false,
) {
    val measured = textMeasurer.measure(text, style)
    val drawX = if (centreHorizontally) x + (maxWidth - measured.size.width) / 2f else x
    drawText(
        textLayoutResult = measured,
        topLeft = Offset(drawX, centerY - measured.size.height / 2f),
    )
}

@Composable
private fun ChartMessage(title: String, body: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatValue(value: Double): String =
    if (abs(value % 1.0) < 0.05) value.toInt().toString() else String.format("%.1f", value)
