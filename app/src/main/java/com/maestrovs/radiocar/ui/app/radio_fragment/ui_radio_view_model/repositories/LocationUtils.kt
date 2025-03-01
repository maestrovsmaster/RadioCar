package com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.repositories

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Reader

/**
 * Created by maestromaster$ on 28/02/2025$.
 */

data class LocationResponse(val country: String?)

// Custom deserializer
class LocationAPIDeserializer : ResponseDeserializable<LocationResponse> {
    override fun deserialize(reader: Reader): LocationResponse? =
        Gson().fromJson(reader, LocationResponse::class.java)
}