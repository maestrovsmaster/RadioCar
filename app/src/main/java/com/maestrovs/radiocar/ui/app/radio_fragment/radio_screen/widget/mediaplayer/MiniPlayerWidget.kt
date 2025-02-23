package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.repository.mock.FakeStationRepository
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets.LikeWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget.BackgroundCover
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget.ControlBackground
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget.ControlBackgroundLight
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.AudioVisualizerManager
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.AudioVisualizerScreenMicro
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.AudioVisualizerScreenTiny
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.DynamicShadowCard
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.MarqueeText
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.PlayButton
import com.maestrovs.radiocar.ui.app.stations_list.RadioListViewModel
import com.maestrovs.radiocar.ui.app.ui.theme.primary

/**
 * Created by maestromaster on 11/02/2025$.
 */

@Composable
fun MiniPlayerWidget(
    viewModel: RadioListViewModel,
    modifier: Modifier = Modifier,
    onClickLike: (StationGroup, Boolean) -> Unit = {_,_ -> }
) {
    //val playerState by PlayerStateManager.playerState.collectAsStateWithLifecycle()

    val currentGroup by PlayerStateManager.currentGroup.collectAsStateWithLifecycle(null)
    val isPlayingFlow by PlayerStateManager.isPlayingFlow.collectAsStateWithLifecycle(null)
    val isBufferingFlow by PlayerStateManager.isBufferingFlow.collectAsStateWithLifecycle(false)
    val audioSessionIdFlow by PlayerStateManager.audioSessionIdFlow.collectAsStateWithLifecycle(null)
    val isLikedFlow by PlayerStateManager.isLikedFlow.collectAsStateWithLifecycle(false)
    val songMetadataFlow by PlayerStateManager.songMetadataFlow.collectAsStateWithLifecycle(null)

    // if (playerState.currentStation == null) return // Не показуємо, якщо станції нема

    val isPlaying = if(isPlayingFlow == null) false else isPlayingFlow!!.first

    Box(
        modifier = modifier

        //.background(Color.Black)
        //.padding(16.dp),
        //contentAlignment = Alignment.Center
    ) {

        DynamicShadowCard(
            modifier = Modifier,
            // .padding(16.dp)
            // .fillMaxWidth(),
            contentColor = primary, backgroundColor = primary
        ) {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    //.padding(8.dp)
                    ,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .height(90.dp)
                        .background(Color.LightGray)
                    //.padding(bottom = 40.dp, top = 0.dp, )
                ) {
                    currentGroup?.favicon?.let {
                        BackgroundCover(imageUrl = currentGroup!!.favicon)
                    }


                }

                Column(
                    modifier = Modifier
                        //.align(Alignment.TopStart)
                        .padding(16.dp)
                        .weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = currentGroup?.name ?: "Unknown Station",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MarqueeText(
                        text = "\uD83C\uDFB5 ${songMetadataFlow ?: "Unknown song"}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        gradientEdgeColor = primary.copy(alpha = 0.9f),
                    )
                }
                currentGroup?.let {
                    PlayButton(
                        isPlaying = isPlaying,
                        isLoading = isBufferingFlow,
                        onPlayPauseClick = {
                            if (isPlaying) {
                                viewModel.stop()
                            } else {
                                viewModel.playGroup(currentGroup!!)

                            }
                        },
                        tintColor = Color(0xFFB0DCF5),
                        modifier = Modifier.size(50.dp),
                        radius = 24.dp
                    )
                }

                currentGroup?.let {

                    Row(
                        modifier = Modifier
                            //.align(Alignment.TopEnd)
                            .padding(6.dp)
                    ) {
                        LikeWidget(isLiked = isLikedFlow, onLikeClick =  {
                            //viewModel.setIsLike(currentGroup!!, !isLikedFlow)
                            onClickLike(currentGroup!!, !isLikedFlow)
                        })

                    }

                }

            }



            ControlBackgroundLight()

            if (audioSessionIdFlow != null) {
                val visualizer =
                    AudioVisualizerManager.getVisualizer(audioSessionIdFlow!!, step = 32)

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 0.dp)
                        .padding(0.dp)
                        .width(90.dp)
                        .height(40.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x0027272A),
                                    Color(0x3227272A),
                                    Color(0x4027272A),
                                    Color(0xC927272A),
                                ),
                                // startY = 0f,
                                //endY = 240f
                            )
                        )
                ) {
                    AudioVisualizerScreenTiny( modifier = Modifier.width(90.dp).height(40.dp))
                }
            }



        }


    }
}


@Composable
@Preview
fun MiniPlayerWidgetPreview() {

    MiniPlayerWidget(
        RadioListViewModel(
            FakeStationRepository()
        ),
        modifier = Modifier.fillMaxWidth()
            .height(90.dp)
    )
}
