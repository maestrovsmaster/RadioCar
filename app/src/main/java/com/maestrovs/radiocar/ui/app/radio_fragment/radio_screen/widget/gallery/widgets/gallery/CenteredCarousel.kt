package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets.gallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.entities.radio.StationStream
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Created by maestromaster$ on 13/02/2025$.
 */

@Composable
fun CenteredCarousel(
    items: List<StationGroup>, // (imageUrl, title)
    modifier: Modifier = Modifier,
    itemHeight: Dp = 120.dp,
    itemWidth: Dp = 140.dp,
    onItemClick: (StationGroup) -> Unit,
    onItemLongClick: (StationGroup) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current // Вібрація

    Box(modifier = modifier
        .height(itemHeight * 1.5f)
        .padding(top = 16.dp, bottom = 16.dp)) {
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = (itemWidth / 2)),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(items) { index, item ->
                val layoutInfo = listState.layoutInfo
                val viewportCenter = layoutInfo.viewportEndOffset / 2
                val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }

                // Відстань елемента від центру
                val distanceFromCenter = itemInfo?.let {
                    ((it.offset + it.size / 2) - viewportCenter).toFloat()
                } ?: 0f

                // Масштабування з меншим зменшенням (мінімум 0.8)
                val scaleFactor =
                    1f - (kotlin.math.abs(distanceFromCenter * 0.1f) / viewportCenter).coerceIn(
                        0f,
                        0.3f
                    )

                Box(
                    modifier = Modifier
                        .width(itemWidth * scaleFactor)
                        .height(itemHeight * scaleFactor)
                        //.clip(RoundedCornerShape(16.dp))
                        //.background(Color.Green)
                        .align(Alignment.Center)
                        //  .align(Alignment.Vertical) // Центруємо по вертикалі
                        .clickable {
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        }
                        .graphicsLayer(
                            scaleX = scaleFactor,
                            scaleY = scaleFactor
                        )
                ) {
                    StationGroupItem(
                        item = item,
                        onItemClick = onItemClick,
                        onItemLongClick = onItemLongClick
                    )
                }
            }
        }
    }


    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
    }
}


@Preview
@Composable
fun CenteredCarouselPreview() {
    val sampleItems = listOf(
        StationGroup(
            name = "Test",
            streams = listOf<StationStream>(),
            favicon = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSPZ-WDFZ5Pz-lBPZj9GSU2LbBSEmlTVOtRmQ&s",
            stations = listOf(),
            isFavorite = true
        ),
        StationGroup(
            name = "Test",
            streams = listOf<StationStream>(),
            favicon = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSPZ-WDFZ5Pz-lBPZj9GSU2LbBSEmlTVOtRmQ&s",
            stations = listOf(),
            isFavorite = false
        ),
        StationGroup(
            name = "Test",
            streams = listOf<StationStream>(),
            favicon = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSPZ-WDFZ5Pz-lBPZj9GSU2LbBSEmlTVOtRmQ&s",
            stations = listOf(),
            isFavorite = true
        ),
        StationGroup(
            name = "Test",
            streams = listOf<StationStream>(),
            favicon = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSPZ-WDFZ5Pz-lBPZj9GSU2LbBSEmlTVOtRmQ&s",
            stations = listOf(),
            isFavorite = false
        ),
        StationGroup(
            name = "Test",
            streams = listOf<StationStream>(),
            favicon = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSPZ-WDFZ5Pz-lBPZj9GSU2LbBSEmlTVOtRmQ&s",
            stations = listOf(),
            isFavorite = true
        )
    )

    CenteredCarousel(items = sampleItems, modifier = Modifier.fillMaxWidth(), onItemClick = {


    }, onItemLongClick = {})

}
