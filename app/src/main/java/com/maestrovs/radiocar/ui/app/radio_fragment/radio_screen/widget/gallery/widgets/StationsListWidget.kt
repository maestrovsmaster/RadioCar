package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.maestrovs.radiocar.data.repository.mock.FakeStationRepository
import com.maestrovs.radiocar.manager.PlayerStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.widgets.BackgroundCover
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.DynamicShadowCard
import com.maestrovs.radiocar.ui.app.ui.theme.primary
import com.maestrovs.radiocar.ui.app.ui.theme.primaryDark

/**
 * Created by maestromaster$ on 13/02/2025$.
 */

@Composable
fun StationsListWidget(
    viewModel: RadioViewModel
    ) {

    val playerState by PlayerStateManager.playerState.collectAsStateWithLifecycle()
    Box(
        modifier = Modifier.fillMaxWidth()
            //.width(250.dp)
            .height(150.dp)
            .background(Color.Black)
            .padding(16.dp)
            ,
        //contentAlignment = Alignment.Center
    ) {

        DynamicShadowCard(
            modifier = Modifier
                // .padding(16.dp)
                .fillMaxSize(), contentColor = primary, backgroundColor = primaryDark
        ) {



            CenteredCarousel(items = playerState.stationGroups, modifier = Modifier.fillMaxWidth(), onItemClick = {
                viewModel.playGroup(it)

            })

        

            Box(
                modifier = Modifier.fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0x9221292F),
                                Color(0x0327272A),
                                Color(0x90182325)
                            ),
                            //startY = 0f,
                           // endY = 800f
                        )
                    )
            )






        }


    }
}

@Composable
@Preview
fun StationsListWidgetPreview() {

    StationsListWidget(RadioViewModel(
        FakeStationRepository()
    ))

}
