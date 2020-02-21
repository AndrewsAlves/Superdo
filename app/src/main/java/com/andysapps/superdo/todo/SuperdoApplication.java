package com.andysapps.superdo.todo;

import android.app.Application;

import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.notification.SuperdoAlarmManager;
import com.google.firebase.FirebaseApp;

/**
 * Created by Admin on 30,October,2019
 */
public class SuperdoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
        FirestoreManager.intialize(this);
        SuperdoAlarmManager.initialise(this);
        SuperdoAlarmManager.getInstance().createNotificationChannels(this);
        SuperdoAlarmManager.getInstance().registerDailyNotificationAlarms(this);
        SuperdoAlarmManager.getInstance().testNotification(this);

    }
}
