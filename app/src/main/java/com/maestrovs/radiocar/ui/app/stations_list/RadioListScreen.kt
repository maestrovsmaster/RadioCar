package com.maestrovs.radiocar.ui.app.stations_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.arpitkatiyarprojects.countrypicker.models.CountryDetails
import com.maestrovs.radiocar.ui.app.stations_list.widgets.CountryPickerWidget
import com.maestrovs.radiocar.ui.app.stations_list.widgets.SearchBar
import com.maestrovs.radiocar.ui.app.stations_list.widgets.StationItem
import com.maestrovs.radiocar.ui.app.stations_list.widgets.TagSelector

/**
 * Created by maestromaster$ on 19/02/2025$.
 */

@Composable
fun RadioListScreen(viewModel: RadioListViewModel, navController: NavController) {
    val stationList = viewModel.stationFlow.collectAsLazyPagingItems()

    var latestCountry by remember { mutableStateOf<CountryDetails?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf<CountryDetails?>(null) }
    var selectedTag by remember { mutableStateOf(Pair("", "")) }

    val showCountryPicker = remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        fun search() {
            viewModel.searchStations(
                searchQuery,
                selectedCountry?.countryCode ?: "",
                selectedTag.first
            )
        }

        SearchBar(
            searchQuery = searchQuery,
            onSearch = {
                searchQuery = it
                search()
            },
            onClear = {
                searchQuery = ""
                search()
            },
            onBackClick = { navController.popBackStack() }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            CountryPickerWidget(selectedCountry = selectedCountry, onCountrySelected = {
                selectedCountry = it
                search()
            }, onShowCountryPicker = {

                showCountryPicker.value = it
                selectedCountry = if (it) {
                    latestCountry
                } else {
                    latestCountry = selectedCountry
                    null
                }
                search()

            }, modifier = Modifier.weight(6f), showCountryPicker = showCountryPicker.value)

            TagSelector(
                selectedTag = selectedTag.second,
                onTagSelected = {

                    selectedTag = it
                    search()
                },
                onTagCleared = {
                    selectedTag = Pair("", "")
                    search()
                },
                modifier = Modifier.weight(4f)
            )

        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {
            items(stationList.itemCount) { index ->
                val stationGroup = stationList[index]
                stationGroup?.let {
                    StationItem(station = it,
                        onItemClick = { item ->
                            //TODO: Play station

                        },
                        onLikeClick = { isLiked ->
                          //  viewModel.setIsLike(it, isLiked)
                        },
                       onPlayClick = { item ->
                           viewModel.playGroup(item)
                       },
                        onPausedClick = { item ->
                            viewModel.stop()
                        }
                    )
                }
            }

            stationList.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item { CircularProgressIndicator() }
                    }

                    loadState.append is LoadState.Loading -> {
                        item { CircularProgressIndicator() }
                    }
                }
            }
        }
    }


}
