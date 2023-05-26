package com.maestrovs.radiocar.ui.components

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView

class AutoResizeMaterialCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val cornerRadius = (Math.min(w, h) / 2).toFloat()
        shapeAppearanceModel = shapeAppearanceModel.toBuilder()
            .setAllCornerSizes(cornerRadius)
            .build()
    }
}