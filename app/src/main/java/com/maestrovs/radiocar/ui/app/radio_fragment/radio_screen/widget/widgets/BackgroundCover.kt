package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.widgets

/**
 * Created by maestromaster$ on 13/02/2025$.
 */

import android.graphics.drawable.Drawable
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

@Composable
fun BackgroundCover(
    imageUrl: String,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    // Плавна анімація появи
    val alphaAnim by animateFloatAsState(
        targetValue = if (imageBitmap != null) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "fade-in"
    )

    // Завантажуємо зображення через Glide
    LaunchedEffect(imageUrl) {
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<android.graphics.Bitmap>() {
                override fun onResourceReady(resource: android.graphics.Bitmap, transition: Transition<in android.graphics.Bitmap>?) {
                    imageBitmap = resource
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    imageBitmap = null
                }
            })
    }

    // Малюємо фон, якщо картинка завантажена
    imageBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = modifier.alpha(alphaAnim),
            contentScale = ContentScale.Crop
        )
    }
}
