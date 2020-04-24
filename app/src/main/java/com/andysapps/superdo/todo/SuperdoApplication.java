package com.andysapps.superdo.todo;

import android.app.Application;
import android.content.Intent;

import com.andysapps.superdo.todo.activity.MainActivity;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.manager.PurchaseManager;
import com.andysapps.superdo.todo.manager.SharedPrefsManager;
import com.andysapps.superdo.todo.manager.SuperdoAudioManager;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.notification.SuperdoNotificationManager;
import com.andysapps.superdo.todo.notification.SuperdoAlarmManager;
import com.google.firebase.FirebaseApp;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Andrews on 30,October,2019
 */
public class SuperdoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
        FirestoreManager.initialise(this);
        PurchaseManager.initialise(this);

        TaskOrganiser.initialise();
        SuperdoAudioManager.init(this);
        SuperdoAlarmManager.initialise(this);
        SuperdoNotificationManager.initialise(this);
    }

}
