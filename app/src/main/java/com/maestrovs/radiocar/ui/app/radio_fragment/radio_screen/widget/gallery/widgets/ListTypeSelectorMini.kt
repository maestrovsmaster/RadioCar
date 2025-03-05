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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.ui.radio.ListType

/**
 * Created by maestromaster$ on 17/02/2025$.
 */

@Composable
fun ListTypeSelector(
    currentListType: ListType,
    modifier: Modifier = Modifier,
    onChangeType: (ListType) -> Unit,
) {

   // val haptic = LocalHapticFeedback.current

    val listTypes =
        ListType.values().filter { it != ListType.Searched } // Перелічуємо всі типи списків
    Column(
        modifier = modifier
            .width(60.dp)
            //.height(150.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF1E2A34),
                        Color(0xDA27272A),
                        Color(0x5E27272A),
                        Color(0x0027272A),
                        // Color(0xC927272A),
                        // Color(0xFF1E2A34)
                    ),
                    endX = 290f
                    // startY = 0f,
                    //endY = 540f
                )
            )

    ) {
        listTypes.forEach { type ->
            val isActive = currentListType == type
            ListTypeItem(
                listType = type,
                isActive = isActive,
                onClick = {
                  //  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onChangeType(type)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ListTypeItem(
    listType: ListType,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundShape = if (isActive) {
        RoundedCornerShape(
            topStart = 16.dp,    // Верхній лівий кут
            topEnd = 0.dp,      // Верхній правий кут
            bottomStart = 16.dp, // Нижній лівий кут
            bottomEnd = 0.dp     // Нижній правий кут
        )
    } else {
        RoundedCornerShape(0.dp)
    }


    val gradientColors = if (isActive) {
        listOf(
            Color(0xF8334450),
            Color(0xE13C5060),
            Color(0xBF4C6377),
            Color(0x00000000),
        ) // Активний стан
    } else {
        listOf(Color(0x003E4D57), Color(0x00434E56)) // Неактивний стан
    }

    val tintColor = if (isActive) {
        Color.White
    } else {
        Color(0xFF7F8D98)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            //.padding(8.dp)
            .clickable { onClick() }
            //.width(60.dp) // фіксована ширина
            //.height(30.dp) // висота для відображення зображення та бекграунду
            .background(
                brush = Brush.horizontalGradient(gradientColors),
                shape = backgroundShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = getImageResId(listType)),
            contentDescription = listType.name,
            colorFilter = ColorFilter.tint(tintColor)
            // modifier = Modifier.size(40.dp),
        )
    }
}

fun getImageResId(listType: ListType): Int {
    return when (listType) {
        ListType.All -> R.drawable.ic_all_24
        ListType.Favorites -> R.drawable.ic_favorite_24
        ListType.Recent -> R.drawable.ic_recent_24
        ListType.Searched -> R.drawable.ic_search
    }
}

@Preview
@Composable
fun ListTypeSelectorPreview() {
    // val currentListType by viewModel.currentListType.observeAsState(ListType.All)

    ListTypeSelector(
        currentListType = ListType.All,
        onChangeType = { }
    )
}
