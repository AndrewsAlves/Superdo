package com.andysapps.superdo.todo.events;

import androidx.fragment.app.Fragment;

import com.andysapps.superdo.todo.model.Bucket;

/**
 * Created by Andrews on 16,March,2020
 */

public class SetTasksFragment {

    public Bucket bucket;

    public SetTasksFragment(Bucket bucket) {
        this.bucket = bucket;
    }
}
