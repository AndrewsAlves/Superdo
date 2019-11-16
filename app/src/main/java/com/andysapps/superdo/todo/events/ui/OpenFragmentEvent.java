package com.andysapps.superdo.todo.events.ui;

import androidx.fragment.app.Fragment;

/**
 * Created by Admin on 14,November,2019
 */
public class OpenFragmentEvent {

    public boolean behindMoonButton;
    public Fragment fragment;

    public OpenFragmentEvent(Fragment fragment, Boolean behindMoonButton) {
        this.fragment = fragment;
        this.behindMoonButton = behindMoonButton;
    }
}
