package com.maestrovs.radiocar.ui.components

import android.content.Context
import android.content.res.Configuration
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.maestrovs.radiocar.R

class PlayPauseView : AppCompatImageView {
    private var mPlayToPauseAnim: AnimatedVectorDrawableCompat? = null
    private var mPauseToPlay: AnimatedVectorDrawableCompat? = null
    private var mFadeOutAnim: Animation? = null
    private var mFadeInAnim: Animation? = null

    private var changedConfiguration = true

    constructor(context: Context) : super(context) {
        Init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        Init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        Init(context)
    }

    private fun Init(context: Context) {
        mPlayToPauseAnim = AnimatedVectorDrawableCompat.create(context, R.drawable.play_to_pause)
        mPauseToPlay = AnimatedVectorDrawableCompat.create(context, R.drawable.pause_to_play)
        mFadeOutAnim = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
        mFadeInAnim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
    }

    fun switchState() {
        var newState = STATE_PAUSE
        newState = if (lastState == STATE_PLAY) {
            STATE_PAUSE
        } else {
            STATE_PLAY
        }
        when (newState) {
            STATE_PLAY -> {
                setImageDrawable(mPlayToPauseAnim)
                mPlayToPauseAnim!!.start()
            }

            STATE_PAUSE -> {
                setImageDrawable(mPauseToPlay)
                mPauseToPlay!!.start()
            }
        }
        lastState = newState
    }

    fun setState(state: Int) {

        if (state != lastState || changedConfiguration) {
            when (state) {
                STATE_PLAY -> {
                    setImageDrawable(mPlayToPauseAnim)
                    mPlayToPauseAnim!!.start()
                }

                STATE_PAUSE -> {
                    setImageDrawable(mPauseToPlay)
                    mPauseToPlay!!.start()
                }
            }
        }
        lastState = state
        changedConfiguration = false
    }

    fun fadeOut() {
        startAnimation(mFadeOutAnim)
        mFadeOutAnim!!.fillAfter = true
    }

    fun fadeIn() {
        startAnimation(mFadeInAnim)
        mFadeInAnim!!.fillAfter = true
    }

    companion object {
        const val STATE_PLAY = 1
        const val STATE_PAUSE = 2
        private var lastState = -1
    }


    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        changedConfiguration = true
    }


}