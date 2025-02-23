package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

/**
 * Created by maestromaster$ on 13/02/2025$.
 */

@Composable
fun ControlBackgroundLight()
{
    Box(
        modifier = Modifier.fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x793A4A57),
                        Color(0x1727272A),
                        Color(0x0027272A),
                        Color(0x1927272A),
                        Color(0x5927272A),
                        Color(0x681E2A34)
                    ),
                   // startY = 0f,
                    //endY = 540f
                )
            )
    )

}

@Preview
@Composable
fun ControlBackgroundLightPreview(){
    ControlBackgroundLight()
}

