package com.maestrovs.radiocar.ui.components

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.databinding.ComponentCarButtonBinding

class CarButton (
    context: Context,
    attributeSet: AttributeSet
) : LinearLayout(context, attributeSet) {
    private var binding: ComponentCarButtonBinding =
        ComponentCarButtonBinding.inflate(LayoutInflater.from(context), this, false)

    init {
        addView(binding.root)
        val typedArray = context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.CarButton, 0, 0
        )

        binding.apply {

            val icon = typedArray.getResourceId(R.styleable.CarButton_icon,R.drawable.ic_settings)
            try{
                icImage.setImageDrawable(ContextCompat.getDrawable(context,icon))
            }catch (_: Exception){}

            val background = typedArray.getResourceId(R.styleable.CarButton_background_tint,R.drawable.ripple_gray_round)
            try{
                icImage.setBackgroundResource(background)
            }catch (_: Exception){}

            val strokeColor = typedArray.getResourceId(R.styleable.CarButton_stroke_color,R.color.white)
            try{
                cardView.strokeColor = ContextCompat.getColor(context, strokeColor)
            }catch (_: Exception){}

            val strokeWidth = typedArray.getLayoutDimension(R.styleable.CarButton_stroke_width,0)
            try{
                cardView.strokeWidth = strokeWidth
            }catch (_: Exception){}


            isClickable = true
            requestFocus()
            val outValue = TypedValue()
            getContext().theme.resolveAttribute(
                android.R.attr.selectableItemBackground,
                outValue,
                true
            )
            setBackgroundResource(outValue.resourceId)
        }
    }


    public fun setIconResource(icon: Int){
        try{
            binding.icImage.setImageDrawable(ContextCompat.getDrawable(context,icon))
        }catch (_: Exception){}
    }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.icImage.setOnClickListener {
            l?.onClick(it)
        }
    }

}