package com.maestrovs.radiocar.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.shared_managers.SpeedUnit
import kotlin.math.round


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


    fun setSpeedKmh(speedKmh: Float, speedUnit: SpeedUnit){
        when(speedUnit){
            SpeedUnit.kmh ->  refreshUI(speedKmh)
            SpeedUnit.mph -> refreshUI(convertToMph(speedKmh).toFloat())
        }

    }

    private fun convertToMph(kmh: Float) = kmh*0.6213711922

    fun setUnit(unit: String){
        tvUnit.text = "$unit"
    }

    private fun refreshUI(speed: Float){

        val speedStr = if(speed<2.0) { "0" }else{round(speed)}
        tvSpeed.text = "$speedStr"


    }



}


