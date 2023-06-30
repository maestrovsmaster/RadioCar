package com.maestrovs.radiocar.ui.components

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.entities.weather.WeatherResponse
import com.maestrovs.radiocar.extensions.setVisible
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.component_weather.view.layError
import kotlinx.android.synthetic.main.component_weather.view.layWeather
import kotlinx.android.synthetic.main.component_weather.view.progress
import kotlinx.android.synthetic.main.component_weather.view.rootCard
import kotlinx.android.synthetic.main.component_weather.view.tvErrText


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

    fun setWeatherError(errorMessage: String){
        refreshUI(null, errorMessage)
    }


    val states = arrayOf(
        intArrayOf(android.R.attr.state_enabled), // enabled
        intArrayOf(-android.R.attr.state_enabled), // disabled
        intArrayOf(-android.R.attr.state_checked), // unchecked
        intArrayOf(android.R.attr.state_pressed)  // pressed
    )

    fun setWeatherELoading(){
        progress.setVisible(true)



        val colors = intArrayOf(
            ContextCompat.getColor(context, R.color.white),
            ContextCompat.getColor(context, R.color.white),
            ContextCompat.getColor(context, R.color.white),
            ContextCompat.getColor(context, R.color.white),
        )

        val myList = ColorStateList(states, colors)
        rootCard.setCardBackgroundColor(myList)
    }

    private fun refreshUI(weatherResponse: WeatherResponse?, errorMessage: String? = null){

        layWeather.setVisible(weatherResponse!=null)
        layError.setVisible(weatherResponse == null)

        progress.setVisible(false)

        weatherResponse?.let {


            tvCity.text = weatherResponse.name

            val temperatureFloat = weatherResponse.main.temp

            var textColorRes = R.color.black

            var tempPref = ""
            if (temperatureFloat == 0.0f) {
                tempPref = ""
                textColorRes = R.color.blue_cold
            } else
                if (temperatureFloat > 0.0f) {
                    tempPref = "+"
                } else if (temperatureFloat < 0) {
                    tempPref = "-"
                    textColorRes
                }


            val tempStr = temperatureFloat.toInt().toString()

            val displayTemp = "$tempPref $tempStr °C"


            tvTemperature.text = displayTemp

            // tvTemperature.setTextColor(textColorRes)

            val weatherList = weatherResponse.weather

            if (weatherList.isNotEmpty()) {
                val weather = weatherList[0]
                val icon = weather.icon

                val iconUrl = "https://openweathermap.org/img/wn/${icon}@2x.png"
                Picasso.get()
                    .load(iconUrl)
                    .resize(120, 120)
                    .centerCrop()
                    .into(ivWeatherIcon)
            }
        }?: kotlin.run {
            tvErrText.text = errorMessage?:"Weather service isn't available"
        }


    }

    public fun setOnBack(l: OnClickListener?) {
        /* ivBackCard?.setOnClickListener {
             l?.onClick(it)
         }*/
    }


}


