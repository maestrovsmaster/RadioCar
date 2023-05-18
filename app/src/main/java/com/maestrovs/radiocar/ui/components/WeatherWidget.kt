package com.maestrovs.radiocar.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.entities.weather.WeatherResponse
import com.squareup.picasso.Picasso


open class WeatherWidget : FrameLayout {

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

    open val layoutID: Int = R.layout.component_weather

    lateinit var ivWeatherIcon: ImageView
    lateinit var tvCity: TextView
    lateinit var tvTemperature: TextView
    protected open fun init(context: Context) {

        foregroundGravity = Gravity.CENTER_VERTICAL

        val inflater = LayoutInflater.from(context)
        mRootView = inflater.inflate(layoutID, this)
        tvCity = mRootView!!.findViewById(R.id.tvCity)
        tvTemperature = mRootView!!.findViewById(R.id.tvTemperature)
        ivWeatherIcon = mRootView!!.findViewById(R.id.ivWeatherIcon)

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

    private fun refreshUI(weatherResponse: WeatherResponse){
        tvCity.text = weatherResponse.name

        val temperatureFloat = weatherResponse.main.temp

        var textColorRes = R.color.black

        var tempPref = ""
        if(temperatureFloat == 0.0f){
            tempPref = ""
            textColorRes = R.color.blue_cold
        }else
        if(temperatureFloat>0.0f){
            tempPref = "+"
        }else if(temperatureFloat <0){
            tempPref = "-"
            textColorRes
        }




        val tempStr = temperatureFloat.toInt().toString()

        val displayTemp = "$tempPref $tempStr °C"


        tvTemperature.text = displayTemp

       // tvTemperature.setTextColor(textColorRes)

        val weatherList = weatherResponse.weather

        if(weatherList.isNotEmpty()){
            val weather = weatherList[0]
            val icon = weather.icon

            val iconUrl = "https://openweathermap.org/img/wn/${icon}@2x.png"
            Picasso.get()
                .load(iconUrl)
                .resize(120, 120)
                .centerCrop()
                .into(ivWeatherIcon)
        }


    }

    public fun setOnBack(l: OnClickListener?) {
        /* ivBackCard?.setOnClickListener {
             l?.onClick(it)
         }*/
    }


}


