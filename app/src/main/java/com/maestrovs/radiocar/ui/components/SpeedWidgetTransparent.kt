package com.maestrovs.radiocar.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.entities.weather.WeatherResponse


open class SpeedWidgetTransparent : FrameLayout {

    protected var mRootView: View? = null




    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {


        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context)
    }

    open val layoutID: Int = R.layout.component_speed_transparent


    lateinit var tvSpeed: TextView
    lateinit var tvUnit: TextView
    protected open fun init(context: Context) {

        foregroundGravity = Gravity.CENTER_VERTICAL

        val inflater = LayoutInflater.from(context)
        mRootView = inflater.inflate(layoutID, this)
        tvSpeed = mRootView!!.findViewById(R.id.tvSpeed)
        tvUnit = mRootView!!.findViewById(R.id.tvUnit)

        initUI()

    }


    internal fun initUI() {

    }


    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        mRootView?.visibility = visibility
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mRootView?.setOnClickListener {
            l?.onClick(it)
        }
    }

    fun setWeatherResponse(weatherResponse: WeatherResponse){
        refreshUI(weatherResponse)
    }



    private fun refreshUI(weatherResponse: WeatherResponse?, errorMessage: String? = null){



    }

    public fun setOnBack(l: OnClickListener?) {
        /* ivBackCard?.setOnClickListener {
             l?.onClick(it)
         }*/
    }


}


