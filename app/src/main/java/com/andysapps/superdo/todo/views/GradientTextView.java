package com.andysapps.superdo.todo.views;


import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;

import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.andysapps.superdo.todo.R;

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

        //Setting the gradient if layout is changed
        if (changed) {
            getPaint().setShader(new LinearGradient(0, 0, getWidth(), getHeight(),
                    ContextCompat.getColor(getContext(), R.color.lightRed),
                    ContextCompat.getColor(getContext(), R.color.lightOrange),
                    Shader.TileMode.CLAMP));
        }
    }
}