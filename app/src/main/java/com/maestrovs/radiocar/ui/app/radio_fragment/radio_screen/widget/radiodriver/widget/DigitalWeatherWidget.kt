package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Created by maestromaster$ on 16/02/2025$.
 */

@Composable
fun DigitalWeatherWidget(temperature: Double, unit: String, color: Color = Color.Cyan) {
    Box(
        modifier = Modifier,
            //.background(Color.Black)
            //.padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            SevenSegmentNumber(temperature.toInt(), color, 15, 27, paddingSegments = 1.dp)
            Spacer(modifier = Modifier.width(1.dp))
            SpeedUnitText(unit,color, 14.sp)
        }
    }
}



@Preview
@Composable
fun DigitalWeatherWidgetPreview(){
    DigitalWeatherWidget(-14.0, "C", color = Color.Cyan)
}
