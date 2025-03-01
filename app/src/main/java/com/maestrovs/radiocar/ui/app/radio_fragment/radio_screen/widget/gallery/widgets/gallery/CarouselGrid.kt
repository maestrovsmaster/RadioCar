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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState

/**
 * Created by maestromaster$ on 28/02/2025$.
 */

@Composable
fun CenteredCarousel2(
    items: List<StationGroup>,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 80.dp,
    itemWidth: Dp = 100.dp,
    onItemClick: (StationGroup) -> Unit,
    onItemLongClick: (StationGroup) -> Unit
) {
    val listState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current // Вібрація

    Box(
        modifier = modifier
            .height(itemHeight * 3f)
            .padding(top = 16.dp, bottom = 16.dp)
    ) {
        LazyHorizontalGrid(
            //columns = GridCells.Fixed(3),
            rows = GridCells.Fixed(2),
            state = listState,
            contentPadding = PaddingValues(horizontal = itemWidth / 2),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(items) { index, item ->
                val layoutInfo = listState.layoutInfo




                Box(
                    modifier = Modifier
                        .width(itemWidth )
                        .height(itemHeight )
                        //.align(Alignment.CenterHorizontally)
                        .clickable {
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        }

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
fun CenteredCarousel2Preview() {
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

    CenteredCarousel2(items = sampleItems, modifier = Modifier.fillMaxWidth(), onItemClick = {


    }, onItemLongClick = {})

}

