package com.andysapps.superdo.todo;

import android.app.Application;

import com.andysapps.superdo.todo.manager.FirestoreManager;

/**
 * Created by Admin on 30,October,2019
 */
public class SuperdoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirestoreManager.intialize(this);
    }
}
