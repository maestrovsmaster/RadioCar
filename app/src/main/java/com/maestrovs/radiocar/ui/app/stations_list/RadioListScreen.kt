package com.maestrovs.radiocar.ui.app.stations_list

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.ui.app.stations_list.widgets.CountryFilterDropdown
import com.maestrovs.radiocar.ui.app.stations_list.widgets.StationItem
import com.maestrovs.radiocar.ui.app.stations_list.widgets.TagSelector

/**
 * Created by maestromaster$ on 19/02/2025$.
 */

@Composable
fun RadioListScreen(viewModel: RadioListViewModel, navController: NavController) {
    val stationList = viewModel.stationFlow.collectAsLazyPagingItems()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf("UA") }
    var selectedTag by remember { mutableStateOf(Pair("", "")) }


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        fun serch() {
            viewModel.searchStations(
                searchQuery,
                selectedCountry,
                selectedTag.first
            )
        }

        // Поле пошуку
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Введіть назву станції") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Вибір країни (поки без реалізації)
        OutlinedButton(
            onClick = { /* Реалізуємо пізніше */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedCountry)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Вибір тегу
        TagSelector(
            selectedTag = selectedTag.second,
            onTagSelected = {

                selectedTag = it
                serch()
            },
            onTagCleared = {
                selectedTag = Pair("", "")
                serch()
            }
        )


        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка пошуку
        /* Button(
             onClick = { viewModel.searchStations(searchQuery, selectedCountry, selectedTag.ifEmpty { customTag }) },
             modifier = Modifier.fillMaxWidth()
         ) {
             Text(text = "Пошук")
         }*/

        Spacer(modifier = Modifier.height(16.dp))

        // Список радіостанцій
        LazyColumn {
            items(stationList.itemCount) { index ->
                val stationGroup = stationList[index]
                stationGroup?.let {
                    StationItem(station = it)
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
