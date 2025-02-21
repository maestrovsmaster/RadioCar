package com.maestrovs.radiocar.ui.app.stations_list.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.entities.radio.StationStream
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets.LikeWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget.BackgroundCover
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.PlayButton
import com.murgupluoglu.flagkit.FlagKit

/**
 * Created by maestromaster$ on 19/02/2025$.
 */

@Composable
fun StationItem(
    station: StationGroup, onItemClick: (StationGroup) -> Unit, onLikeClick: (Boolean) -> Unit,
    onPlayClick: (StationGroup) -> Unit,
    onPausedClick: (StationGroup) -> Unit
) {

    val currentPlayingGroup by PlayerStateManager.currentGroup.collectAsStateWithLifecycle(null)
    val isPlayingFlow by PlayerStateManager.isPlayingFlow.collectAsStateWithLifecycle(null)
    val isBufferingFlow by PlayerStateManager.isBufferingFlow.collectAsStateWithLifecycle(false)
    val audioSessionIdFlow by PlayerStateManager.audioSessionIdFlow.collectAsStateWithLifecycle(null)
    val isLikedFlow by PlayerStateManager.isLikedFlow.collectAsStateWithLifecycle(false)
    val songMetadataFlow by PlayerStateManager.songMetadataFlow.collectAsStateWithLifecycle(null)

    val isPlayingState = if(isPlayingFlow == null) false else isPlayingFlow!!.first
    val showPlaying = currentPlayingGroup == station   && isPlayingState

    val showBuffering = currentPlayingGroup == station   && isBufferingFlow

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onItemClick(station)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
                .background(Color.LightGray)
            //.padding(bottom = 40.dp, top = 0.dp, )
        ) {

            BackgroundCover(imageUrl = station.favicon)

            station.countryCode?.let {
                val flagId = FlagKit.getResId(LocalContext.current, station.countryCode)
                Image(
                    modifier = Modifier.align(Alignment.TopEnd),
                    painter = painterResource(id = flagId),
                    contentDescription = "Previous",

                    )
            }
        }



        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = station.name)//, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            //Text(text = station.country, fontSize = 14.sp, color = Color.Gray)
        }

        PlayButton(
            isPlaying = showPlaying,
            isLoading = showBuffering,
            onPlayPauseClick = {
                if (showPlaying) {
                    onPausedClick(station)
                } else {
                    onPlayClick(station)
                }
            },
            tintColor = Color(0xFFB0DCF5),
            modifier = Modifier.size(50.dp),
            radius = 20.dp
        )

        LikeWidget(
            isLiked = station.isFavorite,
            onLikeClick = {
                onLikeClick(!station.isFavorite)
            },
        )
    }
}

@Preview
@Composable
fun StationItemPreview() {
    val group = StationGroup(
        name = "Test",
        streams = listOf<StationStream>(),
        favicon = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSPZ-WDFZ5Pz-lBPZj9GSU2LbBSEmlTVOtRmQ&s",
        isFavorite = true
    )
    StationItem(
        station = group,
        onItemClick = {},
        onLikeClick = {},
        onPlayClick = {},
        onPausedClick = {}
    )
}
