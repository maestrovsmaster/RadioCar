package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maestrovs.radiocar.data.entities.radio.BitrateOption

/**
 * Created by maestromaster$ on 26/02/2025$.
 */

@Composable
fun ChooseBitratesDialog(
    items: List<BitrateOption>,
    onItemSelected: (BitrateOption) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
       // title = { Text(text = "Choose bitrate") },
        text = {
            Column {
                items.forEach { item ->
                    Text(
                        text = item.displayName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onItemSelected(item)
                                onDismiss()
                            }
                            .padding(16.dp)
                    )
                }
            }
        },
        confirmButton = {} // Без кнопки "ОК"
    )
}
