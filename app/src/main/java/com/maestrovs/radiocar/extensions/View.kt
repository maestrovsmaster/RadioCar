package com.maestrovs.radiocar.extensions

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.Toast

fun View.addRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
    setBackgroundResource(resourceId)
}

fun View.addCircleRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, this, true)
    setBackgroundResource(resourceId)
}

fun View.setVisible(isVisible:Boolean){
    this.visibility = if(isVisible){
        View.VISIBLE
    }else{
        View.GONE
    }
}

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()