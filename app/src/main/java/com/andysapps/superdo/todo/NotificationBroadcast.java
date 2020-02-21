package com.andysapps.superdo.todo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.andysapps.superdo.todo.notification.SuperdoAlarmManager;

/**
 * Created by Andrews on 17,February,2020
 */

public class NotificationBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (SuperdoAlarmManager.getInstance() == null) {
            SuperdoAlarmManager.initialise(context);
        }

        SuperdoAlarmManager.getInstance().createNotification(context, SuperdoAlarmManager.CHANNEL_DAILY, "Superod notification", "Context jjajjajajjaja", " you are amazing" );

    }
}
