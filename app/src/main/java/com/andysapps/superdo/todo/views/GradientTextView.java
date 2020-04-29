package com.andysapps.superdo.todo.views;


import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;

import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.andysapps.superdo.todo.R;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;

/**
 * Created by Andrews on 16/10/19.
 */

public class GradientTextView extends AppCompatTextView {

    public GradientTextView(Context context) {
        super(context);
    }

    public GradientTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GradientTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int colorStart = ContextCompat.getColor(getContext(), R.color.lightRed);
        int colorEnd = ContextCompat.getColor(getContext(), R.color.lightOrange);

        //Setting the gradient if layout is changed
        if (changed) {
            getPaint().setShader(new LinearGradient(0, 0, getWidth(), getHeight(),
                    colorStart,
                    colorEnd,
                    Shader.TileMode.CLAMP));
        }
    }

    public void setTextAndAnimate(String text) {
        ViewAnimator
                .animate(this)
                .alpha(1.0f,0f)
                .translationY(0.0f, 20f)
                .onStop(() -> {
                    setText(text);
                    showText();
                })
                .accelerate()
                .duration(500)
                .start();
    }

    public void showText() {
        ViewAnimator
                .animate(this)
                .alpha(0.0f,1.0f)
                .translationY(20f, 0f)
                .decelerate()
                .duration(500)
                .start();
    }
}