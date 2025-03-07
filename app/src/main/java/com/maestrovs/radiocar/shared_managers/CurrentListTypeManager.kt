package com.maestrovs.radiocar.shared_managers

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.hbb20.countrypicker.models.CPCountry
import com.maestrovs.radiocar.ui.radio.ListType

object CurrentListTypeManager {
    private const val CURRENT_LIST_TYPE_KEY = "CURRENT_LIST_TYPE_KEY"



    fun setListType(context: Context, listType: ListType)  {
        SharedManager.writeStringOption(context, CURRENT_LIST_TYPE_KEY, listType.name)
    }

    fun readListType(context: Context): ListType {

        val listTypeStr = SharedManager.readStringOptions(context, CURRENT_LIST_TYPE_KEY)
        return if (listTypeStr == null) {
            ListType.All
        } else {
            ListType.valueOf(listTypeStr)
        }
    }


}