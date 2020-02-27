package com.andysapps.superdo.todo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.model.notification_reminders.SimpleNotification;
import com.andysapps.superdo.todo.notification.NotificationManager;
import com.andysapps.superdo.todo.notification.SuperdoAlarmManager;

import static com.andysapps.superdo.todo.notification.SuperdoAlarmManager.CHANNEL_DAILY;
import static com.andysapps.superdo.todo.notification.SuperdoAlarmManager.intent_key_notification_id;

/**
 * Created by Andrews on 17,February,2020
 */

public class NotificationBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (SuperdoAlarmManager.getInstance() == null) {
            SuperdoAlarmManager.initialise(context);
        }

        if (intent.getExtras() == null) {
            return;
        }

        NotificationManager.initialise(context);

        if (FirestoreManager.getInstance() == null) {
            FirestoreManager.initialiseForNotification(context);
        }

        if (intent.getExtras().getString(intent_key_notification_id) != null) {
            NotificationManager.getInstance().postNotificationDaily(context, intent.getExtras().getString(intent_key_notification_id));
        }

    }
}
