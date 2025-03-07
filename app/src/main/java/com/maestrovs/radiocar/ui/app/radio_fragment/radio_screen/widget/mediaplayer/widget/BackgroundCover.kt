package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget

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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BackgroundCover(
    imageUrl: String,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val currentImageUrl by rememberUpdatedState(imageUrl)

    val alphaAnim by animateFloatAsState(
        targetValue = if (imageBitmap != null) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "fade-in"
    )

    LaunchedEffect(currentImageUrl) {
        imageBitmap = null
        Glide.with(context)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(true)
            .load(currentImageUrl)
            .into(object : CustomTarget<android.graphics.Bitmap>() {
                override fun onResourceReady(resource: android.graphics.Bitmap, transition: Transition<in android.graphics.Bitmap>?) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(10)
                        imageBitmap = resource
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    imageBitmap = null
                }
            })
    }

    imageBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = modifier.alpha(alphaAnim),
            contentScale = ContentScale.Crop
        )
    }
}
