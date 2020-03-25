package com.andysapps.superdo.todo.manager;

import android.view.View;

import com.github.florent37.viewanimator.ViewAnimator;

/**
 * Created by Andrews on 21,December,2019
 */

public class AnimationManager {

    private static final AnimationManager ourInstance = new AnimationManager();

    boolean isMoonButtonHidded;

    public static AnimationManager getInstance() {
        return ourInstance;
    }

    private AnimationManager() {
    }

    public void hideMoonButton(View view) {
        if (isMoonButtonHidded) {
            return;
        }

        ViewAnimator
                .animate(view)
                .scale(1, 0)
                .translationX(0, 80)
                .translationY(0, 50)
                .decelerate()
                .duration(200)
                .start();

        isMoonButtonHidded = true;
    }

    public void showMoonButton(View view) {
        if (!isMoonButtonHidded) {
            return;
        }

        ViewAnimator
                .animate(view)
                .scale(0, 1)
                .translationX(50, 0)
                .translationY(50, 0)
                .decelerate()
                .duration(200)
                .start();
        isMoonButtonHidded = false;
    }

    public void scaleUp(View view) {
        ViewAnimator
                .animate(view)
                .scale(0, 1)
                .decelerate()
                .duration(100)
                .start();
    }
}
