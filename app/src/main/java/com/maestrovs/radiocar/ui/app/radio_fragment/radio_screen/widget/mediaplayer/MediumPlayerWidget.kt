package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maestrovs.radiocar.data.entities.radio.BitrateOption
import com.maestrovs.radiocar.data.repository.mock.MockStationRepository
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets.BitrateWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets.LikeWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget.BackgroundCover
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget.ChooseBitratesDialog
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget.ControlBackground
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.repositories.SharedPreferencesRepositoryMock
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.visualizers.AudioVisualizerScreen
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.DynamicShadowCard
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.FaviconCard
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.MarqueeText
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.PlayControlWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.VolumeBar
import com.maestrovs.radiocar.ui.app.ui.theme.baseBlue
import com.maestrovs.radiocar.ui.app.ui.theme.baseBlueLight
import com.maestrovs.radiocar.ui.app.ui.theme.primary

/**
 * Created by maestromaster on 11/02/2025$.
 */

@Composable
fun MediumPlayerWidget(
    viewModel: RadioViewModel,
    modifier: Modifier = Modifier
) {
    //val playerState by PlayerStateManager.playerState.collectAsStateWithLifecycle()

    val currentGroup by PlayerStateManager.currentGroup.collectAsStateWithLifecycle(null)
    val isPlayingFlow by PlayerStateManager.isPlayingFlow.collectAsStateWithLifecycle(null)
    val isBufferingFlow by PlayerStateManager.isBufferingFlow.collectAsStateWithLifecycle(false)
    val audioSessionIdFlow by PlayerStateManager.audioSessionIdFlow.collectAsStateWithLifecycle(null)
    val isLikedFlow by PlayerStateManager.isLikedFlow.collectAsStateWithLifecycle(false)
    val songMetadataFlow by PlayerStateManager.songMetadataFlow.collectAsStateWithLifecycle(null)

    val bitrateFlow by PlayerStateManager.preferredBitrateOptionFlow.collectAsStateWithLifecycle(
        null
    )
    val bitrate = bitrateFlow ?: BitrateOption.STANDARD

    val volumeFlow by PlayerStateManager.volumeFlow.collectAsStateWithLifecycle(1f)

    var showDialog by remember { mutableStateOf(false) }

    val isPlaying = if (isPlayingFlow == null) false else isPlayingFlow!!.first
    val isLoadingData by viewModel.isLoading.collectAsStateWithLifecycle()

    val isLoading = isBufferingFlow || isLoadingData;


    val firstBitrate = currentGroup?.streams?.first()?.bitrate


    Box(
        modifier = modifier
    ) {

        DynamicShadowCard(
            modifier = Modifier,
            contentColor = primary,
        ) {

            /*currentGroup?.favicon?.let { imgUrl ->
                Box(
                    modifier = Modifier
                        //.fillMaxWidth()
                        //.background(Color.Green)
                        .padding(bottom = 40.dp, top = 0.dp)
                ) {
                    BackgroundCover(imageUrl = imgUrl)
                }


            }*/


            ControlBackground()


            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .align(Alignment.Start),
                    //.background(Color.Blue)
                    verticalAlignment = Alignment.Top
                ) {
                    FaviconCard(
                        currentGroup?.favicon,
                        modifier = Modifier
                            .width(90.dp)
                            .height(90.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))


                    Column {
                        Text(
                            text = currentGroup?.name ?: "Choose station",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF1FFFF), //Color.White,
                            maxLines = 2,
                            modifier = Modifier.weight(1f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            BitrateWidget(bitrate = firstBitrate, onClick = {
                                // showDialog = true
                            }, modifier = Modifier)
                            //Spacer(modifier = Modifier.weight(1f))
                            LikeWidget(isLiked = isLikedFlow, onLikeClick = {
                                viewModel.setIsLike(currentGroup!!, !isLikedFlow)
                            }, modifier = Modifier.background(Color.Transparent))
                        }


                    }


                }

                Spacer(modifier = Modifier.height(10.dp))

                MarqueeText(
                    text = " ${songMetadataFlow ?: ""}", //🎵
                    color = Color(0xFFECEEF1),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    gradientEdgeColor = Color(0xFF272E33).copy(alpha = 0.9f),
                )

                Spacer(modifier = Modifier.weight(1f))


                if (audioSessionIdFlow != null) {
                    AudioVisualizerScreen()
                }
                VolumeBar(
                    volume = volumeFlow, segmentsCount = 10, onValueChange = {
                        viewModel.setVolume(it)
                    }, color = baseBlueLight.copy(alpha = 0.85f), modifier = Modifier
                        .height(36.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                PlayControlWidget(
                    modifier =Modifier.padding(top = 16.dp) ,
                        //.align(Alignment.BottomCenter)
                       // .padding(16.dp),
                    isPlaying = isPlaying,
                    isLoading = isLoading,
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
            }






           /* Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 66.dp)
                    .padding(16.dp)
            ) {

            }*/






        }

        if (showDialog) {

            var bitratesList = listOf<BitrateOption>()

            currentGroup?.let { group ->

                bitratesList = group.streams.map { it.bitrate }

            }


            ChooseBitratesDialog(
                items = bitratesList,
                onItemSelected = { selectedItem ->
                    Log.d("Dialog", "Вибрано: $selectedItem")
                    viewModel.setBitrate(selectedItem)
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }


    }
}


@Composable
@Preview
fun MediumPlayerWidgetPreview() {

    MediumPlayerWidget(
        RadioViewModel(
            MockStationRepository(), SharedPreferencesRepositoryMock()
        )
    )
}
