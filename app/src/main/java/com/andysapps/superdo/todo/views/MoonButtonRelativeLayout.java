package com.andysapps.superdo.todo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

/**
 * Created by Andrews on 21,February,2020
 */

public class MoonButtonRelativeLayout extends RelativeLayout implements CoordinatorLayout.AttachedBehavior {
    public MoonButtonRelativeLayout(Context context) {
        super(context);
    }

    public MoonButtonRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoonButtonRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    @Override
    public CoordinatorLayout.Behavior getBehavior() {
        return new MoveUpwardBehavior();
    }
}
