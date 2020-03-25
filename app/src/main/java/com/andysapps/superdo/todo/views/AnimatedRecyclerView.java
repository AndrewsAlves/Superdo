package com.andysapps.superdo.todo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.andysapps.superdo.todo.R;

/**
 * Created by Admin on 20,March,2020
 */
public class AnimatedRecyclerView extends RecyclerView {
    public AnimatedRecyclerView(@NonNull Context context) {
        super(context);
    }

    public AnimatedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int resId = R.anim.layout_animation_from_left;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this.getContext(), resId);
        setLayoutAnimation(animation);
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
    }


}
