package com.maestrovs.radiocar.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.maestrovs.radiocar.R;

public class PlayPauseView extends AppCompatImageView {
    public static final int STATE_PLAY = 1;
    public static final int STATE_PAUSE = 2;

    private AnimatedVectorDrawableCompat mPlayToPauseAnim, mPauseToPlay;
    private Animation mFadeOutAnim, mFadeInAnim;

    public PlayPauseView(Context context) {
        super(context);
        Init(context);
    }

    public PlayPauseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public PlayPauseView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context);
    }

    private void Init(Context context) {
        mPlayToPauseAnim = AnimatedVectorDrawableCompat.create(context, R.drawable.play_to_pause);
        mPauseToPlay = AnimatedVectorDrawableCompat.create(context, R.drawable.pause_to_play);

        mFadeOutAnim = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        mFadeInAnim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
    }

    public void setState(int state) {
        switch (state) {
            case STATE_PLAY:
                this.setImageDrawable(mPlayToPauseAnim);
                mPlayToPauseAnim.start();
                break;

            case STATE_PAUSE:
                this.setImageDrawable(mPauseToPlay);
                mPauseToPlay.start();
                break;
        }
    }

    public void fadeOut() {
        this.startAnimation(mFadeOutAnim);
        mFadeOutAnim.setFillAfter(true);
    }

    public void fadeIn() {
        this.startAnimation(mFadeInAnim);
        mFadeInAnim.setFillAfter(true);
    }
}