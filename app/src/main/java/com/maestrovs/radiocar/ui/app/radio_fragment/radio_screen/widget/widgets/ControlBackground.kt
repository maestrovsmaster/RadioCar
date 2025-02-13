package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
                        Color(0xE927272A),
                        Color(0xFF1E2A34)
                    ),
                    startY = 0f,
                    endY = 800f
                )
            )
    )

}

@Preview
@Composable
fun ControlBackgroundPreview(){
    ControlBackground()
}

