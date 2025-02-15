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
fun ControlBackground()
{
    Box(
        modifier = Modifier.fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xCE3A4A57),
                        Color(0xCE27272A),
                        Color(0x4D27272A),
                        Color(0xB427272A),
                        Color(0xC927272A),
                        Color(0xFF1E2A34)
                    ),
                   // startY = 0f,
                    endY = 540f
                )
            )
    )

}

@Preview
@Composable
fun ControlBackgroundPreview(){
    ControlBackground()
}

