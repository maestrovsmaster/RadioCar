package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver.widget

/**
 * Created by maestromaster$ on 16/02/2025$.
 */

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SevenSegmentNumber(
    number: Int,
    color: Color = Color.Cyan,
    segmentWidth: Int = 20,
    segmentHeight: Int = 35,
    paddingSegments: Dp = 4.dp
) {

    val digits = mutableListOf<Int>()

    if (number < 0) {
        digits.add(-1)
        digits.addAll(number.toString().drop(1).map { it.toString().toInt() })
    } else {
        digits.addAll(number.toString().map { it.toString().toInt() })
    }

    // val digits0 = String.format("%03d", number).map { it.toString().toInt() }

    Row {
        var leadingZero = true
        digits.forEachIndexed { index, digit ->
            if (leadingZero && digit == 0 && index < digits.size - 1) return@forEachIndexed
            leadingZero = false

            SevenSegmentDigit(digit, Modifier.size(segmentWidth.dp, segmentHeight.dp), color)
            Spacer(modifier = Modifier.width(paddingSegments))
        }
    }
}

@Composable
fun SevenSegmentDigit(digit: Int, modifier: Modifier = Modifier, color: Color = Color.Cyan) {
    val segmentMap = mapOf(
        -1 to listOf(false, false, false, false, false, false, true),
        0 to listOf(true, true, true, true, true, true, false),
        1 to listOf(false, true, true, false, false, false, false),
        2 to listOf(true, true, false, true, true, false, true),
        3 to listOf(true, true, true, true, false, false, true),
        4 to listOf(false, true, true, false, false, true, true),
        5 to listOf(true, false, true, true, false, true, true),
        6 to listOf(true, false, true, true, true, true, true),
        7 to listOf(true, true, true, false, false, false, false),
        8 to listOf(true, true, true, true, true, true, true),
        9 to listOf(true, true, true, true, false, true, true)
    )

    val segments = segmentMap[digit] ?: listOf(false, false, false, false, false, false, false)

    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = w * 0.15f
        val segmentLength = w * 0.6f
        val segmentHeight = strokeWidth
        val cornerRadius = strokeWidth / 2

        // Список позицій для кожного сегмента (A-G)
        val positions = listOf(
            Offset(w * 0.2f, h * 0.02f), // A (верхній)
            Offset(w * 0.78f, h * 0.12f), // B (верхній правий)
            Offset(w * 0.78f, h * 0.54f), // C (нижній правий)
            Offset(w * 0.2f, h * 0.88f), // D (нижній)
            Offset(w * 0.05f, h * 0.54f), // E (нижній лівий)
            Offset(w * 0.05f, h * 0.12f), // F (верхній лівий)
            Offset(w * 0.2f, h * 0.47f)  // G (середній)
        )

        val segmentAngles = listOf(0f, 90f, 90f, 0f, 90f, 90f, 0f)

        segments.forEachIndexed { index, isOn ->
            if (isOn) {
                val (x, y) = positions[index]
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = if (segmentAngles[index] == 0f)
                        Size(segmentLength, segmentHeight) // Горизонтальні сегменти
                    else
                        Size(segmentHeight, segmentLength), // Вертикальні сегменти
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Fill
                )


            }
        }
    }
}

@Composable
fun SpeedUnitText(unit: String, color: Color = Color.Cyan, fontSize: TextUnit = 22.sp) {
    Text(
        text = unit,
        color = color,
        fontSize = fontSize,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Preview
@Composable
fun SevenSegmentNumberPreview() {
    SevenSegmentNumber(123, color = Color.Cyan)
}

@Preview
@Composable
fun SevenSegmentNumberPreviewTemperature() {
    SevenSegmentNumber(-5, color = Color.Cyan)
}


