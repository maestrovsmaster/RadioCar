package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.ui.radio.ListType

/**
 * Created by maestromaster$ on 17/02/2025$.
 */

@Composable
fun ExtendSearchButtonWidget(

    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val listTypes =
        ListType.values().filter { it != ListType.Searched } // Перелічуємо всі типи списків
    Box(
        modifier = modifier
            .width(40.dp)
            .height(150.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(



                        Color(0x1946545E),
                        Color(0xD027272A),
                        Color(0xE827272A),
                        Color(0xFF1E2A34),

                    ),
                    //endX = 290f
                    // startY = 0f,
                    //endY = 540f
                )
            )

    ) {
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_unfold_more),
                contentDescription = "Next",
                tint =  Color(0xFFC6CFD5)
            )
        }
    }
}





@Preview
@Composable
fun ExtendSearchButtonWidgetPreview() {
    // val currentListType by viewModel.currentListType.observeAsState(ListType.All)

    ExtendSearchButtonWidget(
        onClick = { }
    )
}
