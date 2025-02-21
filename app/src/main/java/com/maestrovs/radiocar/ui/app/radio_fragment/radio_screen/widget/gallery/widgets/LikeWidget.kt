package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.maestrovs.radiocar.R

/**
 * Created by maestromaster$ on 17/02/2025$.
 */

@Composable
fun LikeWidget(isLiked: Boolean, onLikeClick: () -> Unit, modifier: Modifier = Modifier){
    IconButton(onClick = onLikeClick, modifier = modifier) {
        Icon(
            painter = painterResource(id = if(isLiked) R.drawable.ic_favorite_24 else R.drawable.ic_favorite_stroke_24),
            contentDescription = "Like",
            tint =  Color(0xFFC6CFD5)
        )
    }
}