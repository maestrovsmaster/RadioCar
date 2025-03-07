package com.maestrovs.radiocar.ui.app.radio_fragment.widgets

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.maestrovs.radiocar.R
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import com.maestrovs.radiocar.ui.app.ui.theme.baseBlue

/**
 * Created by maestromaster$ on 27/02/2025$.
 */

data class VolumeItem(val volume: Float, val isFilled: Boolean)

@Composable
fun VolumeBar(
    volume: Float,
    segmentsCount: Int,
    color: Color = Color.White,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {

    if (volume > 1f) volume.coerceAtMost(1f)
    if (volume < 0f) volume.coerceAtLeast(0f)


    val step = 1f / segmentsCount
    val items = List(segmentsCount) {
        val segmentVolume = step + it * step
        val isFilled = volume >= segmentVolume
        VolumeItem(segmentVolume, isFilled)
    }


    Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {

        items.map { volumeItem ->
            ResizedImageBox(
                size = 36.dp,
                heightPercent = volumeItem.volume,
                isFilled = volumeItem.isFilled,
                color = color,
                onValueChange = onValueChange,
                modifier.weight(1f)
            )
        }


    }

}


@Composable
fun ResizedImageBox(
    size: Dp,
    heightPercent: Float,
    isFilled: Boolean,
    color: Color = Color.White,
    onValueChange: (Float) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .size(size)
        .pointerInput(Unit) {
            detectTapGestures(
              //  onTap = { onValueChange(heightPercent) },
                onPress = { onValueChange(heightPercent) }
            )
        }
    )
    {
        Image(
            painter = painterResource(id = if (isFilled) R.drawable.ic_square_filled else R.drawable.ic_square),
            contentDescription = null,
            modifier = Modifier
                .size(width = size, height = (size.value/4 + (size.value-size.value/4) * heightPercent).dp)
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillBounds,
            alpha = if (isFilled) 1f else 0.5f,
            colorFilter = if (isFilled) androidx.compose.ui.graphics.ColorFilter.tint(color)
            else androidx.compose.ui.graphics.ColorFilter.tint(color)

        )
    }

}


@Preview
@Composable
fun VolumeBarPreview() {


    var currentVolume by remember {  mutableStateOf(0.6f) }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.Black)
    ) {
        VolumeBar(
            volume = currentVolume, segmentsCount = 10, onValueChange = {
                Log.d("VolumeBar", "VolumeBarPreview: $it")
                currentVolume = it
            }, color = baseBlue, modifier = Modifier
                .height(50.dp)
                .width(200.dp)
        )
    }

}
