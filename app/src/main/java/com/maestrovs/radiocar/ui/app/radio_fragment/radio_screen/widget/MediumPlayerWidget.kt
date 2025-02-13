package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.repository.mock.FakeStationRepository
import com.maestrovs.radiocar.manager.PlayerStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.RadioScreen
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.AudioVisualizerScreen
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.DynamicShadowCard
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.PlayControlWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.Progress
import com.maestrovs.radiocar.ui.app.ui.theme.primary

/**
 * Created by maestromaster on 11/02/2025$.
 */

@Composable
fun MediumPlayerWidget(
    viewModel: RadioViewModel
) {
    val playerState by PlayerStateManager.playerState.collectAsStateWithLifecycle()

   // if (playerState.currentStation == null) return // Не показуємо, якщо станції нема


    Log.d("MiniPlayerWidget","MiniPlayerWidget stationGroup = ${playerState.currentGroup}")

    Box(
        modifier = Modifier
            .width(250.dp)
            .height(320.dp)
            .background(Color.Black)
            .padding(16.dp),
        //contentAlignment = Alignment.Center
    ) {

        DynamicShadowCard(modifier = Modifier
           // .padding(16.dp)
            .fillMaxSize(), contentColor = Color.White, backgroundColor = primary ) {




            // Назва станції (внизу від кнопок)
            Column(
                modifier = Modifier.align(Alignment.TopStart),
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

            if(playerState.audioSessionId != null){
                Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 66.dp)
                ) {
                    AudioVisualizerScreen(audioSessionId = playerState.audioSessionId!!)
                }
            }


            Box(modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                PlayControlWidget(
                    isPlaying = playerState.isPlaying,
                    isLoading = playerState.isBuffering,
                    onPrevClick = {
                        viewModel.prev()
                    },
                    onPlayPauseClick = {
                        if (playerState.isPlaying) viewModel.stop() else viewModel.playGroup(
                            playerState.currentGroup!!
                        )
                    },
                    onNextClick = {
                        viewModel.next()
                    })
            }
        }



    }
}

@Composable
@Preview
fun MediumPlayerWidgetPreview(){

    MediumPlayerWidget(
        RadioViewModel(
            FakeStationRepository()
        )
    )
}
