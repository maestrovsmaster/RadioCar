package com.maestrovs.radiocar.ui.app.radio_fragment.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.ui.app.ui.theme.baseBlueLight

/**
 * Created by maestromaster$ on 13/02/2025$.
 */

@Composable
fun PlayControlWidget(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    isLoading: Boolean = false,
    onPrevClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {

        val tintColor = baseBlueLight.copy(alpha = 0.95f)
        IconButton(onClick = onPrevClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_prev_32),
                contentDescription = "Previous",
                tint = tintColor,
            )

        }

        PlayButton(
            isPlaying = isPlaying,
            isLoading = isLoading,
            onPlayPauseClick = onPlayPauseClick,
            tintColor = Color(0xFFB0DCF5 ),
            modifier = Modifier.size(52.dp)
        )



        IconButton(onClick = onNextClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_next_32),
                contentDescription = "Next",
                tint = tintColor
            )
        }
    }
}

@Preview
@Composable
fun PlayControlWidgetPreview() {
    Box(
        modifier = Modifier
            .width(200.dp)
            .background(Color.Black)
            .padding(16.dp)
    ) {
        PlayControlWidget(
            isPlaying = true,
            onPrevClick = {},
            onPlayPauseClick = {},
            onNextClick = {})
    }
}