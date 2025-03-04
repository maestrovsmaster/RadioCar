package com.maestrovs.radiocar.ui.app.radio_fragment.widgets

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.maestrovs.radiocar.R

/**
 * Created by maestromaster$ on 20/02/2025$.
 */

@Composable
fun PlayButton(
    isPlaying: Boolean,
    isLoading: Boolean = false,
    onPlayPauseClick: () -> Unit,
    tintColor: Color,
    modifier: Modifier = Modifier,
    radius: Dp = 26.dp
) {

    var parentSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier.onSizeChanged { parentSize = it },
        contentAlignment = Alignment.Center
    ) {

        PlayProgressWidget(
            isLoading = isLoading, numDots = 50, radius = radius, baseColor = Color(
                0xFFB0DCF5
            )
        )

        IconButton(
            onClick = onPlayPauseClick
        ) {

            Icon(
                painter = painterResource(
                    id = if (isPlaying) R.drawable.ic_pause_32
                    else R.drawable.ic_play_32
                ),
                contentDescription = "Play",
                tint = tintColor
            )
        }
    }
}

@Preview
@Composable
fun PlayButtonPreview() {
    PlayButton(isPlaying = false, isLoading = true, onPlayPauseClick = {}, tintColor = Color.Blue)
}