package com.asitkg.bmitracker.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.asitkg.bmitracker.domain.model.BmiCategory
import com.asitkg.bmitracker.ui.theme.color

/** Visible span of the scale. Values outside are clamped to the ends. */
private const val SCALE_MIN = 15.0
private const val SCALE_MAX = 40.0

/**
 * Horizontal BMI scale with the four WHO bands drawn to proportional width and
 * a marker at the user's value.
 *
 * A scale communicates more than a bare number: it shows how far the reading
 * sits from the neighbouring bands, so a BMI of 24.9 reads as "near the top of
 * normal" rather than simply "normal".
 */
@Composable
fun BmiGauge(
    bmi: Double,
    modifier: Modifier = Modifier,
) {
    val markerColor = MaterialTheme.colorScheme.onSurface

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
        ) {
            val barHeight = 16.dp.toPx()
            val barTop = size.height - barHeight
            val span = (SCALE_MAX - SCALE_MIN).toFloat()

            // Bands, each sized by its share of the visible scale.
            BmiCategory.entries.forEach { category ->
                val start = category.minInclusive.coerceAtLeast(SCALE_MIN)
                val end = category.maxExclusive.coerceAtMost(SCALE_MAX)
                if (end <= start) return@forEach

                val left = ((start - SCALE_MIN) / span * size.width).toFloat()
                val right = ((end - SCALE_MIN) / span * size.width).toFloat()

                // Round only the outermost corners so the bands read as one bar.
                val radius = when {
                    start <= SCALE_MIN -> CornerRadius(barHeight / 2, barHeight / 2)
                    end >= SCALE_MAX -> CornerRadius(barHeight / 2, barHeight / 2)
                    else -> CornerRadius.Zero
                }
                val path = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(left, barTop, right, barTop + barHeight),
                            topLeft = if (start <= SCALE_MIN) radius else CornerRadius.Zero,
                            bottomLeft = if (start <= SCALE_MIN) radius else CornerRadius.Zero,
                            topRight = if (end >= SCALE_MAX) radius else CornerRadius.Zero,
                            bottomRight = if (end >= SCALE_MAX) radius else CornerRadius.Zero,
                        ),
                    )
                }
                drawPath(path, color = category.color)
            }

            // Marker: a downward triangle sitting above the bar.
            val clamped = bmi.coerceIn(SCALE_MIN, SCALE_MAX)
            val x = ((clamped - SCALE_MIN) / span * size.width).toFloat()
            val markerWidth = 12.dp.toPx()
            val markerHeight = 10.dp.toPx()
            val tipY = barTop - 2.dp.toPx()

            drawPath(
                path = Path().apply {
                    moveTo(x, tipY)
                    lineTo(x - markerWidth / 2, tipY - markerHeight)
                    lineTo(x + markerWidth / 2, tipY - markerHeight)
                    close()
                },
                color = markerColor,
            )
            // A thin line through the bar pins the marker to an exact position.
            drawLine(
                color = markerColor,
                start = Offset(x, barTop),
                end = Offset(x, barTop + barHeight),
                strokeWidth = 2.dp.toPx(),
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            ScaleLabel(SCALE_MIN.toInt().toString(), Modifier.weight(3.5f))
            ScaleLabel("18.5", Modifier.weight(6.5f))
            ScaleLabel("25", Modifier.weight(5f))
            ScaleLabel("30", Modifier.weight(10f))
        }
    }
}

@Composable
private fun ScaleLabel(text: String, modifier: Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(top = 4.dp),
    )
}

/** Legend mapping each band colour to its name. */
@Composable
fun BmiCategoryLegend(
    highlighted: BmiCategory?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        BmiCategory.entries.forEach { category ->
            LegendRow(
                category = category,
                color = category.color,
                isHighlighted = category == highlighted,
            )
        }
    }
}

@Composable
private fun LegendRow(
    category: BmiCategory,
    color: Color,
    isHighlighted: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
    ) {
        Canvas(
            modifier = Modifier
                .height(12.dp)
                .padding(top = 4.dp)
                .fillMaxWidth(0.06f),
        ) {
            drawCircle(color = color, radius = size.minDimension / 2)
        }
        Text(
            text = category.label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isHighlighted) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
