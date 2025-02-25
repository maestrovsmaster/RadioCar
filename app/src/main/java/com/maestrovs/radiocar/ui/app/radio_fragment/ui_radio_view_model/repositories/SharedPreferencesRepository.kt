package com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.repositories

import android.content.Context
import com.maestrovs.radiocar.shared_managers.CurrentListTypeManager
import com.maestrovs.radiocar.ui.radio.ListType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Created by maestromaster$ on 25/02/2025$.
 */

abstract class SharedPreferencesRepository() {

    abstract fun setListType(listType: ListType)
    abstract fun getListType(): ListType
}

class SharedPreferencesRepositoryIml @Inject constructor(@ApplicationContext val context: Context) :
    SharedPreferencesRepository() {

    override fun setListType(listType: ListType) {
        val savedList =
            if (listType == ListType.All || listType == ListType.Searched) ListType.Recent else listType
        CurrentListTypeManager.setListType(context, savedList)
    }

    override fun getListType() =
        CurrentListTypeManager.readListType(context)

}

class SharedPreferencesRepositoryMock : SharedPreferencesRepository() {
    override fun setListType(listType: ListType) {
        TODO("Not yet implemented")
    }

    override fun getListType(): ListType {
        TODO("Not yet implemented")
    }


}