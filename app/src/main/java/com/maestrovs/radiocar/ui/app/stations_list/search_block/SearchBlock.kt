package com.maestrovs.radiocar.ui.app.stations_list.search_block

/**
 * Created by maestromaster$ on 23/02/2025$.
 */

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arpitkatiyarprojects.countrypicker.models.CountryDetails
import com.maestrovs.radiocar.data.repository.mock.FakeStationRepository
import com.maestrovs.radiocar.ui.app.radio_fragment.widgets.DynamicShadowCard
import com.maestrovs.radiocar.ui.app.stations_list.RadioListViewModel
import com.maestrovs.radiocar.ui.app.stations_list.widgets.CountryPickerWidget
import com.maestrovs.radiocar.ui.app.stations_list.widgets.SearchBar
import com.maestrovs.radiocar.ui.app.stations_list.widgets.TagSelector
import com.maestrovs.radiocar.ui.app.ui.theme.primary


@Composable
fun SearchBlock(
    viewModel: RadioListViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    var latestCountry by remember { mutableStateOf<CountryDetails?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf<CountryDetails?>(null) }
    var selectedTag by remember { mutableStateOf(Pair("", "")) }

    val showCountryPicker = remember { mutableStateOf(false) }

    fun search() {
        viewModel.searchStations(
            searchQuery,
            selectedCountry?.countryCode ?: "",
            selectedTag.first
        )
    }



        DynamicShadowCard(
            modifier = modifier.fillMaxWidth(),
            contentColor = primary, backgroundColor = primary
        ) {

            Column(
                modifier = Modifier.height(160.dp).fillMaxWidth()
            ) {

                Spacer(modifier = Modifier.height(8.dp))
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
                    onBackClick = onBackClick
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CountryPickerWidget(
                        selectedCountry = selectedCountry,
                        onCountrySelected = {
                            selectedCountry = it
                            search()
                        },
                        onShowCountryPicker = {

                                showCountryPicker.value = it
                                selectedCountry = if (it) {
                                    latestCountry
                                } else {
                                    latestCountry = selectedCountry
                                    null
                                }
                                search()

                        },
                        modifier = Modifier.weight(6f),
                        showCountryPicker = showCountryPicker.value
                    )

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
                        modifier = Modifier.weight(4f).padding(end = 8.dp, bottom = 6.dp).height(40.dp)
                    )
                }
            }
        }
    }



@Preview
@Composable
fun SearchBlockPreview() {
    SearchBlock(
        RadioListViewModel(FakeStationRepository()),
        onBackClick = {}
    )
}