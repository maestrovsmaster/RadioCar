package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maestrovs.radiocar.data.repository.mock.FakeStationRepository
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets.CenteredCarousel
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets.ListTypeSelector
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.DynamicShadowCard
import com.maestrovs.radiocar.ui.app.ui.theme.primary
import com.maestrovs.radiocar.ui.app.ui.theme.primaryDark
import com.maestrovs.radiocar.ui.radio.ListType
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import com.maestrovs.radiocar.manager.radio.PlaylistManager
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets.ExtendSearchButtonWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.repositories.SharedPreferencesRepositoryMock


/**
 * Created by maestromaster$ on 13/02/2025$.
 */

@Composable
fun StationsListWidget(
    viewModel: RadioViewModel,
    modifier: Modifier = Modifier,
    onSelectAllClick: () -> Unit = {},
) {

    val currentListType by viewModel.currentListType.collectAsState()
    val stationsGroupFlow by PlaylistManager.stationGroups.collectAsState(emptyList())

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp),
    ) {

        DynamicShadowCard(
            modifier = Modifier
                // .padding(16.dp)
                .fillMaxSize(), contentColor = primary, backgroundColor = primaryDark
        ) {


            CenteredCarousel(
                items = stationsGroupFlow,
                modifier = Modifier.fillMaxWidth(),
                onItemClick = {
                    viewModel.playGroup(it)

                })


            ListTypeSelector(
                currentListType = currentListType,
                onChangeType = {
                    viewModel.setListType(it)


                    if(it == ListType.All){
                        onSelectAllClick()
                    }
                }
            )

            ExtendSearchButtonWidget(modifier = Modifier.align(Alignment.CenterEnd),
                onClick = { }
            )


        }


    }
}

@Composable
@Preview
fun StationsListWidgetPreview() {

    StationsListWidget(
        RadioViewModel(
            FakeStationRepository(),
            SharedPreferencesRepositoryMock()
        ),
        modifier = Modifier,
    )

}
