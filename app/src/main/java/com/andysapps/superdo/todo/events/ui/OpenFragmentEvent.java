package com.andysapps.superdo.todo.events.ui;

import androidx.fragment.app.Fragment;

/**
 * Created by Admin on 14,November,2019
 */
public class OpenFragmentEvent {
    public Fragment fragment;

    public OpenFragmentEvent(Fragment fragment) {
        this.fragment = fragment;
    }
}
