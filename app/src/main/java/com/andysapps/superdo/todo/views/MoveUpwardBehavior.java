package com.andysapps.superdo.todo.views;

import android.os.Build;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

/**
 * Created by Admin on 21,February,2020
 */
public class MoveUpwardBehavior extends CoordinatorLayout.Behavior<View> {
    private static final boolean SNACKBAR_BEHAVIOR_ENABLED;

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return SNACKBAR_BEHAVIOR_ENABLED && dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        return true;
    }

    static {
        SNACKBAR_BEHAVIOR_ENABLED = Build.VERSION.SDK_INT >= 11;
    }
}
