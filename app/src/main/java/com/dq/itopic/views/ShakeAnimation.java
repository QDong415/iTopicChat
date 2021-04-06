package com.dq.itopic.views;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by dongjin on 2017/11/23.
 */
public class ShakeAnimation {
    public ShakeAnimation with(@NonNull View view){
        this.view = view;
        return this;
    }

    public static ShakeAnimation create(){
        return new ShakeAnimation();
    }

    public void start(){

        if (view == null) throw new NullPointerException("View cant be null!");

        final ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(view, "translationX", 0, 25, -25, 25, -25,15, -15, 6, -6, 0);

        imageViewObjectAnimator.setDuration(duration);
        imageViewObjectAnimator.setRepeatMode(repeatMode);
        imageViewObjectAnimator.setRepeatCount(0);
        imageViewObjectAnimator.setInterpolator(new LinearInterpolator());
        imageViewObjectAnimator.start();
    }

    private int duration = 700;
    private int repeatMode = ValueAnimator.RESTART;
    private int repeatCount = RESTART;
    private View view;

    public static final int RESTART = 1;
    public static final int REVERSE = 2;
    public static final int INFINITE = -1;

    public ShakeAnimation setDuration(int duration){
        this.duration = duration;
        return this;
    }

    public ShakeAnimation setRepeatMode(int repeatMode){
        this.repeatMode = repeatMode;
        return this;
    }

    public ShakeAnimation setRepeatCount(int repeatCount){
        this.repeatCount = repeatCount;
        return this;
    }
}
