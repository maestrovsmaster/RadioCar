package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maestrovs.radiocar.data.repository.mock.MockStationRepository
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets.ListTypeSelector
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.DynamicShadowCard
import com.maestrovs.radiocar.ui.app.ui.theme.primary
import com.maestrovs.radiocar.ui.app.ui.theme.primaryDark
import com.maestrovs.radiocar.ui.radio.ListType
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.manager.radio.PlaylistManager
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets.ConfirmDeleteDialog
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets.ExtendSearchButtonWidget
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets.gallery.CenteredCarousel2
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.repositories.SharedPreferencesRepositoryMock


/**
 * Created by maestromaster$ on 13/02/2025$.
 */

@Composable
fun StationsListWidget(
    viewModel: RadioViewModel,
    modifier: Modifier = Modifier,
    onSelectAllClick: () -> Unit = {},
    navController: NavController
) {

    val currentListType by viewModel.currentListType.collectAsState()
    val stationsGroupFlow by PlaylistManager.stationGroups.collectAsState(emptyList())
    Log.d("StationRepositoryIml", "StationsListWidget: $stationsGroupFlow")
    val navBackStackEntry = rememberUpdatedState(navController.currentBackStackEntry)

    LaunchedEffect(navBackStackEntry) {
        viewModel.fetchStations()
    }

    var showDialog by remember { mutableStateOf(false) }
    var selectedStation by remember { mutableStateOf<StationGroup?>(null) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            //.height(180.dp),
    ) {

        DynamicShadowCard(
            modifier = Modifier
                // .padding(16.dp)
                .fillMaxSize(), contentColor = primary,
        ) {


            CenteredCarousel2(
                items = stationsGroupFlow,
                modifier = Modifier.fillMaxWidth(),
                onItemClick = {
                    viewModel.playGroup(it)

                },
                onItemLongClick = { selectedStation = it
                    showDialog = true

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

           /* ExtendSearchButtonWidget(modifier = Modifier.align(Alignment.CenterEnd),
                onClick = { }
            )*/


        }



    }
    if (showDialog && selectedStation != null) {
        ConfirmDeleteDialog(
            stationName = selectedStation!!.name,
            onConfirm = {
                viewModel.deleteFromRecentAndFavorites(selectedStation!!)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
@Preview
fun StationsListWidgetPreview() {

    StationsListWidget(
        RadioViewModel(
            MockStationRepository(),
            SharedPreferencesRepositoryMock()
        ),
        modifier = Modifier,
        navController = NavController(LocalContext.current)
    )

}
