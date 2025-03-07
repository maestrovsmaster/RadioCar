package com.maestrovs.radiocar.ui.app.radio_fragment.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

/**
 * Created by maestromaster$ on 14/02/2025$.
 */
//car_infinite_road.json
//night_radio.json
@Composable
fun LottieLoader(speed: Float = 0.2f) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("car_infinite_road.json"))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = speed
    )

    LottieAnimation(
        composition = composition,
        progress = { progress }
    )
}

@Preview
@Composable
fun LottiePreview() {
    LottieLoader()
}
