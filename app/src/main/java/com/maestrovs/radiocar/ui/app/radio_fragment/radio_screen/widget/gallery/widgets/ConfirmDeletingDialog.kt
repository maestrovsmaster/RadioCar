package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.gallery.widgets

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.maestrovs.radiocar.R

/**
 * Created by maestromaster$ on 26/02/2025$.
 */

@Composable
fun ConfirmDeleteDialog(
    stationName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_station_title)) },
        text = { Text(stringResource(R.string.delete_station_message, stationName)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
