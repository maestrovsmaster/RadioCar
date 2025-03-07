package com.maestrovs.radiocar.utils

/**
 * Created by maestromaster$ on 12/02/2025$.
 */

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

fun getBitmapFromDrawable(context: Context, drawableId: Int): Bitmap? {
    val drawable: Drawable = ContextCompat.getDrawable(context, drawableId) ?: return null

    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

