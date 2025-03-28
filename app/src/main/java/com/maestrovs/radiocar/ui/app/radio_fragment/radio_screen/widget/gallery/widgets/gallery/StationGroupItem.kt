package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets.gallery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.entities.radio.StationStream
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget.BackgroundCover
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.AudioVisualizerScreenIonic
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.AudioVisualizerScreenMicro
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.DynamicShadowCard
import com.maestrovs.radiocar.ui.app.ui.theme.primary
import com.maestrovs.radiocar.ui.app.ui.theme.primaryLight

/**
 * Created by maestromaster$ on 13/02/2025$.
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StationGroupItem(
    item: StationGroup,
    onItemClick: (StationGroup) -> Unit,
    onItemLongClick: (StationGroup) -> Unit,
    modifier: Modifier = Modifier
) {

    val audioSessionIdFlow by PlayerStateManager.audioSessionIdFlow.collectAsStateWithLifecycle(null)

    val currentPlayingGroup by PlayerStateManager.currentGroup.collectAsStateWithLifecycle(null)
    val showVisualizer = currentPlayingGroup?.name == item.name


    Box(
        modifier = modifier//.fillMaxSize()
            .combinedClickable(
                onClick = { onItemClick(item) },
                onLongClick = {
                    onItemLongClick(item)
                }
            )
    ) {

        DynamicShadowCard(
            modifier = Modifier
                .padding(0.dp)
                .fillMaxSize(), contentColor = primary,
            elevation = 5
        ) {

            if (audioSessionIdFlow != null && showVisualizer) {


                AudioVisualizerScreenIonic(modifier = Modifier.fillMaxSize())

            }


            DynamicShadowCard(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxSize(), contentColor = primary,
                elevation = 3
            ) {
                item.favicon?.let { imgUrl ->

                    BackgroundCover(imageUrl = imgUrl)
                }


                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x3C3A4A57),
                                    Color(0x8F27272A),
                                    Color(0xFF1E2A34)
                                ),
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.name ?: "Unknown Station",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 2
                    )

                }


            }


        }


    }
}

@Composable
@Preview
fun StationGroupItemPreview() {

    StationGroupItem(item = StationGroup(
        name = "Test FM",
        streams = listOf<StationStream>(),
        favicon = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSPZ-WDFZ5Pz-lBPZj9GSU2LbBSEmlTVOtRmQ&s",
        isFavorite = true,
        stations = listOf(),
    ), onItemClick = {}, onItemLongClick = {})

}
