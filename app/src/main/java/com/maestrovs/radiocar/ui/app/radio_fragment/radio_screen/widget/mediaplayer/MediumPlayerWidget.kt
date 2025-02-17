package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maestrovs.radiocar.data.repository.mock.FakeStationRepository
import com.maestrovs.radiocar.manager.audio_visual.AudioVisualizerStateManager
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget.BackgroundCover
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget.ControlBackground
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.AudioVisualizerManager
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.AudioVisualizerScreen
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
    val playerState by PlayerStateManager.playerState.collectAsStateWithLifecycle()

    // if (playerState.currentStation == null) return // Не показуємо, якщо станції нема


    Log.d("MiniPlayerWidget", "MiniPlayerWidget stationGroup = ${playerState.currentGroup}")

    Box(
        modifier = modifier
            //.width(250.dp)
            //.height(320.dp)
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

             playerState.currentGroup?.favicon?.let{ imgUrl ->
            Box(
                modifier = Modifier
                    //.fillMaxWidth()
                    //.background(Color.Green)
                    .padding(bottom = 40.dp, top = 0.dp, )
            ) {
                 BackgroundCover(imageUrl = imgUrl)
            }


             }

            ControlBackground()

            // Назва станції (внизу від кнопок)
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = playerState.currentGroup?.name ?: "Unknown Station",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = playerState.songMetadata ?: "Unknown Station",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Thin,
                    color = Color.White
                )
            }


            if (playerState.audioSessionId != null) {
                val visualizer = AudioVisualizerManager.getVisualizer(playerState.audioSessionId!!, step = 32)

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 50.dp)
                        .padding(16.dp)
                ) {
                    AudioVisualizerScreen()
                }
            }



            PlayControlWidget(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                isPlaying = playerState.isPlaying,
                isLoading = playerState.isBuffering || viewModel.isLoading.value == true,
                onPrevClick = {
                    viewModel.prev()
                },
                onPlayPauseClick = {
                    playerState.currentGroup?.let{ group ->
                        if (playerState.isPlaying) viewModel.stop() else viewModel.playGroup(
                            group
                        )
                    }

                },
                onNextClick = {
                    viewModel.next()
                })

        }


    }
}




@Composable
@Preview
fun MediumPlayerWidgetPreview() {

    MediumPlayerWidget(
        RadioViewModel(
            FakeStationRepository()
        )
    )
}
