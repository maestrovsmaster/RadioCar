package com.maestrovs.radiocar.ui.app.radio_fragment.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Created by maestromaster$ on 13/02/2025$.
 */

@Composable
fun DynamicShadowCard(modifier: Modifier = Modifier,
    contentColor: Color,
                      backgroundColor: Color = Color.White,
                      content: @Composable () -> Unit) {
    val shadowColor = contentColor.copy(alpha = 1.0f)

    Box(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .background(backgroundColor, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        content()
    }
}

@Preview
@Composable
fun DynamicShadowExample() {
    Column {
        DynamicShadowCard(contentColor = Color.Red) {
            Text("Red Content", color = Color.Red, modifier = Modifier)
        }
        DynamicShadowCard(contentColor = Color.Blue) {
            Text("Blue Content", color = Color.Blue, modifier = Modifier)
        }
    }
}
