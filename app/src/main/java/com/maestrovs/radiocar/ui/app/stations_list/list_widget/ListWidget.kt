package com.maestrovs.radiocar.ui.app.stations_list.list_widget

/**
 * Created by maestromaster$ on 23/02/2025$.
 */

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.maestrovs.radiocar.data.repository.mock.MockStationRepository
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.repositories.SharedPreferencesRepositoryMock
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.DynamicShadowCard
import com.maestrovs.radiocar.ui.app.stations_list.RadioListViewModel
import com.maestrovs.radiocar.ui.app.stations_list.widgets.StationItem
import com.maestrovs.radiocar.ui.app.ui.theme.primary
import com.maestrovs.radiocar.ui.app.ui.theme.primaryDiv


@Composable
fun ListWidget(viewModel: RadioListViewModel,  modifier: Modifier = Modifier){

    val stationList = viewModel.stationFlow.collectAsLazyPagingItems()

    Box(
        modifier = modifier
    ) {

        DynamicShadowCard(
            modifier = Modifier.fillMaxSize(),
            contentColor = primary,
        ) {
            LazyColumn(modifier = modifier) {
                items(stationList.itemCount) { index ->
                    val stationGroup = stationList[index]
                    stationGroup?.let {
                        Column {
                            StationItem(station = it,
                                onItemClick = { item ->
                                    //TODO: Play station

                                },
                                onLikeClick = { isLiked ->
                                    viewModel.setIsLike(it, isLiked)
                                    stationList.refresh()
                                },
                                onPlayClick = { item ->
                                    viewModel.playGroup(item)
                                },
                                onPausedClick = { item ->
                                    viewModel.stop()
                                }
                            )
                            Divider(
                                color = primaryDiv,
                                thickness = 1.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 64.dp, top = 3.dp, bottom = 3.dp)
                            )
                        }
                    }
                }

                stationList.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item { CircularProgressIndicator(modifier = Modifier) }
                        }
                        loadState.append is LoadState.Loading -> {
                            item { CircularProgressIndicator(modifier = Modifier) }
                        }
                    }
                }
            }
        }
    }

}


@Preview
@Composable
fun ListWidgetPreview(){

    ListWidget(
        RadioListViewModel(MockStationRepository(),   SharedPreferencesRepositoryMock()),
        )

}