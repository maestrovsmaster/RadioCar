package com.maestrovs.radiocar.ui.app.pip_screen

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maestrovs.radiocar.data.entities.radio.BitrateOption
import com.maestrovs.radiocar.data.repository.mock.MockStationRepository
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget.BackgroundCover
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.repositories.SharedPreferencesRepositoryMock
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.AudioVisualizerScreenTiny
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.MarqueeText

/**
 * Created by maestromaster$ on 04/03/2025$.
 */

@Composable
fun PiPScreen(viewModel: RadioViewModel, modifier: Modifier = Modifier) {

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




    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF3A4A57),
                        Color(0xFF1E2A34)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter)
                .padding(top = 6.dp, start = 8.dp, end = 8.dp, bottom = 0.dp),
            horizontalAlignment = Alignment.Start
        ) {


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {


                Text(
                    text = "Radio Car",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFB0DCF5)
                )

            }
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
                //
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(4.dp),
                        )
                        .background(
                            shape = RoundedCornerShape(4.dp),
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF3A4A57),
                                    Color(0xFF1E2A34)
                                ),
                            )
                        )

                ) {
                    currentGroup?.favicon?.let { imgUrl ->
                        Box(
                            modifier = Modifier

                        ) {
                            BackgroundCover(imageUrl = imgUrl)
                        }


                    }
                }

                Spacer(Modifier.width(8.dp))

                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = currentGroup?.name ?: "",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                    MarqueeText(
                        text = "\uD83C\uDFB5 ${songMetadataFlow ?: ""}",
                        color = Color(0xFFECEEF1),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        gradientEdgeColor = Color(0xFF2F383F).copy(alpha = 0.9f),
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            if (audioSessionIdFlow != null) {
                AudioVisualizerScreenTiny(modifier = Modifier.alpha(0.5f))
            }



        }

    }
}


@Preview
@Composable
fun PiPScreenPreview() {
    PiPScreen(
        RadioViewModel(
            MockStationRepository(
            ), SharedPreferencesRepositoryMock()
        ),
        modifier = Modifier
            .width(160.dp)
            .height(90.dp)
    )
}