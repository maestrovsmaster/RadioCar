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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.maestrovs.radiocar.data.repository.mock.MockStationRepository
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets.LikeWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget.BackgroundCover
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget.ControlBackgroundLight
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.repositories.SharedPreferencesRepositoryMock
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.AudioVisualizerScreen
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.AudioVisualizerScreenTiny
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.DynamicShadowCard
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.MarqueeText
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.PlayButton
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.PlayControlWidget
import com.maestrovs.radiocar.ui.app.stations_list.RadioListViewModel
import com.maestrovs.radiocar.ui.app.ui.theme.primary

/**
 * Created by maestromaster on 11/02/2025$.
 */

@Composable
fun MiniPlayerWidgetHorizontal(
    viewModel: RadioListViewModel,
    modifier: Modifier = Modifier,
) {
    val stationList = viewModel.stationFlow.collectAsLazyPagingItems()

    val currentGroup by PlayerStateManager.currentGroup.collectAsStateWithLifecycle(null)
    val isPlayingFlow by PlayerStateManager.isPlayingFlow.collectAsStateWithLifecycle(null)
    val isBufferingFlow by PlayerStateManager.isBufferingFlow.collectAsStateWithLifecycle(false)
    val audioSessionIdFlow by PlayerStateManager.audioSessionIdFlow.collectAsStateWithLifecycle(null)
    val isLikedFlow by PlayerStateManager.isLikedFlow.collectAsStateWithLifecycle(false)
    val songMetadataFlow by PlayerStateManager.songMetadataFlow.collectAsStateWithLifecycle(null)

    val isPlaying = if(isPlayingFlow == null) false else isPlayingFlow!!.first


    Box(
        modifier = modifier
    ) {

        DynamicShadowCard(
            modifier = Modifier,
            contentColor = primary,
        ) {

            Column {
                Row(
                    modifier = Modifier
                        //.fillMaxSize()
                    ,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(90.dp)
                            .height(90.dp)
                            .background(Color.LightGray)
                    ) {
                        currentGroup?.favicon?.let {
                            BackgroundCover(imageUrl = currentGroup!!.favicon)
                        }
                    }

                    Column(
                        modifier = Modifier
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
                            gradientEdgeColor = Color(0xFF343E46),
                        )
                    }


                    Row(
                        modifier = Modifier
                            .padding(6.dp)
                    ) {
                        LikeWidget(isLiked = isLikedFlow, onLikeClick =  {
                            viewModel.setIsLike(currentGroup!!, !isLikedFlow)
                            stationList.refresh()
                        })
                    }

                }

                PlayControlWidget(
                    modifier =Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp).fillMaxWidth() ,
                    //.align(Alignment.BottomCenter)
                    // .padding(16.dp),
                    isPlaying = isPlaying,
                    isLoading = isBufferingFlow,
                    onPrevClick = {
                        viewModel.prev()
                    },
                    onPlayPauseClick = {
                        currentGroup?.let { group ->
                            if (isPlaying) viewModel.stop() else viewModel.playGroup(
                                group
                            )
                        }

                    },
                    onNextClick = {
                        viewModel.next()
                    })


                if (audioSessionIdFlow != null) {

                    Box(
                        modifier = Modifier
                            //.align(Alignment.BottomStart)
                            .padding(bottom = 0.dp)
                            .padding(0.dp)

                            .height(50.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x0027272A),
                                        Color(0x3227272A),
                                        Color(0x4027272A),
                                        Color(0xC927272A),
                                    ),
                                )
                            )
                    ) {
                        AudioVisualizerScreenTiny( modifier = Modifier.fillMaxWidth().height(50.dp))
                    }
                }

            }






            ControlBackgroundLight()

        }
    }
}


@Composable
@Preview
fun MiniPlayerWidgetHorizontalPreview() {

    MiniPlayerWidgetHorizontal(
        RadioListViewModel(
            MockStationRepository(),
            SharedPreferencesRepositoryMock()

        ),
        modifier = Modifier.fillMaxWidth()
            .height(190.dp)
    )
}
