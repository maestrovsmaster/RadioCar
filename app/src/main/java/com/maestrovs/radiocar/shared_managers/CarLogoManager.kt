package com.maestrovs.radiocar.shared_managers

import android.content.Context

object CarLogoManager {
    private const val CAR_LOGO_RES_ID = "car_logo_res_id"


    fun saveCarResId(context: Context, resId: Int) {
        SharedManager.writeIntOption(context, CAR_LOGO_RES_ID, resId)
    }

    fun removeCarResId(context: Context) {
        SharedManager.writeIntOption(context, CAR_LOGO_RES_ID, -1)
    }

    fun readLogoResId(context: Context): Int? {
        val resId = SharedManager.readIntOptions(context, CAR_LOGO_RES_ID,-1)

        return if (resId == -1) {
            null
        } else {
            resId
        }
    }

}