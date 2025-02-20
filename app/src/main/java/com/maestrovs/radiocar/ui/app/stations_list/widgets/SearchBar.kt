package com.maestrovs.radiocar.ui.app.stations_list.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maestrovs.radiocar.R

/**
 * Created by maestromaster$ on 20/02/2025$.
 */

@Composable
fun SearchBar(
    searchQuery: String,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = { onBackClick() }) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
        }


        TextField(
            value = searchQuery,
            onValueChange = { onSearch(it) },
            placeholder = { Text(LocalContext.current.getString(R.string.enter_station_name)) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            /*colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )*/
        )


        IconButton(
            onClick = {
                if (searchQuery.isNotEmpty()) {
                    onClear() // Очищення пошуку
                } else {
                    onSearch(searchQuery) // Виконати пошук
                }
            }
        ) {
            Icon(
                imageVector = if (searchQuery.isNotEmpty()) Icons.Default.Close else Icons.Default.Search,
                contentDescription = "Search/Clear"
            )
        }
    }
}

@Preview
@Composable
fun SearchBarPreview(){
    SearchBar(onBackClick = {}, searchQuery = "", onSearch = {}, onClear = {})
}
