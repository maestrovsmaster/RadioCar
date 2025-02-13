package com.maestrovs.radiocar.ui.app.radio_fragment.widgets

/**
 * Created by maestromaster$ on 13/02/2025$.
 */

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PlayProgressWidget(
    modifier: Modifier = Modifier,
    radius: Dp = 50.dp,
    isLoading: Boolean = false,
    numDots: Int = 50,
    baseColor: Color = Color(0xFF4E687E) // Базовий колір
) {
    val infiniteTransition = rememberInfiniteTransition()
    val duration = 800
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier.size(radius * 2)) {
        val center = Offset(size.width / 2, size.height / 2)
        val circleRadius = radius.toPx()

        // **1. Глянцевий ефект кола (з обмеженням alpha)**
        val lighterShade = baseColor.copy(alpha = (0.2f).coerceIn(0f, 1f))
        val darkerShade = baseColor.copy(alpha = (0.25f).coerceIn(0f, 1f))

        val paint = androidx.compose.ui.graphics.Paint()

        drawIntoCanvas { canvas ->
            val shader = RadialGradientShader(
                colors = listOf(lighterShade, darkerShade),
                center = center,
                radius = circleRadius * 1.1f
            )
            paint.shader = shader
            canvas.drawCircle(center, circleRadius, paint)
        }

        // **2. Малюємо точки-комети**
        if (isLoading) {
            for (i in 0 until numDots) {
                val currentAngle = angle + 2.2f * i
                val radians = Math.toRadians(currentAngle.toDouble())

                val dotX = center.x + circleRadius * cos(radians).toFloat()
                val dotY = center.y + circleRadius * sin(radians).toFloat()

                val dotRadius = (circleRadius * 0.002f * i).coerceIn(1f, circleRadius * 0.1f)
                val dotLength = dotRadius

                // **Генеруємо відтінок на основі `baseColor`, обмежуючи значення**
                val hueShift = (i.toFloat() / numDots) * 50f
                val adjustedBlue = (baseColor.blue * (0.8f + hueShift / 200)).coerceIn(0f, 1f)
                val adjustedAlpha = (0.7f).coerceIn(0f, 1f)

                val color = Color(baseColor.red, baseColor.green, adjustedBlue, adjustedAlpha)

                drawRoundRect(
                    color = color,
                    topLeft = Offset(dotX - dotLength / 2, dotY - dotRadius),
                    size = Size(dotLength, dotRadius),
                    cornerRadius = CornerRadius(dotRadius, dotRadius)
                )
            }
        }
    }
}


@Preview
@Composable
fun PlayProgressWidgetPreview() {
    Box(modifier = Modifier.size(150.dp).background(Color.Black).padding(16.dp)) {
        PlayProgressWidget(isLoading = true, numDots = 50, radius = 40.dp, baseColor = Color(
            0xFF4AC0EE
        )
        )
    }

}
