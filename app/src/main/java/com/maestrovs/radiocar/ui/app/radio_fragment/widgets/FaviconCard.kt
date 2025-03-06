package com.maestrovs.radiocar.ui.app.radio_fragment.widgets

import android.graphics.drawable.Drawable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.mediaplayer.widget.BackgroundCover
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by maestromaster$ on 05/03/2025$.
 */

@Composable
fun FaviconCard(imgURL: String?, modifier: Modifier = Modifier.size(36.dp)){

    if (imgURL == null){
        Box(
            modifier = modifier
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(8.dp),
                )
                .background(
                    shape = RoundedCornerShape(8.dp),
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF3A4A57),
                            Color(0xFF1E2A34)
                        ),
                    )
                )

        ) {}
        return
    }

    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val currentImageUrl by rememberUpdatedState(imgURL)

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

    Box(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(8.dp),
            )
            .background(
                shape = RoundedCornerShape(8.dp),
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF3A4A57),
                        Color(0xFF1E2A34)
                    ),
                )
            )

    ) {
        imageBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = modifier.alpha(alphaAnim),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Preview
@Composable
fun FaviconCardPreview(){
    FaviconCard(null)
}