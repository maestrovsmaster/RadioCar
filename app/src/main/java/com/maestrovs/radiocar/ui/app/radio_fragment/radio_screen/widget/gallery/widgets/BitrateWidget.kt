package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.entities.radio.BitrateOption
import com.maestrovs.radiocar.ui.app.ui.theme.baseBlue
import com.maestrovs.radiocar.ui.app.ui.theme.baseBlueLight
import com.maestrovs.radiocar.ui.app.ui.theme.cyan
import com.maestrovs.radiocar.ui.app.ui.theme.red

/**
 * Created by maestromaster$ on 17/02/2025$.
 */

@Composable
fun BitrateWidget(
    bitrate: BitrateOption? = BitrateOption.STANDARD,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    if (bitrate == null) return

//Color(0xFFC6CFD5)

    val text = when (bitrate) {
        BitrateOption.HD -> "HD"
        BitrateOption.HIGH -> "192"
        BitrateOption.STANDARD -> "128"
        BitrateOption.LOW -> "64"
        BitrateOption.VERY_LOW -> "32"
    }

    val tint = when (bitrate) {
        BitrateOption.HD -> cyan
        BitrateOption.HIGH -> baseBlue
        BitrateOption.STANDARD -> baseBlueLight
        BitrateOption.LOW -> baseBlueLight
        BitrateOption.VERY_LOW -> red
    }

    Box(
        modifier = modifier
            .background(Color.Transparent)
            .height(36.dp)
            .width(36.dp)
            .clickable { onClick() }
    ) {

        Icon(
            painter = painterResource(id = R.drawable.ic_rect_36),
            contentDescription = "Like",
            tint = baseBlueLight,
            modifier = Modifier.align(Alignment.Center).alpha(0.6f)
        )

        Text(
            text = text,
            color = tint,
            modifier = Modifier
                .padding(end = 0.dp, top = 0.dp)
                .align(Alignment.Center),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview
@Composable
fun BitrateWidgetPreview() {
    Column(Modifier
        .background(Color.Black)
        .padding(16.dp)) {
        BitrateWidget(bitrate = BitrateOption.HD, onClick = {})
        BitrateWidget(bitrate = BitrateOption.HIGH, onClick = {})
        BitrateWidget(bitrate = BitrateOption.STANDARD, onClick = {})
        BitrateWidget(bitrate = BitrateOption.LOW, onClick = {})
        BitrateWidget(bitrate = BitrateOption.VERY_LOW, onClick = {})

    }
}