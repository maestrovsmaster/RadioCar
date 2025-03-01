package com.maestrovs.radiocar.ui.app.stations_list.search_block

/**
 * Created by maestromaster$ on 23/02/2025$.
 */

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arpitkatiyarprojects.countrypicker.models.CountryDetails
import com.maestrovs.radiocar.data.repository.mock.MockStationRepository
import com.maestrovs.radiocar.manager.location.LocationStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget.ControlBackgroundLight
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.repositories.SharedPreferencesRepositoryMock
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
    modifier: Modifier = Modifier,
    currentCountryCode: String? = null
) {

   // val currentCountryCode by LocationStateManager.currentCountryCode.collectAsStateWithLifecycle()
   // var useCountryForSearch by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCountryForSearch by remember { mutableStateOf<String?>(currentCountryCode) }

    var selectedTag by remember { mutableStateOf(Pair("", "")) }

    val showCountryPicker = remember { mutableStateOf(true) }

    fun search() {
        viewModel.searchStations(
            searchQuery,
            if(showCountryPicker.value) selectedCountryForSearch ?: "" else "",
            selectedTag.first
        )
    }



    DynamicShadowCard(
        modifier = modifier.fillMaxWidth(),
        contentColor = primary,
    ) {


        Column(
            modifier = Modifier
                .height(160.dp)
                .fillMaxWidth()
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
                    defaultCountryCode = currentCountryCode,
                    onCountrySelected = {
                        selectedCountryForSearch = it.countryCode
                        viewModel.updateCurrentCountry(it.countryCode)
                        search()


                    },
                    onShowCountryPicker = {

                        showCountryPicker.value = it

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
                    modifier = Modifier
                        .weight(4f)
                        .padding(end = 8.dp, bottom = 6.dp)
                        .height(40.dp)
                )
            }
        }
    }
}


@Preview
@Composable
fun SearchBlockPreview() {
    SearchBlock(
        RadioListViewModel(MockStationRepository(), SharedPreferencesRepositoryMock()),
        onBackClick = {}
    )
}