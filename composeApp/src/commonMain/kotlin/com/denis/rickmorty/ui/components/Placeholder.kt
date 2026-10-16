package com.denis.rickmorty.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.denis.rickmorty.ui.AppTheme
import androidx.compose.ui.tooling.preview.Preview

enum class PlaceholderDirection { VERTICAL, DIAGONAL }
@Composable
fun Placeholder(size: Dp = 100.dp, baseColor: Color = Color.Gray, shape: Shape = RoundedCornerShape(12.0.dp), direction: PlaceholderDirection = PlaceholderDirection.DIAGONAL) {
    val infiniteTransition = rememberInfiniteTransition()
    val localDensity = LocalDensity.current
    val start = with(localDensity) { -(size).toPx() }
    val value by infiniteTransition.animateFloat(
        initialValue = start,
        targetValue = with(localDensity) { size.toPx() },
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
    )

    when(direction) {
        PlaceholderDirection.VERTICAL -> VerticalPlaceholder(baseColor, size, shape, value)
        PlaceholderDirection.DIAGONAL -> DiagonalPlaceholder(baseColor, size, shape, value, localDensity)
    }
}

@Composable
private fun VerticalPlaceholder(
    baseColor: Color,
    size: Dp,
    shape: Shape,
    value: Float
) {
    val boxColors = arrayOf(
        0.1f to baseColor.copy(alpha = 0f),
        0.6f to baseColor.copy(alpha = 0.4f),
        1f to baseColor.copy(alpha = 0f),
    )
    Box(
        Modifier.size(size)
            .clip(shape)
            .background(baseColor.copy(alpha = 0.1f), shape = shape)
    ) {
        Box(
            Modifier.fillMaxWidth()
                .offset { IntOffset(0, value.toInt()) }
                .height(size / 2)
                .graphicsLayer()
                .background(Brush.verticalGradient(colorStops = boxColors))
        )
    }
}

@Composable
private fun DiagonalPlaceholder(
    baseColor: Color,
    size: Dp,
    shape: Shape,
    value: Float,
    localDensity: Density
) {
    val canvasColors = arrayOf(
        0.1f to baseColor.copy(alpha = 0f),
        0.6f to baseColor.copy(alpha = 0.4f),
        1f to baseColor.copy(alpha = 0f),
    )
    Canvas(
        Modifier.size(size)
            .clip(shape)
            .background(baseColor.copy(alpha = 0.1f), shape = shape)
    ) {
        rotate(degrees = -45f) {
            drawRect(
                brush = Brush.verticalGradient(colorStops = canvasColors),
                topLeft = Offset(-50f, value),
                size = androidx.compose.ui.geometry.Size(
                    with(localDensity) { size.toPx() * 2 },
                    with(localDensity) { (size / 2).toPx() }
                )
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PlaceholderPreview() {
    AppTheme {
        Row(Modifier.background(Color.White).padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Placeholder()
            Text("Hello world")
        }
    }
}