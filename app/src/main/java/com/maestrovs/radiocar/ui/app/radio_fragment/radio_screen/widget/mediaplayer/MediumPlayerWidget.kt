package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.PlayControlWidget
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

    val bitrateFlow by PlayerStateManager.preferredBitrateOptionFlow.collectAsStateWithLifecycle(null)
    val bitrate = bitrateFlow ?: BitrateOption.STANDARD

    var showDialog by remember { mutableStateOf(false) }


    Box(
        modifier = modifier
    ) {

        DynamicShadowCard(
            modifier = Modifier,
            // .padding(16.dp)
            // .fillMaxWidth(),
            contentColor = primary, backgroundColor = primary
        ) {

            currentGroup?.favicon?.let { imgUrl ->
                Box(
                    modifier = Modifier
                        //.fillMaxWidth()
                        //.background(Color.Green)
                        .padding(bottom = 40.dp, top = 0.dp)
                ) {
                    BackgroundCover(imageUrl = imgUrl)
                }


            }

            ControlBackground()


            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = currentGroup?.name ?: "Choose station",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = songMetadataFlow ?: "",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Thin,
                    color = Color.White
                )
            }
            currentGroup?.let {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    LikeWidget(isLiked = isLikedFlow, onLikeClick =  {
                        viewModel.setIsLike(currentGroup!!, !isLikedFlow)
                    })

                    BitrateWidget(bitrate = bitrate, onClick = {
                        showDialog = true
                    })

                }

            }


            if (audioSessionIdFlow != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 50.dp)
                        .padding(16.dp)
                ) {
                    AudioVisualizerScreen()
                }
            }


            val isPlaying = if(isPlayingFlow == null) false else isPlayingFlow!!.first

            PlayControlWidget(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                isPlaying = isPlaying,
                isLoading = isBufferingFlow || viewModel.isLoading.value == true,
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

        if (showDialog) {

            var bitratesList = listOf<BitrateOption>()

            currentGroup?.let { group ->

                bitratesList = group.streams.map { it.bitrate  }

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
            MockStationRepository(),  SharedPreferencesRepositoryMock()
        )
    )
}
