package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver.widget

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
fun LottieCover()
{
    Box(
        modifier = Modifier.fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xCE293138),
                        Color(0xD02D2A2D),
                        Color(0x652D2D31),
                        Color(0xDC2D2A2D),
                        Color(0xFF1E2A34),
                       Color(0xFF212F3F)
                    ),
                   // startY = 0f,
                   // endY = 800f
                )
            )
    )

}

@Preview
@Composable
fun LottieCoverPreview(){
    LottieCover()
}

