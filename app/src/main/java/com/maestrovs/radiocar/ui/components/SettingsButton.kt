package com.maestrovs.radiocar.ui.components

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.databinding.ComponentSettingsButtonBinding

class SettingsButton (
    context: Context,
    attributeSet: AttributeSet
) : LinearLayout(context, attributeSet) {
    private var binding: ComponentSettingsButtonBinding =
        ComponentSettingsButtonBinding.inflate(LayoutInflater.from(context), this, false)

    init {
        addView(binding.root)
        val typedArray = context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.SettingsButton, 0, 0
        )

        binding.apply {
            val title = typedArray.getString(R.styleable.SettingsButton_title)
            val icon = typedArray.getResourceId(R.styleable.SettingsButton_card_icon,R.drawable.ic_arrow_right)
            try{
                arrowIcon.setImageDrawable(ContextCompat.getDrawable(context,icon))
            }catch (e: Exception){
                //Log.d("SettingsButton","SettingsButton error: $e")
            }
            tvTitle.text = title

            isClickable = true
            requestFocus();
            val outValue = TypedValue()
            getContext().theme.resolveAttribute(
                android.R.attr.selectableItemBackground,
                outValue,
                true
            )
            setBackgroundResource(outValue.resourceId)
        }
    }
    fun setTitle(text: String){
        binding.tvTitle.text = text
    }


}