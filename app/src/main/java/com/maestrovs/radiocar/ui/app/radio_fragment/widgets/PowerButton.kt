package com.maestrovs.radiocar.ui.app.radio_fragment.widgets

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.maestrovs.radiocar.R

/**
 * Created by maestromaster$ on 28/02/2025$.
 */

@Composable
fun PowerButton(onClick: () -> Unit, modifier: Modifier = Modifier){
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painter = painterResource(id = R.drawable.ic_power_bold),
            contentDescription = "Next",
            tint =  Color(0xFFC6CFD5)
        )
    }
}