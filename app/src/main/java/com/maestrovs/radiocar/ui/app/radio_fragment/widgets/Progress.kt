package com.maestrovs.radiocar.ui.app.radio_fragment.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Created by maestromaster on 12/02/2025.
 */

@Composable
fun Progress() {
    CircularProgressIndicator(
        modifier = Modifier.size(24.dp), // Розмір
        color = MaterialTheme.colorScheme.primary,
        strokeWidth = 3.dp // Товщина кола
    )
}
