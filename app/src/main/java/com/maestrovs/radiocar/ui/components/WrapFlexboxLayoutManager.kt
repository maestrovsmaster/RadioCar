package com.maestrovs.radiocar.ui.components

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager

class WrapFlexboxLayoutManager(context: Context?) : FlexboxLayoutManager(context) {

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        }catch (e: Exception){
            Log.e("TAG", "meet a IOOBE in RecyclerView ${e.localizedMessage}");
        }
    }
}