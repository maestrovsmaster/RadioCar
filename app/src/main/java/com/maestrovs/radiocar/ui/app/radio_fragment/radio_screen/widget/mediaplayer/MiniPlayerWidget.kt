package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.repository.mock.FakeStationRepository
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.Progress

/**
 * Created by maestromaster on 11/02/2025$.
 */

@Composable
fun MiniPlayerWidget(
    viewModel: RadioViewModel
) {
    val playerState by PlayerStateManager.playerState.collectAsStateWithLifecycle()

   // if (playerState.currentStation == null) return // Не показуємо, якщо станції нема


    Log.d("MiniPlayerWidget","MiniPlayerWidget stationGroup = ${playerState.currentGroup}")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Кнопка "Prev"
            IconButton(onClick = { viewModel.prev() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Кнопка "Play/Pause"

            if(playerState.isBuffering){
                Progress()
            }else {
                IconButton(
                    onClick = {
                        if (playerState.isPlaying) viewModel.stop() else viewModel.playGroup(
                            playerState.currentGroup!!
                        )
                    }
                ) {


                    Icon(
                        painter = painterResource(id =  if (playerState.isPlaying) R.drawable.ic_pause
                            else R.drawable.ic_play_xml
                        ),
                        contentDescription = "Play",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary // Якщо потрібно змінити колір
                    )
                }
            }

            // Кнопка "Next"
            IconButton(onClick = { viewModel.next() }) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Назва станції (внизу від кнопок)
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = playerState.currentGroup?.name ?: "Unknown Station",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
@Preview
fun MiniPlayerWidgetPreview(){

    MiniPlayerWidget(
        RadioViewModel(
            FakeStationRepository()
        )
    )
}
