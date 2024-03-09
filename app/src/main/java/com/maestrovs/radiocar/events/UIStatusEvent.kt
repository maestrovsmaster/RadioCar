package com.maestrovs.radiocar.events

import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.ui.radio.ListType

enum class ActivityStatus {
    VISIBLE,
    INVISIBLE
}

data class UIStatusEvent(
    val activityStatus: ActivityStatus,
    val listType: ListType,
    val station: Station?
) : PlayEvent()