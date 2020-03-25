package com.andysapps.superdo.todo.events.ui;

import androidx.fragment.app.Fragment;

/**
 * Created by Andrews on 14,November,2019
 */

public class OpenFragmentEvent {

    public boolean behindMoonButton;
    public Fragment fragment;
    public String tag;
    public boolean animate;

    public OpenFragmentEvent(Fragment fragment, Boolean behindMoonButton, String tag) {
        this.fragment = fragment;
        this.behindMoonButton = behindMoonButton;
        this.tag = tag;
    }

    public OpenFragmentEvent(Fragment fragment, Boolean behindMoonButton, String tag, boolean animate) {
        this.fragment = fragment;
        this.behindMoonButton = behindMoonButton;
        this.tag = tag;
        this.animate = animate;
    }
}
