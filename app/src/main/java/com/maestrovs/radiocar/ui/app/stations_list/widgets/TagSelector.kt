package com.maestrovs.radiocar.ui.app.stations_list.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.ui.app.ui.theme.primary


/**
 * Created by maestromaster$ on 20/02/2025$.
 */
@Composable
fun TagSelector(
    selectedTag: String,
    onTagSelected: (Pair<String, String>) -> Unit,
    onTagCleared: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tagMap = getTagMap()
    val tagKeys = tagMap.keys.toList()
    val tagValues = tagMap.values.toList()

    var expanded by remember { mutableStateOf(false) }
    var customTag by remember { mutableStateOf("") }

    Box(modifier = modifier) {
        if (selectedTag.isNullOrEmpty()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = LocalContext.current.getString(R.string.select_tag), color = Color.White)
            }
        } else {

            Button(
                onClick = { onTagCleared() },
                colors = ButtonDefaults.buttonColors(containerColor = primary),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedTag,
                    color = Color.White,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear tag",
                    tint = Color.White
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(),

        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { expanded = false }) {
                    Icon(imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black)
                }
                OutlinedTextField(
                    value = customTag,
                    onValueChange = { customTag = it },
                    label = { Text(LocalContext.current.getString(R.string.enter_custom_tag)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (customTag.isNotEmpty()) {
                            onTagSelected(Pair(customTag, customTag))
                            expanded = false
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }


            HorizontalDivider()


            tagValues.forEach { tagValue ->
                DropdownMenuItem(
                    text = { Text(tagValue) },
                    onClick = {
                        val tagKey = tagMap.entries.find { it.value == tagValue }?.key ?: ""
                        onTagSelected(Pair(tagKey, tagValue))
                        expanded = false
                    }
                )
            }
        }
    }



}

@Composable
fun getTagMap(): Map<String, String> {
    val context = LocalContext.current
    val keys = context.resources.getStringArray(R.array.tag_keys)
    val values = context.resources.getStringArray(R.array.tag_values)

    return keys.zip(values).toMap()
}