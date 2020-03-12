package com.andysapps.superdo.todo;

import android.app.Application;

import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.notification.SuperdoNotificationManager;
import com.andysapps.superdo.todo.notification.SuperdoAlarmManager;
import com.google.firebase.FirebaseApp;

/**
 * Created by Andrews on 30,October,2019
 */
public class SuperdoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
        FirestoreManager.initialise(this);

        SuperdoAlarmManager.initialise(this);
        SuperdoAlarmManager.getInstance().registerBackgroundProcesses(this);
        SuperdoAlarmManager.getInstance().registerDailyNotificationAlarms(this);
        SuperdoNotificationManager.initialise(this);
    }
}
